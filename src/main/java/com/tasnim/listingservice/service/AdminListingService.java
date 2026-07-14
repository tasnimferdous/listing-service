package com.tasnim.listingservice.service;

import com.tasnim.listingservice.dtos.request.ListingRejectionRequest;
import com.tasnim.listingservice.dtos.response.ListingDetailsResponse;
import com.tasnim.listingservice.dtos.response.ListingResponse;
import com.tasnim.listingservice.enums.ListingStatus;
import org.springframework.data.domain.Page;

public interface AdminListingService {

    Page<ListingResponse> getListingsByStatus(
            ListingStatus status,
            int page,
            int size,
            String sortBy,
            String direction);

    ListingDetailsResponse getListingDetails(Long listingId);

    void approveListing(Long listingId);

    void rejectListing(Long listingId, ListingRejectionRequest request);
}