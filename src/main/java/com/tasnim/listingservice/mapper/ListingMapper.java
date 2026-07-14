package com.tasnim.listingservice.mapper;

import com.tasnim.listingservice.dtos.response.ListingDetailsResponse;
import com.tasnim.listingservice.dtos.response.ListingResponse;
import com.tasnim.listingservice.entity.Listing;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ListingMapper {
    public ListingResponse toListingResponse(Listing listing) {
        return ListingResponse.builder()
                .id(listing.getId())
                .title(listing.getTitle())
                .status(listing.getStatus())
                .startingPrice(listing.getStartingPrice())
                .reservePrice(listing.getReservePrice())
                .buyNowPrice(listing.getBuyNowPrice())
                .build();
    }

    public ListingDetailsResponse toListingDetailsResponse(Listing listing, String category, List<String> images) {
        return ListingDetailsResponse.builder()
                .id(listing.getId())
                .title(listing.getTitle())
                .description(listing.getDescription())
                .condition(listing.getListingCondition())
                .category(category)
                .startingPrice(listing.getStartingPrice())
                .reservePrice(listing.getReservePrice())
                .buyNowPrice(listing.getBuyNowPrice())
                .status(listing.getStatus())
                .images(images)
                .build();
    }
}