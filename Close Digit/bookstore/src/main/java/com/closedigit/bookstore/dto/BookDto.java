package com.closedigit.bookstore.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Book DTO
 */
public record BookDto(
        Long id,
        
        @NotBlank(message = "Title is required")
        @Size(min = 1, max = 100, message = "Title must be between 1 and 100 characters")
        String title,
        
        @NotBlank(message = "Author is required")
        @Size(min = 1, max = 50, message = "Author must be between 1 and 50 characters")
        String author,
        
        @NotNull(message = "Published date is required")
        LocalDate publishedDate,
        
        @Size(max = 50, message = "Genre must not exceed 50 characters")
        String genre,
        
        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
        @Digits(integer = 10, fraction = 2, message = "Price must have at most 2 decimal places")
        BigDecimal price,
        
        @NotBlank(message = "ISBN is required")
        String isbn
) {
    
    /**
     * Create a BookDto for requests (without id)
     */
    public static BookDto createRequest(String title, String author, LocalDate publishedDate, 
                                      String genre, BigDecimal price, String isbn) {
        return new BookDto(null, title, author, publishedDate, genre, price, isbn);
    }
    
    /**
     * Create a BookDto for updates (with id)
     */
    public static BookDto updateRequest(Long id, String title, String author, LocalDate publishedDate, 
                                      String genre, BigDecimal price, String isbn) {
        return new BookDto(id, title, author, publishedDate, genre, price, isbn);
    }
}