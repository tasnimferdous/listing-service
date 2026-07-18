package com.tasnim.listingservice.service.impl;

import com.tasnim.listingservice.entity.Listing;
import com.tasnim.listingservice.enums.ListingStatus;
import com.tasnim.listingservice.repository.ListingRepository;
import com.tasnim.listingservice.service.SchedulerListingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
@Transactional
public class SchedulerListingServiceImpl implements SchedulerListingService {
    private static final int BATCH_SIZE = 100;
    private final ListingRepository listingRepository;

    public SchedulerListingServiceImpl(ListingRepository listingRepository) {
        this.listingRepository = listingRepository;
    }

    @Override
    public void startScheduledAuctions() {
        Instant now = Instant.now();
        Page<Listing> listings = listingRepository.findByStatusAndAuctionStartTimeLessThanEqual(
                    ListingStatus.SCHEDULED,
                    now,
                    PageRequest.of(
                            0,
                            BATCH_SIZE,
                            Sort.by("auctionStartTime").ascending()
                    ));

        if (listings.isEmpty()) return;
        listings.forEach(this::startAuction);

        log.info("Started auctions. count={}", listings.getNumberOfElements());
    }

    @Override
    public void endLiveAuctions() {
        Instant now = Instant.now();
        Page<Listing> listings = listingRepository.findByStatusAndAuctionEndTimeLessThanEqual(
                    ListingStatus.LIVE,
                    now,
                    PageRequest.of(
                            0,
                            BATCH_SIZE,
                            Sort.by("auctionEndTime").ascending()
                    ));

        if (listings.isEmpty()) return;
        listings.forEach(this::endAuction);

        log.info("Ended auctions. count={}", listings.getNumberOfElements());
    }

    private void startAuction(Listing listing) {
        listing.setStatus(ListingStatus.LIVE);
        log.info("Auction started. listingId={}", listing.getId());
    }

    private void endAuction(Listing listing) {
        listing.setStatus(ListingStatus.ENDED);
        log.info("Auction ended. listingId={}", listing.getId());
    }
}
