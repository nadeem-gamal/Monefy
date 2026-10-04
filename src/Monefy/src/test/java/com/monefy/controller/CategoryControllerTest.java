package com.monefy.controller;

import com.monefy.entity.Category;
import com.monefy.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CategoryControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(categoryController).build();
    }

    @Test
    void testGetAllCategories() throws Exception {
        Category category1 = new Category(1L, "Category1", "Description1", false);
        Category category2 = new Category(2L, "Category2", "Description2", false);

        when(categoryService.getAllCategories()).thenReturn(Arrays.asList(category1, category2));

        mockMvc.perform(get("/category/getAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Category1"))
                .andExpect(jsonPath("$[1].name").value("Category2"));
    }

    @Test
    void testGetCategoryById() throws Exception {
        Category category = new Category(1L, "Category1", "Description1", false);

        when(categoryService.getCategoryById(anyLong())).thenReturn(Optional.of(category));

        mockMvc.perform(get("/category/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Category1"));
    }

    @Test
    void testCreateCategory() throws Exception {
        Category category = new Category(1L, "Category1", "Description1", false);

        when(categoryService.saveCategory(any(Category.class))).thenReturn(category);

        mockMvc.perform(post("/category")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Category1\",\"description\":\"Description1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Category1"));
    }

    @Test
    void testDeleteCategory() throws Exception {
        mockMvc.perform(delete("/category/1"))
                .andExpect(status().isOk());
    }
}