package com.tasnim.listingservice.utils;

import com.tasnim.commonlibrary.exceptions.BadRequestException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static com.tasnim.listingservice.utils.Constants.ALLOWED_SORT_FIELDS;

public class ListingUtil {
    public static void validateSorting(String sortBy, String direction) {
        if (!"asc".equalsIgnoreCase(direction)
                && !"desc".equalsIgnoreCase(direction)) {
            throw new BadRequestException(
                    "Invalid sort direction");
        }

        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
            throw new BadRequestException(
                    "Invalid sort field: " + sortBy);
        }
    }

    public static Pageable buildPageable(int page, int size, String sortBy, String direction) {
        Sort sort = Sort.by(sortBy);
        sort = "desc".equalsIgnoreCase(direction)
                ? sort.descending()
                : sort.ascending();

        return PageRequest.of(page, size, sort);
    }
}
