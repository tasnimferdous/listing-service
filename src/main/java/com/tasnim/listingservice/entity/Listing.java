package com.tasnim.listingservice.entity;

import com.tasnim.listingservice.enums.ListingStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Listing {
    private Long id;
    private String title;
    private String description;
    private Long category;
    private String condition;
    private Long sellerId;
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal startingPrice;
    @Column(precision = 19, scale = 2)
    private BigDecimal reservePrice;
    @Column(precision = 19, scale = 2)
    private BigDecimal buyNowPrice;
    private ListingStatus status;
    private Instant auctionStartTime;
    private Instant auctionEndTime;
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }
}
