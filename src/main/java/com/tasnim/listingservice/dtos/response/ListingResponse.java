package com.tasnim.listingservice.dtos.response;

import com.tasnim.listingservice.enums.ListingStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

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
}
