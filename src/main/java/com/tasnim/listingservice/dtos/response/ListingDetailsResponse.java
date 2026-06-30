package com.tasnim.listingservice.dtos.response;

import com.tasnim.listingservice.enums.ListingStatus;

import java.math.BigDecimal;
import java.util.List;

public class ListingDetailsResponse {
    private Long id;
    private String title;
    private String description;
    private BigDecimal startingPrice;
    private ListingStatus status;
    private List<String> images;
}
