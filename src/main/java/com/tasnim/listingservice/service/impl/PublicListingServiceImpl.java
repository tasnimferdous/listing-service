package com.tasnim.listingservice.service.impl;

import com.tasnim.commonlibrary.exceptions.BadRequestException;
import com.tasnim.commonlibrary.exceptions.ResourceNotFoundException;
import com.tasnim.listingservice.dtos.response.ListingDetailsResponse;
import com.tasnim.listingservice.dtos.response.ListingResponse;
import com.tasnim.listingservice.entity.Category;
import com.tasnim.listingservice.entity.Listing;
import com.tasnim.listingservice.enums.ListingStatus;
import com.tasnim.listingservice.repository.ListingRepository;
import com.tasnim.listingservice.service.CategoryService;
import com.tasnim.listingservice.service.ListingImageService;
import com.tasnim.listingservice.service.PublicListingService;
import com.tasnim.listingservice.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@Transactional(readOnly = true)
public class PublicListingServiceImpl implements PublicListingService {
    private final ListingRepository listingRepository;
    private final CategoryService categoryService;
    private final ListingImageService listingImageService;

    public PublicListingServiceImpl(
            ListingRepository listingRepository,
            CategoryService categoryService,
            ListingImageService listingImageService) {

        this.listingRepository = listingRepository;
        this.categoryService = categoryService;
        this.listingImageService = listingImageService;
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
                    .map(this::mapToListingResponse);
        }

        return listingRepository
                .findByStatus(ListingStatus.LIVE, pageable)
                .map(this::mapToListingResponse);
    }

    @Override
    public ListingDetailsResponse getListingDetails(Long listingId) {
        log.info("Fetching listing details. id={}", listingId);

        Listing listing = getListing(listingId);
        validatePublicVisibility(listing);

        return mapToListingDetailsResponse(listing);
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
                    .map(this::mapToListingResponse);
        }

        return listingRepository
                .findByCategoryIdAndStatus(
                        categoryId,
                        ListingStatus.LIVE,
                        pageable)
                .map(this::mapToListingResponse);
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
                    .map(this::mapToListingResponse);
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
                .map(this::mapToListingResponse);
    }

    private Pageable buildPageable(int page, int size, String sortBy, String direction) {
        Sort sort = Sort.by(sortBy);
        sort = "desc".equalsIgnoreCase(direction)
                ? sort.descending()
                : sort.ascending();

        return PageRequest.of(page, size, sort);
    }

    private void validateSorting(String sortBy, String direction) {
        if (!"asc".equalsIgnoreCase(direction)
                && !"desc".equalsIgnoreCase(direction)) {
            throw new BadRequestException(
                    "Invalid sort direction");
        }

        if (!Constants.ALLOWED_SORT_FIELDS.contains(sortBy)) {
            throw new BadRequestException(
                    "Invalid sort field: " + sortBy);
        }
    }

    private String validateKeyword(String keyword) {
        if (keyword == null) {
            throw new BadRequestException(
                    "Keyword cannot be empty");
        }

        keyword = keyword.trim();

        if (keyword.isBlank()) {
            throw new BadRequestException(
                    "Keyword cannot be empty");
        }

        if (keyword.length() > 100) {
            throw new BadRequestException(
                    "Keyword is too long");
        }

        return keyword;
    }

    private Listing getListing(Long listingId) {
        return listingRepository.findById(listingId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Listing not found with id: " + listingId));
    }

    private void validatePublicVisibility(Listing listing) {
        if (!Constants.PUBLIC_STATUSES.contains(listing.getStatus())) {
            throw new ResourceNotFoundException(
                    "Listing not found");
        }
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
                .category(category == null ? "Uncategorized" : category.getName())
                .startingPrice(listing.getStartingPrice())
                .reservePrice(listing.getReservePrice())
                .buyNowPrice(listing.getBuyNowPrice())
                .status(listing.getStatus())
                .images(imageUrls)
                .build();
    }

    private void validateStatusFilter(ListingStatus status) {
        if (!Constants.PUBLIC_STATUSES.contains(status)) {
            throw new BadRequestException(
                    "Invalid status filter");
        }
    }
}
