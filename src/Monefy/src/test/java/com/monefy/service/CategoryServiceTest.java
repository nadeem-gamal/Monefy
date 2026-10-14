package com.monefy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.monefy.entity.Category;
import com.monefy.exception.BusinessRuleException;
import com.monefy.exception.ResourceNotFoundException;
import com.monefy.repository.CategoryRepository;
import com.monefy.repository.TransactionRepository;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category category(Long id, String name, boolean type) {
        Category category = new Category();
        category.setId(id);
        category.setName(name);
        category.setType(type);
        return category;
    }

    @Test
    void savingTrimsTheName() {
        when(categoryRepository.save(any(Category.class))).thenAnswer(i -> i.getArgument(0));

        Category saved = categoryService.saveCategory(category(null, "  Salary  ", true));

        assertEquals("Salary", saved.getName());
    }

    @Test
    void missingNameIsRejected() {
        assertThrows(BusinessRuleException.class,
                () -> categoryService.saveCategory(category(null, null, true)));
        assertThrows(BusinessRuleException.class,
                () -> categoryService.saveCategory(category(null, "   ", true)));

        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void categoryWithTransactionsCannotBeDeleted() {
        when(categoryRepository.existsById(3L)).thenReturn(true);
        when(transactionRepository.existsByCategoryId(3)).thenReturn(true);

        assertThrows(BusinessRuleException.class, () -> categoryService.deleteCategory(3L));

        verify(categoryRepository, never()).deleteById(3L);
    }

    @Test
    void unusedCategoryCanBeDeleted() {
        when(categoryRepository.existsById(3L)).thenReturn(true);

        categoryService.deleteCategory(3L);

        verify(categoryRepository).deleteById(3L);
    }

    @Test
    void deletingUnknownCategoryIsRejected() {
        when(categoryRepository.existsById(9L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> categoryService.deleteCategory(9L));

        verify(transactionRepository, never()).existsByCategoryId(9);
    }
}
