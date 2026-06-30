package com.tasnim.listingservice.service;

import com.tasnim.listingservice.dtos.request.ListingRequest;
import com.tasnim.listingservice.dtos.response.ListingDetailsResponse;
import com.tasnim.listingservice.dtos.response.ListingResponse;

import java.util.List;

public interface ListingService {
    ListingResponse createListing(ListingRequest request);

    ListingResponse updateListing(Long listingId, ListingRequest request);

    void deleteListing(Long listingId);

    List<ListingResponse> getMyListings();

    ListingDetailsResponse getListingById(Long listingId);
}
