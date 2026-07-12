package com.tasnim.listingservice.service.impl;

import com.tasnim.listingservice.entity.ListingImage;
import com.tasnim.listingservice.repository.ListingImageRepository;
import com.tasnim.listingservice.service.ListingImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ListingImageServiceImpl implements ListingImageService {
    private final ListingImageRepository listingImageRepository;

    public ListingImageServiceImpl(ListingImageRepository listingImageRepository) {
        this.listingImageRepository = listingImageRepository;
    }

    @Override
    public List<String> findImagesByListingId(Long listingId) {
        return listingImageRepository
                .findByListingId(listingId)
                .stream()
                .map(ListingImage::getImageUrl)
                .toList();
    }

    @Override
    public List<String> findImagesByListingIdInOrder(Long listingId) {
        return listingImageRepository
                .findByListingIdOrderByDisplayOrderAsc(listingId)
                .stream()
                .map(ListingImage::getImageUrl)
                .toList();
    }

    @Override
    @Transactional
    public void deleteImagesByListingId(Long listingId) {
        List<String> images = findImagesByListingId(listingId);
        if (images.isEmpty())  return;

        log.info("Deleting {} images for listingId={}", images.size(), listingId);

        deletePhysicalImages(images);
        listingImageRepository.deleteByListingId(listingId);

        log.info("Images deleted for listingId={}", listingId);
    }

    @Override
    public void storeListingImages(List<String> imageUrls, Long listingId) {
        log.info("Storing listing image for listingId={}", listingId);

        List<ListingImage> images = new ArrayList<>();
        for (int i = 0; i < imageUrls.size(); i++) {
            images.add(
                    ListingImage.builder()
                            .listingId(listingId)
                            .imageUrl(imageUrls.get(i))
                            .displayOrder(i + 1)
                            .build()
            );
        }

        listingImageRepository.saveAll(images);
    }

    private void deletePhysicalImages(List<String> images) {
        for (String image : images) {
            try {
                String fileName = image.replace("/uploads/", "");
                Path filePath = Paths.get("uploads").resolve(fileName);
                Files.deleteIfExists(filePath);
            } catch (IOException ex) {
                log.error(
                        "Failed to delete image. url={}",
                        image, ex);
            }
        }
    }
}
