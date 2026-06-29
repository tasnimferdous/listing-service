package com.tasnim.listingservice.entity;

import com.tasnim.listingservice.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Listing {
    private long id;
    private String title;
    private String description;
    private String category;
    private String condition;
    private long sellerId;
    private double startingPrice;
    private double reservePrice;
    private Status status;
    private Instant auctionStartTime;
    private Instant auctionEndTime;
    private Instant createdAt;
    private Instant updatedAt;
}
