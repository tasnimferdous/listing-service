package com.tasnim.listingservice.service.impl;

import com.tasnim.listingservice.dtos.request.ListingRequest;
import com.tasnim.listingservice.dtos.response.ListingDetailsResponse;
import com.tasnim.listingservice.dtos.response.ListingResponse;
import com.tasnim.listingservice.service.ListingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ListingServiceImpl implements ListingService {
    @Override
    public ListingResponse createListing(ListingRequest request) {
        return null;
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
