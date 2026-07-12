package com.tasnim.listingservice.service.impl;

import com.tasnim.commonlibrary.exceptions.ResourceNotFoundException;
import com.tasnim.listingservice.entity.Category;
import com.tasnim.listingservice.repository.CategoryRepository;
import com.tasnim.listingservice.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void validateCategory(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException(
                    "Category not found with id: " + categoryId);
        }
    }

    @Override
    public Category getCategoryById(Long categoryId) {
        return categoryRepository
                .findById(categoryId).orElse(null);
    }
}
