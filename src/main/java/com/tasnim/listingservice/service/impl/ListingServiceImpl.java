package com.tasnim.listingservice.service.impl;

import com.tasnim.listingservice.dtos.request.ListingRequest;
import com.tasnim.listingservice.dtos.response.ListingDetailsResponse;
import com.tasnim.listingservice.dtos.response.ListingResponse;
import com.tasnim.listingservice.entity.Listing;
import com.tasnim.listingservice.repository.CategoryRepository;
import com.tasnim.listingservice.repository.ListingRepository;
import com.tasnim.listingservice.service.ListingService;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public ListingResponse createListing(ListingRequest request) {

        log.info("Creating listing with title={}", request.getTitle());

        validateCategory(request.getCategoryId());

        Listing listing = buildListing(request);

        assert listing != null;
        listing = listingRepository.save(listing);

        log.info("Listing created successfully. id={}", listing.getId());

        return ListingResponse.builder()
                .id(listing.getId())
                .title(listing.getTitle())
                .status(listing.getStatus())
                .build();
    }

    private Listing buildListing(ListingRequest request) {
        return null;
    }

    private void validateCategory(@NotNull(message = "Category is required") Long categoryId) {
    }

    @Override
    public ListingResponse updateListing(Long listingId, ListingRequest request) {
        return null;
    }

    @Override
    public void deleteListing(Long listingId) {

    }

    @Override
    public List<ListingResponse> getMyListings() {
        return List.of();
    }

    @Override
    public ListingDetailsResponse getListingById(Long listingId) {
        return null;
    }
}
