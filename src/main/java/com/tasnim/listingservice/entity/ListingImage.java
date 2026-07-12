package com.tasnim.listingservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ListingImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long listingId;
    private String imageUrl;
    private Integer displayOrder;
}
