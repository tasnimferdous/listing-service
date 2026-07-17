package com.tasnim.listingservice.service.impl;

import com.tasnim.commonlibrary.exceptions.BadRequestException;
import com.tasnim.commonlibrary.exceptions.ResourceNotFoundException;
import com.tasnim.listingservice.dtos.response.ListingDetailsResponse;
import com.tasnim.listingservice.dtos.response.ListingResponse;
import com.tasnim.listingservice.entity.Category;
import com.tasnim.listingservice.entity.Listing;
import com.tasnim.listingservice.enums.ListingStatus;
import com.tasnim.listingservice.mapper.ListingMapper;
import com.tasnim.listingservice.repository.ListingRepository;
import com.tasnim.listingservice.service.CategoryService;
import com.tasnim.listingservice.service.ListingImageService;
import com.tasnim.listingservice.service.PublicListingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.tasnim.listingservice.utils.Constants.PUBLIC_STATUSES;
import static com.tasnim.listingservice.utils.ListingUtil.*;

@Service
@Slf4j
@Transactional(readOnly = true)
public class PublicListingServiceImpl implements PublicListingService {
    private final ListingRepository listingRepository;
    private final CategoryService categoryService;
    private final ListingImageService listingImageService;
    private final ListingMapper listingMapper;

    public PublicListingServiceImpl(
            ListingRepository listingRepository,
            CategoryService categoryService,
            ListingImageService listingImageService, ListingMapper listingMapper) {

        this.listingRepository = listingRepository;
        this.categoryService = categoryService;
        this.listingImageService = listingImageService;
        this.listingMapper = listingMapper;
    }

    @Override
    public Page<ListingResponse> getListings(ListingStatus status, int page, int size, String sortBy, String direction) {
        log.info("Fetching listings. status={}, page={}, size={}, sortBy={}, direction={}",
                status, page, size, sortBy, direction);

        validateSorting(sortBy, direction);
        Pageable pageable = buildPageable(page, size, sortBy, direction);

        if (status != null) {
            validateStatusFilter(status);

            return listingRepository
                    .findByStatus(status, pageable)
                    .map(listingMapper::toListingResponse);
        }

        return listingRepository
                .findByStatus(ListingStatus.LIVE, pageable)
                .map(listingMapper::toListingResponse);
    }

    @Override
    public ListingDetailsResponse getListingDetails(Long listingId) {
        log.info("Fetching listing details. id={}", listingId);

        Listing listing = getListing(listingId);
        validatePublicVisibility(listing);
        List<String> imageUrls = listingImageService
                .findImagesByListingIdInOrder(listing.getId());
        Category category = categoryService
                .getCategoryById(listing.getCategoryId());

        return listingMapper.toListingDetailsResponse(listing, category.getName(), imageUrls);
    }

    @Override
    public Page<ListingResponse> getListingsByCategory(Long categoryId, ListingStatus status, int page, int size) {
        log.info("Fetching listings by category. categoryId={}, status={}", categoryId, status);

        categoryService.validateCategory(categoryId);
        Pageable pageable = PageRequest.of(page, size);

        if (status != null) {
            validateStatusFilter(status);

            return listingRepository
                    .findByCategoryIdAndStatus(
                            categoryId,
                            status,
                            pageable)
                    .map(listingMapper::toListingResponse);
        }

        return listingRepository
                .findByCategoryIdAndStatus(
                        categoryId,
                        ListingStatus.LIVE,
                        pageable)
                .map(listingMapper::toListingResponse);
    }

    @Override
    public Page<ListingResponse> searchListings(String keyword, ListingStatus status, int page, int size) {
        keyword = validateKeyword(keyword);

        log.info("Searching listings. keyword={}, status={}, page={}, size={}", keyword, status, page, size);

        Pageable pageable = PageRequest.of(page, size);

        if (status != null) {
            validateStatusFilter(status);

            return listingRepository
                    .searchListings(
                            keyword,
                            status,
                            pageable
                    )
                    .map(listingMapper::toListingResponse);
        }

        return listingRepository
                .searchListings(
                        keyword,
                        List.of(
                                ListingStatus.SCHEDULED,
                                ListingStatus.LIVE
                        ),
                        pageable
                )
                .map(listingMapper::toListingResponse);
    }

    private Listing getListing(Long listingId) {
        return listingRepository.findById(listingId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Listing not found with id: " + listingId));
    }

    private void validatePublicVisibility(Listing listing) {
        if (!PUBLIC_STATUSES.contains(listing.getStatus())) {
            throw new ResourceNotFoundException(
                    "Listing not found");
        }
    }

    private void validateStatusFilter(ListingStatus status) {
        if (!PUBLIC_STATUSES.contains(status)) {
            throw new BadRequestException(
                    "Invalid status filter");
        }
    }

    private String validateKeyword(String keyword) {
        if (keyword == null) {
            throw new BadRequestException("Keyword cannot be empty");
        }
        keyword = keyword.trim();
        if (keyword.isBlank()) {
            throw new BadRequestException("Keyword cannot be empty");
        }
        if (keyword.length() > 100) {
            throw new BadRequestException("Keyword is too long");
        }

        return keyword;
    }
}
