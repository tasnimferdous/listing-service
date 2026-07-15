package com.tasnim.listingservice.scheduler;

import com.tasnim.listingservice.service.SchedulerListingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ListingScheduler {
    private final SchedulerListingService schedulerListingService;

    public ListingScheduler(SchedulerListingService schedulerListingService) {
        this.schedulerListingService = schedulerListingService;
    }

    @Scheduled(fixedDelay = 60000)
    public void startScheduledAuctions() {
        schedulerListingService.startScheduledAuctions();
    }

    @Scheduled(fixedDelay = 60000)
    public void endLiveAuctions() {
        schedulerListingService.endLiveAuctions();
    }
}