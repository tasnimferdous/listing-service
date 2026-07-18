package com.tasnim.listingservice.service.impl;

import com.tasnim.commonlibrary.exceptions.BadRequestException;
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
import com.tasnim.listingservice.mapper.ListingMapper;
import com.tasnim.listingservice.repository.ListingRepository;
import com.tasnim.listingservice.service.CategoryService;
import com.tasnim.listingservice.service.ListingImageService;
import com.tasnim.listingservice.service.ListingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static com.tasnim.commonlibrary.utils.PublicUtil.isNullOrEmpty;
import static com.tasnim.listingservice.utils.Constants.*;
import static com.tasnim.listingservice.utils.ListingUtil.*;

@Slf4j
@Service
@Transactional
public class ListingServiceImpl implements ListingService {
    private final ListingRepository listingRepository;
    private final ListingImageService listingImageService;
    private final CategoryService categoryService;
    private final ListingMapper listingMapper;

    public ListingServiceImpl(ListingRepository listingRepository, ListingImageService listingImageService, CategoryService categoryService, ListingMapper listingMapper) {
        this.listingRepository = listingRepository;
        this.listingImageService = listingImageService;
        this.categoryService = categoryService;
        this.listingMapper = listingMapper;
    }

    @Override
    public ListingResponse createListing(ListingCreateRequest request) {
        validateAuctionTime(request.getAuctionStartTime(), request.getAuctionEndTime());

        log.info("Creating listing with title={}", request.getTitle());

        categoryService.validateCategory(request.getCategoryId());
        Listing listing = buildListing(request);
        listing = listingRepository.save(listing);
        listingImageService.storeListingImages(request.getImageUrls(), listing.getId());
        log.info("Listing created successfully. id={}", listing.getId());

        return listingMapper.toListingResponse(listing);
    }

    @Override
    public ListingResponse updateListing(Long listingId, ListingUpdateRequest request) {
        validateAuctionTimeUpdate(request);
        String sellerId = SecurityUtil.getCurrentUserId();
        log.info("Updating listing. id={}, sellerId={}", listingId, sellerId);

        Listing listing = getListing(listingId);
        validateListingEditable(request, listing,  sellerId);
        updateListingFields(listing, request);
        updateListingImages(listingId, request.getImageUrls());
        listing = listingRepository.save(listing);

        log.info("Listing updated successfully. id={}", listingId);

        return listingMapper.toListingResponse(listing);
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

    @Override
    @Transactional(readOnly = true)
    public Page<ListingResponse> getMyListings(ListingStatus status, int page, int size, String sortBy, String direction) {
        String sellerId = SecurityUtil.getCurrentUserId();

        log.info("Fetching seller listings. sellerId={}, status={}, page={}, size={}, sortBy={}, direction={}",
                sellerId, status, page, size, sortBy, direction);

        validateSorting(sortBy, direction);
        Pageable pageable = buildPageable(page, size, sortBy, direction);

        if (status != null) {
            return listingRepository
                    .findBySellerIdAndStatus(
                            sellerId,
                            status,
                            pageable)
                    .map(listingMapper::toListingResponse);
        }

        return listingRepository
                .findBySellerId(
                        sellerId,
                        pageable)
                .map(listingMapper::toListingResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ListingDetailsResponse getListingDetails(Long listingId) {
        log.info("Fetching listing details. id={}", listingId);

        Listing listing = getListing(listingId);
        List<String> imageUrls = listingImageService
                .findImagesByListingIdInOrder(listing.getId());
        Category category = categoryService
                .getCategoryById(listing.getCategoryId());

        return listingMapper.toListingDetailsResponse(listing, category.getName(), imageUrls);
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
                .status(ListingStatus.PENDING)
                .sellerId(SecurityUtil.getCurrentUserId())
                .build();
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
        listing.setAuctionStartTime(request.getAuctionStartTime() == null ? listing.getAuctionStartTime() : request.getAuctionStartTime());
        listing.setAuctionEndTime(request.getAuctionEndTime() == null ? listing.getAuctionEndTime() : request.getAuctionEndTime());
    }

    private Listing getListing(Long listingId) {
        return listingRepository.findById(listingId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Listing not found with id: " + listingId
                        ));
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

    private void validateAuctionTimeUpdate(ListingUpdateRequest request) {
        Instant startTime = request.getAuctionStartTime();
        Instant endTime = request.getAuctionEndTime();

        if ((startTime == null) != (endTime == null)) {
            throw new BadRequestException(
                    "Auction start time and end time must be provided together");
        }
        if (startTime != null) {
            validateAuctionTime(startTime, endTime);
        }
    }

    private void validateAuctionTime(Instant startTime, Instant endTime) {
        Instant now = Instant.now();
        if (!startTime.isAfter(now.plus(MIN_START_TIME_OFFSET))) {
            throw new BadRequestException(
                    "Auction start time must be at least %d hour(s) in the future"
                            .formatted(MIN_START_TIME_OFFSET.toHours()));
        }
        if (!endTime.isAfter(startTime)) {
            throw new BadRequestException(
                    "Auction end time must be after auction start time");
        }

        Duration duration = Duration.between(startTime, endTime);
        if (duration.compareTo(MIN_AUCTION_DURATION) < 0) {
            throw new BadRequestException(
                    "Auction duration must be at least %d hour(s)"
                            .formatted(MIN_AUCTION_DURATION.toHours()));
        }
        if (duration.compareTo(MAX_AUCTION_DURATION) > 0) {
            throw new BadRequestException(
                    "Auction duration cannot exceed %d day(s)"
                            .formatted(MAX_AUCTION_DURATION.toDays()));
        }
    }
}
