package com.tasnim.listingservice.dtos.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ListingRequest {
    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title cannot exceed 200 characters")
    private String title;

    @Size(max = 5000, message = "Description cannot exceed 5000 characters")
    private String description;

    @NotNull(message = "Starting price is required")
    @DecimalMin(value = "0.01", message = "Starting price must be greater than zero")
    private BigDecimal startingPrice;

    @NotNull(message = "Category is required")
    private Long categoryId;

    @Size(max = 10, message = "Maximum 10 images allowed")
    private List<String> imageUrls = new ArrayList<>();
}
