package com.tasnim.listingservice.dtos.response;

import com.tasnim.listingservice.enums.ListingStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ListingResponse {
    private long id;
    private String title;
    @Enumerated(EnumType.STRING)
    private ListingStatus status;
    private BigDecimal startingPrice;
    private BigDecimal reservePrice;
    private BigDecimal buyNowPrice;
}
