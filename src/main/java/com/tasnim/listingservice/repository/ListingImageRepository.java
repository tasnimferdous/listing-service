package com.tasnim.listingservice.repository;

import com.tasnim.listingservice.entity.ListingImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ListingImageRepository extends JpaRepository<ListingImage, Long> {
    List<ListingImage> findByListingId(Long listingId);

    List<ListingImage> findByListingIdOrderByDisplayOrderAsc(Long listingId);

    void deleteByListingId(Long listingId);
}
