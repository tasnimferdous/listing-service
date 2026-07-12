package com.tasnim.listingservice.controller;

import com.tasnim.commonlibrary.model.CommonResponse;
import com.tasnim.commonlibrary.utils.ResponseUtil;
import com.tasnim.listingservice.dtos.request.ListingCreateRequest;
import com.tasnim.listingservice.dtos.request.ListingUpdateRequest;
import com.tasnim.listingservice.dtos.response.ListingDetailsResponse;
import com.tasnim.listingservice.dtos.response.ListingResponse;
import com.tasnim.listingservice.service.ListingService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasRole('SELLER')")
    public CommonResponse<ListingResponse> createListing(@RequestBody @Valid ListingCreateRequest request){
        ListingResponse response = listingService.createListing(request);
        return ResponseUtil.success(response, "Listing created successfully");
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public CommonResponse<ListingResponse> updateListing(
            @PathVariable Long id,
            @RequestBody @Valid ListingUpdateRequest request)
    {
        ListingResponse response = listingService.updateListing(id, request);
        return ResponseUtil.success(response, "Listing updated successfully");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public CommonResponse<Void> deleteListing(@PathVariable Long id) {
        listingService.deleteListing(id);
        return ResponseUtil.success("Listing deleted successfully");
    }

    @GetMapping("/my-listings")
    @PreAuthorize("hasRole('SELLER')")
    public CommonResponse<List<ListingResponse>> getMyListings() {
        List<ListingResponse> responses = listingService.getMyListings();
        return ResponseUtil.success(responses, "Listings retrieved successfully");
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public CommonResponse<ListingDetailsResponse> getListingDetails(@PathVariable Long id) {
        ListingDetailsResponse response = listingService.getListingDetails(id);
        return ResponseUtil.success(response, "Listing Details retrieved successfully");
    }
}