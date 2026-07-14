package com.tasnim.listingservice.controller;

import com.tasnim.commonlibrary.model.CommonResponse;
import com.tasnim.commonlibrary.utils.ResponseUtil;
import com.tasnim.listingservice.dtos.request.ListingCreateRequest;
import com.tasnim.listingservice.dtos.request.ListingUpdateRequest;
import com.tasnim.listingservice.dtos.response.ListingDetailsResponse;
import com.tasnim.listingservice.dtos.response.ListingResponse;
import com.tasnim.listingservice.enums.ListingStatus;
import com.tasnim.listingservice.service.ListingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@PreAuthorize("hasRole('SELLER')")
@RequestMapping("/api/v1/seller/listings")
public class SellerController {

    private final ListingService listingService;

    public SellerController(ListingService listingService) {
        this.listingService = listingService;
    }

    @PostMapping
    public CommonResponse<ListingResponse> createListing(@RequestBody @Valid ListingCreateRequest request){
        ListingResponse response = listingService.createListing(request);
        return ResponseUtil.success(response, "Listing created successfully");
    }

    @PutMapping("/{id}")
    public CommonResponse<ListingResponse> updateListing(
            @PathVariable Long id,
            @RequestBody @Valid ListingUpdateRequest request)
    {
        ListingResponse response = listingService.updateListing(id, request);
        return ResponseUtil.success(response, "Listing updated successfully");
    }

    @DeleteMapping("/{id}")
    public CommonResponse<Void> deleteListing(@PathVariable Long id) {
        listingService.deleteListing(id);
        return ResponseUtil.success("Listing deleted successfully");
    }

    @GetMapping("/my-listings")
    public CommonResponse<Page<ListingResponse>> getMyListings(
            @RequestParam(required = false) ListingStatus status,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        Page<ListingResponse> response =
                listingService.getMyListings(status, page, size, sortBy, direction);

        return ResponseUtil.success(response, "Listings retrieved successfully");
    }

    @GetMapping("/{id}")
    public CommonResponse<ListingDetailsResponse> getListingDetails(@PathVariable Long id) {
        ListingDetailsResponse response = listingService.getListingDetails(id);
        return ResponseUtil.success(response, "Listing Details retrieved successfully");
    }
}