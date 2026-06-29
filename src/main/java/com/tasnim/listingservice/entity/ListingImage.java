package com.tasnim.listingservice.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ListingImage {
    private long id;
    private long listingId;
    private String imageUrl;
    private int displayOrder;
}
