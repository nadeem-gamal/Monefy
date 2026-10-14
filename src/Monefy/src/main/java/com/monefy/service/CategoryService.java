package com.monefy.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.monefy.entity.Category;
import com.monefy.exception.BusinessRuleException;
import com.monefy.exception.ResourceNotFoundException;
import com.monefy.repository.CategoryRepository;
import com.monefy.repository.TransactionRepository;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    public Category saveCategory(Category category) {
        if (category.getName() == null || category.getName().isBlank()) {
            throw new BusinessRuleException("Category name is required");
        }
        category.setName(category.getName().trim());
        return categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category not found: " + id);
        }
        if (transactionRepository.existsByCategoryId(id.intValue())) {
            throw new BusinessRuleException(
                    "Category still has transactions; move or delete them first");
        }
        categoryRepository.deleteById(id);
    }
}
