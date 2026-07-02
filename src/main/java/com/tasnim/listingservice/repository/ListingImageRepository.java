package com.tasnim.listingservice.repository;

import com.tasnim.listingservice.entity.ListingImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ListingImageRepository extends JpaRepository<ListingImage, Long> {
}
