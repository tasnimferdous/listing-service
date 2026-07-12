package com.tasnim.listingservice.controller;

import com.tasnim.commonlibrary.model.CommonResponse;
import com.tasnim.commonlibrary.utils.ResponseUtil;
import com.tasnim.listingservice.dtos.response.ListingDetailsResponse;
import com.tasnim.listingservice.dtos.response.ListingResponse;
import com.tasnim.listingservice.enums.ListingStatus;
import com.tasnim.listingservice.service.PublicListingService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/public/listings")
public class PublicListingController {

    private final PublicListingService publicListingService;

    public PublicListingController(PublicListingService publicListingService) {
        this.publicListingService = publicListingService;
    }

    @GetMapping
    public CommonResponse<Page<ListingResponse>> getListings(
            @RequestParam(required = false) ListingStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        Page<ListingResponse> response =
                publicListingService.getListings(status, page, size, sortBy, direction);

        return ResponseUtil.success(response, "Listings retrieved successfully");
    }

    @GetMapping("/{id}")
    public CommonResponse<ListingDetailsResponse> getListingDetails(@PathVariable Long id) {
        ListingDetailsResponse response =
                publicListingService.getListingDetails(id);

        return ResponseUtil.success(response, "Listing Details retrieved successfully");
    }

    @GetMapping("/category/{categoryId}")
    public CommonResponse<Page<ListingResponse>> getListingsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(required = false) ListingStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ListingResponse> response =
                publicListingService.getListingsByCategory(categoryId, status, page, size);

        return ResponseUtil.success(response, "Listings by category retrieved successfully");
    }

    @GetMapping("/search")
    public CommonResponse<Page<ListingResponse>> searchListings(
            @RequestParam String keyword,
            @RequestParam(required = false) ListingStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ListingResponse> response =
                publicListingService.searchListings(keyword, status, page, size);

        return ResponseUtil.success(response, "Listings retrieved successfully");
    }
}