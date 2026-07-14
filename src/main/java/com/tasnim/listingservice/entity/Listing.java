package com.tasnim.listingservice.entity;

import com.tasnim.listingservice.enums.ListingStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(
        indexes = {

                @Index(name = "idx_listing_seller_id", columnList = "sellerId"),
                @Index(name = "idx_listing_status", columnList = "status"),
                @Index(name = "idx_listing_category_id", columnList = "categoryId"),
                @Index(name = "idx_listing_status_category", columnList = "status, categoryId"),
                @Index(name = "idx_listing_seller_status", columnList = "sellerId, status"),
                @Index(name = "idx_listing_status_start_time", columnList = "status, auctionStartTime"),
                @Index(name = "idx_listing_status_end_time", columnList = "status, auctionEndTime")
        }
)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Listing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private Long categoryId;
    private String listingCondition;
    private String sellerId;
    @Column(precision = 19, scale = 2)
    private BigDecimal startingPrice;
    @Column(precision = 19, scale = 2)
    private BigDecimal reservePrice;
    @Column(precision = 19, scale = 2)
    private BigDecimal buyNowPrice;
    @Enumerated(EnumType.STRING)
    private ListingStatus status;
    private Instant auctionStartTime;
    private Instant auctionEndTime;
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
    @Column(nullable = false)
    private Instant updatedAt;

//    --- Admin Part ---
    private String approvedBy;
    private Instant approvedAt;
    private String rejectedBy;
    private Instant rejectedAt;
    private String rejectionReason;

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
