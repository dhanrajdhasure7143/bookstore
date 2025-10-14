package com.closedigit.bookstore.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.closedigit.bookstore.dto.BookDto;
import com.closedigit.bookstore.service.BookService;

import jakarta.validation.Valid;

/**
 * REST Controller for Book operations
 * Handles basic CRUD operations for books
 */
@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "*", maxAge = 3600)
public class BookController {

    private static final Logger logger = LoggerFactory.getLogger(BookController.class);

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    /**
     * Get all books with pagination and sorting
     */
    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Page<BookDto>> getAllBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        logger.debug("Getting all books - page: {}, size: {}, sortBy: {}, sortDir: {}", page, size, sortBy, sortDir);

        String[] validSortFields = {"id", "title", "author", "publishedDate", "genre", "price", "isbn"};
        boolean isValidSortField = false;
        for (String field : validSortFields) {
            if (field.equalsIgnoreCase(sortBy)) {
                sortBy = field;
                isValidSortField = true;
                break;
            }
        }
        
        if (!isValidSortField) {
            logger.warn("Invalid sort field requested: {}, defaulting to 'title'", sortBy);
            sortBy = "title";
        }

        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<BookDto> books = bookService.getAllBooks(pageable);

        return ResponseEntity.ok(books);
    }

    /**
     * Get book by ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<BookDto> getBookById(@PathVariable Long id) {
        logger.debug("Getting book with ID: {}", id);

        BookDto book = bookService.getBookById(id);
        return ResponseEntity.ok(book);
    }

    /**
     * Create a new book (Admin only)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookDto> createBook(@Valid @RequestBody BookDto bookDto) {
        logger.info("Creating new book: {}", bookDto.title());

        BookDto createdBook = bookService.createBook(bookDto);

        logger.info("Book created successfully with ID: {}", createdBook.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBook);
    }

    /**
     * Update an existing book (Admin only)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookDto> updateBook(@PathVariable Long id, @Valid @RequestBody BookDto bookDto) {
        logger.info("Updating book with ID: {}", id);

        BookDto updatedBook = bookService.updateBook(id, bookDto);

        logger.info("Book updated successfully with ID: {}", id);
        return ResponseEntity.ok(updatedBook);
    }

    /**
     * Delete a book (Admin only)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        logger.info("Deleting book with ID: {}", id);

        bookService.deleteBook(id);

        logger.info("Book deleted successfully with ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}