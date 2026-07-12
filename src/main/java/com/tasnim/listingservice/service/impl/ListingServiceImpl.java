package com.tasnim.listingservice.service.impl;

import com.tasnim.commonlibrary.exceptions.BusinessException;
import com.tasnim.commonlibrary.exceptions.ForbiddenException;
import com.tasnim.commonlibrary.exceptions.ResourceNotFoundException;
import com.tasnim.commonlibrary.utils.SecurityUtil;
import com.tasnim.listingservice.dtos.request.ListingCreateRequest;
import com.tasnim.listingservice.dtos.request.ListingUpdateRequest;
import com.tasnim.listingservice.dtos.response.ListingDetailsResponse;
import com.tasnim.listingservice.dtos.response.ListingResponse;
import com.tasnim.listingservice.entity.Category;
import com.tasnim.listingservice.entity.Listing;
import com.tasnim.listingservice.enums.ListingStatus;
import com.tasnim.listingservice.repository.ListingRepository;
import com.tasnim.listingservice.service.CategoryService;
import com.tasnim.listingservice.service.ListingImageService;
import com.tasnim.listingservice.service.ListingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.tasnim.commonlibrary.utils.PublicUtil.isNullOrEmpty;

@Slf4j
@Service
@Transactional
public class ListingServiceImpl implements ListingService {
    private final ListingRepository listingRepository;
    private final ListingImageService listingImageService;
    private final CategoryService categoryService;

    public ListingServiceImpl(ListingRepository listingRepository, ListingImageService listingImageService, CategoryService categoryService) {
        this.listingRepository = listingRepository;
        this.listingImageService = listingImageService;
        this.categoryService = categoryService;
    }

    @Override
    public ListingResponse createListing(ListingCreateRequest request) {
        log.info("Creating listing with title={}", request.getTitle());

        categoryService.validateCategory(request.getCategoryId());
        Listing listing = buildListing(request);
        listing = listingRepository.save(listing);
        listingImageService.storeListingImages(request.getImageUrls(), listing.getId());
        log.info("Listing created successfully. id={}", listing.getId());

        return mapToListingResponse(listing);
    }

    private Listing buildListing(ListingCreateRequest request) {
        return Listing.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .listingCondition(request.getCondition().toString())
                .startingPrice(request.getStartingPrice())
                .reservePrice(request.getReservePrice())
                .buyNowPrice(request.getBuyNowPrice())
                .categoryId(request.getCategoryId())
                .status(ListingStatus.PENDING_APPROVAL)
                .sellerId(SecurityUtil.getCurrentUserId())
                .build();
    }

    @Override
    public ListingResponse updateListing(Long listingId, ListingUpdateRequest request) {
        String sellerId = SecurityUtil.getCurrentUserId();
        log.info("Updating listing. id={}, sellerId={}", listingId, sellerId);

        Listing listing = getListing(listingId);
        validateListingEditable(request, listing,  sellerId);
        updateListingFields(listing, request);
        updateListingImages(listingId, request.getImageUrls());
        listing = listingRepository.save(listing);

        log.info("Listing updated successfully. id={}", listingId);

        return mapToListingResponse(listing);
    }

    private void updateListingImages(Long listingId, List<String> imageUrls) {
        if (imageUrls == null || imageUrls.isEmpty()) return;
        listingImageService.deleteImagesByListingId(listingId);
        listingImageService.storeListingImages(imageUrls, listingId);
    }

    private void validateListingEditable(ListingUpdateRequest request, Listing listing, String sellerId) {
        validateStatus(listing);
        validateOwnership(listing, sellerId);
        if(request.getCategoryId() != null) {
            categoryService.validateCategory(request.getCategoryId());
        }
    }

    private void validateOwnership(Listing listing, String sellerId) {
        if (!listing.getSellerId().equals(sellerId)) {
            throw new ForbiddenException(
                    "You do not own this listing");
        }
    }

    private void updateListingFields(Listing listing, ListingUpdateRequest request) {
        listing.setTitle(isNullOrEmpty(request.getTitle()) ? listing.getTitle() : request.getTitle());
        listing.setDescription(isNullOrEmpty(request.getDescription()) ? listing.getDescription() : request.getDescription());
        listing.setListingCondition(request.getCondition() == null ? listing.getListingCondition() : request.getCondition().toString());
        listing.setStartingPrice(request.getStartingPrice() == null ? listing.getStartingPrice() : request.getStartingPrice());
        listing.setReservePrice(request.getReservePrice() == null ? listing.getReservePrice() : request.getReservePrice());
        listing.setBuyNowPrice(request.getBuyNowPrice() == null ? listing.getBuyNowPrice() : request.getBuyNowPrice());
        listing.setCategoryId(request.getCategoryId() ==  null ? listing.getCategoryId() : request.getCategoryId());
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
        String sellerId = SecurityUtil.getCurrentUserId();
        log.info("Deleting listing. id={}, sellerId={}", listingId, sellerId);

        Listing listing = getListing(listingId);
        validateListingDeletion(listing,  sellerId);
        listingImageService.deleteImagesByListingId(listingId);
        listingRepository.delete(listing);

        log.info("Listing deleted successfully. id={}", listingId);
    }

    private void validateListingDeletion(Listing listing, String sellerId) {
        validateStatus(listing);
        validateOwnership(listing, sellerId);
    }

    private void validateStatus(Listing listing) {
        if (ListingStatus.LIVE.equals(listing.getStatus()) ||
                ListingStatus.ENDED.equals(listing.getStatus())) {
            throw new BusinessException(
                    "Listing cannot be updated or deleted in current status");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ListingResponse> getMyListings() {
        String sellerId = SecurityUtil.getCurrentUserId();

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
                .startingPrice(listing.getStartingPrice())
                .reservePrice(listing.getReservePrice())
                .buyNowPrice(listing.getBuyNowPrice())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ListingDetailsResponse getListingDetails(Long listingId) {
        log.info("Fetching listing details. id={}", listingId);

        Listing listing = getListing(listingId);

        return mapToListingDetailsResponse(listing);
    }

    private ListingDetailsResponse mapToListingDetailsResponse(Listing listing) {
        List<String> imageUrls = listingImageService
                .findImagesByListingIdInOrder(listing.getId());
        Category category = categoryService
                .getCategoryById(listing.getCategoryId());

        return ListingDetailsResponse.builder()
                .id(listing.getId())
                .title(listing.getTitle())
                .description(listing.getDescription())
                .condition(listing.getListingCondition())
                .category(category == null? "Uncategorized" : category.getName())
                .startingPrice(listing.getStartingPrice())
                .reservePrice(listing.getReservePrice())
                .buyNowPrice(listing.getBuyNowPrice())
                .status(listing.getStatus())
                .images(imageUrls)
                .build();
    }
}
