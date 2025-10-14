package com.closedigit.bookstore.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.closedigit.bookstore.dto.AuthRequest;
import com.closedigit.bookstore.dto.AuthResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Test class for book sorting functionality
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class BookSortingTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String adminToken;

    @BeforeEach
    void setUp() throws Exception {
        AuthRequest loginRequest = new AuthRequest("admin", "admin123");
        
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();
        
        String loginResponse = loginResult.getResponse().getContentAsString();
        AuthResponse authResponse = objectMapper.readValue(loginResponse, AuthResponse.class);
        adminToken = authResponse.token();
    }

    @Test
    void getAllBooks_WithPriceSortAsc_ShouldReturnBooksSortedByCheapestFirst() throws Exception {
        mockMvc.perform(get("/api/books")
                .header("Authorization", "Bearer " + adminToken)
                .param("sortBy", "price")
                .param("sortDir", "asc")
                .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].price").exists());
    }

    @Test
    void getAllBooks_WithPriceSortDesc_ShouldReturnBooksSortedByMostExpensiveFirst() throws Exception {
        mockMvc.perform(get("/api/books")
                .header("Authorization", "Bearer " + adminToken)
                .param("sortBy", "price")
                .param("sortDir", "desc")
                .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].price").exists());
    }

    @Test
    void getAllBooks_WithPublishedDateSortDesc_ShouldReturnNewestBooksFirst() throws Exception {
        mockMvc.perform(get("/api/books")
                .header("Authorization", "Bearer " + adminToken)
                .param("sortBy", "publishedDate")
                .param("sortDir", "desc")
                .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].publishedDate").exists());
    }

    @Test
    void getAllBooks_WithPublishedDateSortAsc_ShouldReturnOldestBooksFirst() throws Exception {
        mockMvc.perform(get("/api/books")
                .header("Authorization", "Bearer " + adminToken)
                .param("sortBy", "publishedDate")
                .param("sortDir", "asc")
                .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].publishedDate").exists());
    }

    @Test
    void getAllBooks_WithAuthorSort_ShouldReturnBooksSortedByAuthor() throws Exception {
        mockMvc.perform(get("/api/books")
                .header("Authorization", "Bearer " + adminToken)
                .param("sortBy", "author")
                .param("sortDir", "asc")
                .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].author").exists());
    }

    @Test
    void getAllBooks_WithInvalidSortField_ShouldDefaultToTitle() throws Exception {
        mockMvc.perform(get("/api/books")
                .header("Authorization", "Bearer " + adminToken)
                .param("sortBy", "invalidField")
                .param("sortDir", "asc")
                .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }
}