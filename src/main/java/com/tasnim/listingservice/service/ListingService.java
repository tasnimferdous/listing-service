package com.tasnim.listingservice.service;

import com.tasnim.listingservice.dtos.request.ListingCreateRequest;
import com.tasnim.listingservice.dtos.request.ListingUpdateRequest;
import com.tasnim.listingservice.dtos.response.ListingDetailsResponse;
import com.tasnim.listingservice.dtos.response.ListingResponse;

import java.util.List;

public interface ListingService {
    ListingResponse createListing(ListingCreateRequest request);

    ListingResponse updateListing(Long listingId, ListingUpdateRequest request);

    void deleteListing(Long listingId);

    List<ListingResponse> getMyListings();

    ListingDetailsResponse getListingById(Long listingId);
}
