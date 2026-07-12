package com.tasnim.listingservice.service;

import com.tasnim.listingservice.entity.Category;
import jakarta.validation.constraints.NotNull;

public interface CategoryService {
    void validateCategory(Long categoryId);

    Category getCategoryById(Long categoryId);
}
