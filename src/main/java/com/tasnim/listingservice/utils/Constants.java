package com.tasnim.listingservice.utils;

import com.tasnim.listingservice.enums.ListingStatus;

import java.time.Duration;
import java.util.Set;

public final class Constants {

    private Constants() {}

    public static final Set<String> ALLOWED_SORT_FIELDS =
            Set.of(
                    "id",
                    "title",
                    "startingPrice",
                    "createdAt"
            );

    public static final Set<ListingStatus> PUBLIC_STATUSES =
            Set.of(
                    ListingStatus.SCHEDULED,
                    ListingStatus.LIVE,
                    ListingStatus.ENDED
            );
    public static final Set<String> ALLOWED_TYPES =
            Set.of(
                    "image/jpeg",
                    "image/png",
                    "image/webp"
            );

    public static final Duration MIN_START_TIME_OFFSET =
            Duration.ofHours(24);

    public static final Duration MIN_AUCTION_DURATION =
            Duration.ofHours(1);

    public static final Duration MAX_AUCTION_DURATION =
            Duration.ofDays(30);
}
