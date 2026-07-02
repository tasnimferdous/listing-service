package com.tasnim.listingservice.dtos.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ListingUpdateRequest {
    @Size(max = 200, message = "Title cannot exceed 200 characters")
    private String title;

    @Size(max = 5000, message = "Description cannot exceed 5000 characters")
    private String description;

    private String condition;

    @DecimalMin(value = "0.01", message = "Starting price must be greater than zero")
    private BigDecimal startingPrice;

    @DecimalMin(value = "0.01", message = "Starting price must be greater than zero")
    private BigDecimal reservePrice;

    @DecimalMin(value = "0.01", message = "Starting price must be greater than zero")
    private BigDecimal buyNowPrice;

    private Long categoryId;
}
