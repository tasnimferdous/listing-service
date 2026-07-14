package com.tasnim.listingservice.controller;

import com.tasnim.commonlibrary.model.CommonResponse;
import com.tasnim.commonlibrary.utils.ResponseUtil;
import com.tasnim.listingservice.dtos.request.ListingRejectionRequest;
import com.tasnim.listingservice.dtos.response.ListingDetailsResponse;
import com.tasnim.listingservice.dtos.response.ListingResponse;
import com.tasnim.listingservice.enums.ListingStatus;
import com.tasnim.listingservice.service.AdminListingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/v1/admin/listings")
public class AdminListingController {
    private final AdminListingService adminListingService;

    public AdminListingController(AdminListingService adminListingService) {
        this.adminListingService = adminListingService;
    }

    @GetMapping("/status/{status}")
    public CommonResponse<Page<ListingResponse>> getListingsByStatus(
            @PathVariable ListingStatus status,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        Page<ListingResponse> response =
                adminListingService.getListingsByStatus(status, page, size, sortBy, direction);

        return ResponseUtil.success(response, "Listings retrieved successfully");
    }

    @GetMapping("/{listingId}")
    public CommonResponse<ListingDetailsResponse> getListingDetails(@PathVariable Long listingId) {
        ListingDetailsResponse response = adminListingService.getListingDetails(listingId);
        return ResponseUtil.success(response, "Listing details retrieved successfully");
    }

    @PostMapping("/{listingId}/approve")
    public CommonResponse<Void> approveListing(@PathVariable Long listingId) {
        adminListingService.approveListing(listingId);
        return ResponseUtil.success("Listing approved successfully");
    }

    @PostMapping("/{listingId}/reject")
    public CommonResponse<Void> rejectListing(
            @PathVariable Long listingId,
            @Valid @RequestBody ListingRejectionRequest request) {
        adminListingService.rejectListing(listingId, request);

        return ResponseUtil.success("Listing rejected successfully");
    }
}