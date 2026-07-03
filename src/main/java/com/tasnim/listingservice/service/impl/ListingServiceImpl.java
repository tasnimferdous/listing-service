package com.tasnim.listingservice.service.impl;

import com.tasnim.listingservice.dtos.request.ListingCreateRequest;
import com.tasnim.listingservice.dtos.request.ListingUpdateRequest;
import com.tasnim.listingservice.dtos.response.ListingDetailsResponse;
import com.tasnim.listingservice.dtos.response.ListingResponse;
import com.tasnim.listingservice.entity.Listing;
import com.tasnim.listingservice.enums.ListingStatus;
import com.tasnim.listingservice.exception.BusinessException;
import com.tasnim.listingservice.exception.ResourceNotFoundException;
import com.tasnim.listingservice.repository.CategoryRepository;
import com.tasnim.listingservice.repository.ListingRepository;
import com.tasnim.listingservice.service.ListingService;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.tasnim.listingservice.utils.PublicUtil.isNullOrEmpty;

@Slf4j
@Service
public class ListingServiceImpl implements ListingService {
    private final ListingRepository listingRepository;
    private final CategoryRepository categoryRepository;

    public ListingServiceImpl(ListingRepository listingRepository, CategoryRepository categoryRepository) {
        this.listingRepository = listingRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public ListingResponse createListing(ListingCreateRequest request) {
        log.info("Creating listing with title={}", request.getTitle());

        validateCategory(request.getCategoryId());
        Listing listing = buildListing(request);
        listing = listingRepository.save(listing);

        storeListingImages(request.getImageUrls(), listing.getId());

        log.info("Listing created successfully. id={}", listing.getId());

        return mapToListingResponse(listing);
    }

    //Need to work on it
    private void storeListingImages(@Size(max = 10, message = "Maximum 10 images allowed") List<String> imageUrls, Long id) {

    }

    private void validateCategory(@NotNull(message = "Category is required") Long categoryId) {
        categoryRepository.findById(categoryId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category not found with id: " + categoryId
                        ));
    }

    private Listing buildListing(ListingCreateRequest request) {
        return Listing.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .condition(request.getCondition())
                .startingPrice(request.getStartingPrice())
                .reservePrice(request.getReservePrice())
                .buyNowPrice(request.getBuyNowPrice())
                .category(request.getCategoryId())
                .status(ListingStatus.PENDING_APPROVAL)
//                .sellerId()  //for later. need to get it from jwt
                .build();
    }

    @Override
    public ListingResponse updateListing(Long listingId, ListingUpdateRequest request) {
        log.info("Updating listing. id={}", listingId);

        if(request.getCategoryId() != null) {
            validateCategory(request.getCategoryId());
        }
        Listing listing = getListing(listingId);
        updateListingFields(listing, request);
        listing = listingRepository.save(listing);

        log.info("Listing updated successfully. id={}", listingId);

        return mapToListingResponse(listing);
    }

    private void updateListingFields(Listing listing, ListingUpdateRequest request) {
        listing.setTitle(isNullOrEmpty(request.getTitle()) ? listing.getTitle() : request.getTitle());
        listing.setDescription(isNullOrEmpty(request.getDescription()) ? listing.getDescription() : request.getDescription());
        listing.setCondition(isNullOrEmpty(request.getCondition()) ? listing.getCondition() : request.getCondition());
        listing.setStartingPrice(request.getStartingPrice() == null ? listing.getStartingPrice() : request.getStartingPrice());
        listing.setReservePrice(request.getReservePrice() == null ? listing.getReservePrice() : request.getReservePrice());
        listing.setBuyNowPrice(request.getBuyNowPrice() == null ? listing.getBuyNowPrice() : request.getBuyNowPrice());
        listing.setCategory(request.getCategoryId() ==  null ? listing.getCategory() : request.getCategoryId());
    }

    private Listing getListing(Long listingId) {
        return listingRepository.findById(listingId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Listing not found with id: " + listingId
                        ));
    }

    @Override
    public void deleteListing(Long listingId) {
        log.info("Deleting listing. id={}", listingId);

        Listing listing = getListing(listingId);
        validateDeletionAllowed(listing);
        //delete related images
        listingRepository.delete(listing);

        log.info("Listing deleted successfully. id={}", listingId);
    }

    private void validateDeletionAllowed(Listing listing) {
        if (ListingStatus.ACTIVE.equals(listing.getStatus())) {
            throw new BusinessException(
                    "Active listing can't be deleted");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ListingResponse> getMyListings() {
        Long sellerId = getCurrentSellerId();

        log.info("Fetching listings for seller={}", sellerId);

        return listingRepository.findBySellerId(sellerId)
                .stream()
                .map(this::mapToListingResponse)
                .toList();
    }

    private ListingResponse mapToListingResponse(Listing listing) {
        return ListingResponse.builder()
                .id(listing.getId())
                .title(listing.getTitle())
                .status(listing.getStatus())
                .build();
    }

    private Long getCurrentSellerId() {
        // Implementation for getting current seller ID from JWT or other source
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public ListingDetailsResponse getListingDetails(Long listingId) {
        log.info("Fetching listing details. id={}", listingId);

        Listing listing = getListing(listingId);

        return mapToListingDetailsResponse(listing);
    }

    private ListingDetailsResponse mapToListingDetailsResponse(Listing listing) {
        //get and set images
        return ListingDetailsResponse.builder()
                .id(listing.getId())
                .title(listing.getTitle())
                .description(listing.getDescription())
                .condition(listing.getCondition())
                .categoryId(listing.getCategory())
                .startingPrice(listing.getStartingPrice())
                .reservePrice(listing.getReservePrice())
                .buyNowPrice(listing.getBuyNowPrice())
                .status(listing.getStatus())
                .build();
    }
}
