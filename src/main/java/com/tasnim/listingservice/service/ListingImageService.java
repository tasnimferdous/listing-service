package com.tasnim.listingservice.service;

import java.util.List;

public interface ListingImageService {
    List<String> findImagesByListingId(Long listingId);
    List<String> findImagesByListingIdInOrder(Long listingId);
    void storeListingImages(List<String> imageUrls, Long listingId);
    void deleteImagesByListingId(Long listingId);
}
