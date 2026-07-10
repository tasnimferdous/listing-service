package com.tasnim.listingservice.dtos.request;

import com.tasnim.listingservice.enums.ListingCondition;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class ListingUpdateRequest {
    @Size(max = 200, message = "Title cannot exceed 200 characters")
    private String title;

    @Size(max = 5000, message = "Description cannot exceed 5000 characters")
    private String description;

    @Enumerated(EnumType.STRING)
    private ListingCondition condition;

    @DecimalMin(value = "0.01", message = "Starting price must be greater than zero")
    private BigDecimal startingPrice;

    @DecimalMin(value = "0.01", message = "Reserve price must be greater than zero")
    private BigDecimal reservePrice;

    @DecimalMin(value = "0.01", message = "Buy Now price must be greater than zero")
    private BigDecimal buyNowPrice;

    private Long categoryId;

    @Size(max = 10, message = "Maximum 10 images allowed")
    private List<String> imageUrls;
}
