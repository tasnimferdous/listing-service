package com.tasnim.listingservice.service.impl;

import com.tasnim.commonlibrary.exceptions.BusinessException;
import com.tasnim.commonlibrary.exceptions.ResourceNotFoundException;
import com.tasnim.commonlibrary.utils.SecurityUtil;
import com.tasnim.listingservice.dtos.request.ListingRejectionRequest;
import com.tasnim.listingservice.dtos.response.ListingDetailsResponse;
import com.tasnim.listingservice.dtos.response.ListingResponse;
import com.tasnim.listingservice.entity.Category;
import com.tasnim.listingservice.entity.Listing;
import com.tasnim.listingservice.enums.ListingStatus;
import com.tasnim.listingservice.mapper.ListingMapper;
import com.tasnim.listingservice.repository.ListingRepository;
import com.tasnim.listingservice.service.AdminListingService;
import com.tasnim.listingservice.service.CategoryService;
import com.tasnim.listingservice.service.ListingImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

import static com.tasnim.listingservice.utils.ListingUtil.*;

@Slf4j
@Service
@Transactional
public class AdminListingServiceImpl implements AdminListingService {
    private final ListingRepository listingRepository;
    private final CategoryService categoryService;
    private final ListingImageService listingImageService;
    private final ListingMapper listingMapper;

    public AdminListingServiceImpl(
            ListingRepository listingRepository,
            CategoryService categoryService,
            ListingImageService listingImageService, ListingMapper listingMapper) {
        this.listingRepository = listingRepository;
        this.categoryService = categoryService;
        this.listingImageService = listingImageService;
        this.listingMapper = listingMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ListingResponse> getListingsByStatus(
            ListingStatus status,
            int page,
            int size,
            String sortBy,
            String direction) {
        log.info("Fetching listings. status={}, page={}, size={}, sortBy={}, direction={}",
                status, page, size, sortBy, direction);

        validateSorting(sortBy, direction);
        Pageable pageable = buildPageable(page, size, sortBy, direction);

        return listingRepository
                .findByStatus(status, pageable)
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

    @Override
    public void approveListing(Long listingId) {
        String adminId = SecurityUtil.getCurrentUserId();

        log.info("Approve listing. id={}, adminId={}", listingId, adminId);

        Listing listing = getListing(listingId);
        validateAction(listing);

        listing.setStatus(ListingStatus.SCHEDULED);
        listing.setApprovedBy(adminId);
        listing.setApprovedAt(Instant.now());
        listing.setRejectedBy(null);
        listing.setRejectedAt(null);
        listing.setRejectionReason(null);
        listingRepository.save(listing);

        log.info("Listing approved successfully. id={}, adminId={}", listingId, adminId);
    }

    @Override
    public void rejectListing(Long listingId, ListingRejectionRequest request) {
        String adminId = SecurityUtil.getCurrentUserId();

        log.info("Reject listing. id={}, adminId={}", listingId, adminId);

        Listing listing = getListing(listingId);
        validateAction(listing);

        listing.setStatus(ListingStatus.REJECTED);
        listing.setRejectedBy(adminId);
        listing.setRejectedAt(Instant.now());
        listing.setRejectionReason(request.getReason());
        listing.setApprovedBy(null);
        listing.setApprovedAt(null);
        listingRepository.save(listing);

        log.info("Listing rejected successfully. id={}, adminId={}", listingId, adminId);
    }

    private void validateAction(Listing listing) {
        if (listing.getStatus() != ListingStatus.PENDING) {
            throw new BusinessException(
                    "Only pending listings can be moderated");
        }
    }

    private Listing getListing(Long listingId) {
        return listingRepository.findById(listingId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Listing not found with id: " + listingId));
    }
}