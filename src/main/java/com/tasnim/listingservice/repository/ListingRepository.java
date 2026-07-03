package com.tasnim.listingservice.repository;

import com.tasnim.listingservice.entity.Listing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface ListingRepository extends JpaRepository<Listing, Long> {
    Collection<Listing> findBySellerId(Long sellerId);
}
