package com.tasnim.listingservice.repository;

import com.tasnim.listingservice.entity.Listing;
import com.tasnim.listingservice.enums.ListingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface ListingRepository extends JpaRepository<Listing, Long> {
    Page<Listing> findBySellerId(String sellerId,  Pageable pageable);

    Page<Listing> findBySellerIdAndStatus(String sellerId, ListingStatus status, Pageable pageable);

    Page<Listing> findByStatus(ListingStatus status, Pageable pageable);

    Page<Listing> findByCategoryIdAndStatus(Long categoryId, ListingStatus status, Pageable pageable);

    @Query("""
       SELECT l
       FROM Listing l
       WHERE l.status = :status
       AND (
            LOWER(l.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(l.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
       )
       """)
    Page<Listing> searchListings(@Param("keyword") String keyword, @Param("status") ListingStatus status, Pageable pageable);

    @Query("""
       SELECT l
       FROM Listing l
       WHERE l.status IN :statuses
       AND (
            LOWER(l.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(l.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
       )
       """)
    Page<Listing> searchListings(@Param("keyword") String keyword, @Param("statuses") Collection<ListingStatus> statuses, Pageable pageable);
}
