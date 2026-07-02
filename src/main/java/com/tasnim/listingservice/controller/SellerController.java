package com.tasnim.listingservice.controller;

import com.tasnim.listingservice.dtos.request.ListingCreateRequest;
import com.tasnim.listingservice.dtos.request.ListingUpdateRequest;
import com.tasnim.listingservice.dtos.response.CommonResponse;
import com.tasnim.listingservice.dtos.response.ListingDetailsResponse;
import com.tasnim.listingservice.dtos.response.ListingResponse;
import com.tasnim.listingservice.service.ListingService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/seller/listings")
public class SellerController {

    private final ListingService listingService;

    public SellerController(ListingService listingService) {
        this.listingService = listingService;
    }

    @PostMapping
    public CommonResponse<ListingResponse> createListing(@RequestBody @Valid ListingCreateRequest request){
        ListingResponse response = listingService.createListing(request);
        return CommonResponse.<ListingResponse>builder()
                .success(true)
                .content(response)
                .message("Listing created successfully")
                .build();
    }

    @PutMapping("/{id}")
    public CommonResponse<ListingResponse> updateListing(
            @PathVariable Long id,
            @RequestBody @Valid ListingUpdateRequest request)
    {
        ListingResponse response = listingService.updateListing(id, request);
        return CommonResponse.<ListingResponse>builder()
                .success(true)
                .content(response)
                .message("Listing updated successfully")
                .build();
    }

    @DeleteMapping("/{id}")
    public CommonResponse<Void> deleteListing(@PathVariable Long id) {
        listingService.deleteListing(id);
        return CommonResponse.<Void>builder()
                .success(true)
                .message("Listing deleted successfully")
                .build();
    }

    @GetMapping("/my-listings")
    public CommonResponse<List<ListingResponse>> getMyListings() {
        List<ListingResponse> listings = listingService.getMyListings();
        return CommonResponse.<List<ListingResponse>>builder()
                .success(true)
                .content(listings)
                .message("Listings retrieved successfully")
                .build();
    }

    @GetMapping("/{id}")
    public CommonResponse<ListingDetailsResponse> getListing(@PathVariable Long id) {
        ListingDetailsResponse response = listingService.getListingById(id);
        return CommonResponse.<ListingDetailsResponse>builder()
                .success(true)
                .content(response)
                .message("Listing retrieved successfully")
                .build();
    }
}