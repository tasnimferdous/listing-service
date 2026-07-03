package com.tasnim.listingservice.dtos.response;

import com.tasnim.listingservice.enums.ListingStatus;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ListingDetailsResponse {
    private Long id;
    private Long categoryId;
    private String title;
    private String description;
    private String condition;
    private BigDecimal startingPrice;
    private BigDecimal reservePrice;
    private BigDecimal buyNowPrice;
    private ListingStatus status;
    private List<String> images;
}
