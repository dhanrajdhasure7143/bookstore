package com.closedigit.bookstore.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Book entity representing a book in the bookstore
 * Uses JPA annotations for database mapping and validation
 */
@Entity
@Table(name = "books")
public class Book {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 100, message = "Title must be between 1 and 100 characters")
    @Column(nullable = false, length = 100)
    private String title;
    
    @NotBlank(message = "Author is required")
    @Size(min = 1, max = 50, message = "Author must be between 1 and 50 characters")
    @Column(nullable = false, length = 50)
    private String author;
    
    @NotNull(message = "Published date is required")
    @Column(name = "published_date", nullable = false)
    private LocalDate publishedDate;
    
    @Size(max = 50, message = "Genre must not exceed 50 characters")
    @Column(length = 50)
    private String genre;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Price must have at most 2 decimal places")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;
    
    @NotBlank(message = "ISBN is required")
    @Column(unique = true, nullable = false)
    private String isbn;
    
    public Book() {}
    
    public Book(String title, String author, LocalDate publishedDate, BigDecimal price, String isbn) {
        this.title = title;
        this.author = author;
        this.publishedDate = publishedDate;
        this.price = price;
        this.isbn = isbn;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }
    
    public LocalDate getPublishedDate() {
        return publishedDate;
    }
    
    public void setPublishedDate(LocalDate publishedDate) {
        this.publishedDate = publishedDate;
    }
    
    public String getGenre() {
        return genre;
    }
    
    public void setGenre(String genre) {
        this.genre = genre;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public String getIsbn() {
        return isbn;
    }
    
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
    
    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", publishedDate=" + publishedDate +
                ", genre='" + genre + '\'' +
                ", price=" + price +
                ", isbn='" + isbn + '\'' +
                '}';
    }
}