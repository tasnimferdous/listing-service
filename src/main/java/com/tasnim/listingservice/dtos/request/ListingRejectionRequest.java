package com.tasnim.listingservice.dtos.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ListingRejectionRequest {
    @NotBlank(message = "Rejection reason is required")
    private String reason;
}