package com.tasnim.listingservice.service.impl;

import com.tasnim.commonlibrary.exceptions.BusinessException;
import com.tasnim.commonlibrary.exceptions.ForbiddenException;
import com.tasnim.commonlibrary.exceptions.ResourceNotFoundException;
import com.tasnim.commonlibrary.utils.SecurityUtil;
import com.tasnim.listingservice.dtos.request.ListingCreateRequest;
import com.tasnim.listingservice.dtos.request.ListingUpdateRequest;
import com.tasnim.listingservice.dtos.response.ListingDetailsResponse;
import com.tasnim.listingservice.dtos.response.ListingResponse;
import com.tasnim.listingservice.entity.Listing;
import com.tasnim.listingservice.entity.ListingImage;
import com.tasnim.listingservice.enums.ListingStatus;
import com.tasnim.listingservice.repository.CategoryRepository;
import com.tasnim.listingservice.repository.ListingImageRepository;
import com.tasnim.listingservice.repository.ListingRepository;
import com.tasnim.listingservice.service.ListingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static com.tasnim.listingservice.utils.PublicUtil.isNullOrEmpty;

@Slf4j
@Service
@Transactional
public class ListingServiceImpl implements ListingService {
    private final ListingRepository listingRepository;
    private final CategoryRepository categoryRepository;
    private final ListingImageRepository listingImageRepository;

    public ListingServiceImpl(ListingRepository listingRepository, CategoryRepository categoryRepository, ListingImageRepository listingImageRepository) {
        this.listingRepository = listingRepository;
        this.categoryRepository = categoryRepository;
        this.listingImageRepository = listingImageRepository;
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

    private void storeListingImages(List<String> imageUrls, Long listingId) {
        log.info("Storing listing image for listingId={}", listingId);

        List<ListingImage> images = new ArrayList<>();
        for (int i = 0; i < imageUrls.size(); i++) {
            images.add(
                    ListingImage.builder()
                            .listingId(listingId)
                            .imageUrl(imageUrls.get(i))
                            .displayOrder(i + 1)
                            .build()
            );
        }

        listingImageRepository.saveAll(images);
    }

    private void validateCategory(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException(
                    "Category not found with id: " + categoryId);
        }
    }

    private Listing buildListing(ListingCreateRequest request) {
        return Listing.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .listingCondition(request.getCondition().toString())
                .startingPrice(request.getStartingPrice())
                .reservePrice(request.getReservePrice())
                .buyNowPrice(request.getBuyNowPrice())
                .category(request.getCategoryId())
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
        deleteListingImages(listingId);
        storeListingImages(imageUrls, listingId);
    }

    private void validateListingEditable(ListingUpdateRequest request, Listing listing, String sellerId) {
        validateStatus(listing);
        validateOwnership(listing, sellerId);
        if(request.getCategoryId() != null) {
            validateCategory(request.getCategoryId());
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
        String sellerId = SecurityUtil.getCurrentUserId();
        log.info("Deleting listing. id={}, sellerId={}", listingId, sellerId);

        Listing listing = getListing(listingId);
        validateListingDeletion(listing,  sellerId);
        deleteListingImages(listingId);
        listingRepository.delete(listing);

        log.info("Listing deleted successfully. id={}", listingId);
    }

    private void deleteListingImages(Long listingId) {
        log.info("Deleting images for listingId={}", listingId);

        List<ListingImage> images = listingImageRepository.findByListingId(listingId);
        if (images == null || images.isEmpty())  return;
        deletePhysicalImages(images);
        listingImageRepository.deleteByListingId(listingId);

        log.info("Images deleted for listingId={}", listingId);
    }

    private void deletePhysicalImages(List<ListingImage> images) {
        for (ListingImage image : images) {
            try {
                String fileName = image.getImageUrl().replace("/uploads/", "");
                Path filePath = Paths.get("uploads").resolve(fileName);
                Files.deleteIfExists(filePath);
            } catch (IOException ex) {
                log.error(
                        "Failed to delete image. url={}",
                        image.getImageUrl(), ex);
            }
        }
    }

    private void validateListingDeletion(Listing listing, String sellerId) {
        validateStatus(listing);
        validateOwnership(listing, sellerId);
    }

    private void validateStatus(Listing listing) {
        if (ListingStatus.ACTIVE.equals(listing.getStatus()) ||
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
        //get and set images
        return ListingDetailsResponse.builder()
                .id(listing.getId())
                .title(listing.getTitle())
                .description(listing.getDescription())
                .condition(listing.getListingCondition())
                .categoryId(listing.getCategory())
                .startingPrice(listing.getStartingPrice())
                .reservePrice(listing.getReservePrice())
                .buyNowPrice(listing.getBuyNowPrice())
                .status(listing.getStatus())
                .build();
    }
}
