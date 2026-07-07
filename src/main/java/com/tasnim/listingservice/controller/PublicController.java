package com.tasnim.listingservice.controller;

import com.tasnim.commonlibrary.model.CommonResponse;
import com.tasnim.commonlibrary.utils.ResponseUtil;
import com.tasnim.listingservice.dtos.response.ListingDetailsResponse;
import com.tasnim.listingservice.service.ListingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/public/listings")
public class PublicController {
    private final ListingService listingService;

    public PublicController(ListingService listingService) {
        this.listingService = listingService;
    }

    @GetMapping("/{id}")
    public CommonResponse<ListingDetailsResponse> getListing(@PathVariable Long id) {
        ListingDetailsResponse response = listingService.getListingDetails(id);
        return ResponseUtil.success(response, "Listing retrieved successfully");
    }
}
