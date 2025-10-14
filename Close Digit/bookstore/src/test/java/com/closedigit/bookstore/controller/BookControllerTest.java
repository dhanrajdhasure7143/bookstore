package com.closedigit.bookstore.controller;

import com.closedigit.bookstore.dto.BookDto;
import com.closedigit.bookstore.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for BookController - Basic CRUD operations
 */
@WebMvcTest(value = BookController.class, excludeAutoConfiguration = {
    org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
})
class BookControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private BookService bookService;
    
    @MockBean
    private com.closedigit.bookstore.security.JwtUtil jwtUtil;
    
    @MockBean
    private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private BookDto testBookDto;
    
    @BeforeEach
    void setUp() {
        testBookDto = new BookDto(
                1L,
                "Test Book",
                "Test Author",
                LocalDate.of(2023, 1, 15),
                "Fiction",
                new BigDecimal("19.99"),
                "9780743273565"
        );
    }
    
    @Test
    @WithMockUser(roles = "USER")
    void getAllBooks_WithUserRole_ShouldReturnBooks() throws Exception {
        Page<BookDto> bookPage = new PageImpl<>(List.of(testBookDto));
        when(bookService.getAllBooks(any())).thenReturn(bookPage);
        
        mockMvc.perform(get("/api/books")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Test Book"))
                .andExpect(jsonPath("$.content[0].author").value("Test Author"));
    }
    
    @Test
    @WithMockUser(roles = "USER")
    void getBookById_WithUserRole_ShouldReturnBook() throws Exception {
        when(bookService.getBookById(1L)).thenReturn(testBookDto);
        
        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Book"))
                .andExpect(jsonPath("$.author").value("Test Author"));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void createBook_WithAdminRole_ShouldCreateBook() throws Exception {
        BookDto newBookDto = BookDto.createRequest(
                "New Book",
                "New Author",
                LocalDate.of(2023, 2, 20),
                "Science Fiction",
                new BigDecimal("24.99"),
                "9780061120084"
        );
        
        BookDto createdBookDto = new BookDto(
                2L,
                "New Book",
                "New Author",
                LocalDate.of(2023, 2, 20),
                "Science Fiction",
                new BigDecimal("24.99"),
                "9780061120084"
        );
        
        when(bookService.createBook(any(BookDto.class))).thenReturn(createdBookDto);
        
        mockMvc.perform(post("/api/books")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newBookDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.title").value("New Book"))
                .andExpect(jsonPath("$.author").value("New Author"));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void updateBook_WithAdminRole_ShouldUpdateBook() throws Exception {
        BookDto updatedBookDto = new BookDto(
                1L,
                "Updated Book",
                "Updated Author",
                LocalDate.of(2023, 1, 15),
                "Updated Fiction",
                new BigDecimal("29.99"),
                "9780743273565"
        );
        
        when(bookService.updateBook(eq(1L), any(BookDto.class))).thenReturn(updatedBookDto);
        
        mockMvc.perform(put("/api/books/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedBookDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Book"))
                .andExpect(jsonPath("$.author").value("Updated Author"))
                .andExpect(jsonPath("$.price").value(29.99));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteBook_WithAdminRole_ShouldDeleteBook() throws Exception {
        mockMvc.perform(delete("/api/books/1")
                .with(csrf()))
                .andExpect(status().isNoContent());
    }
}