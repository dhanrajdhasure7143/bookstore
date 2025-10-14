package com.closedigit.bookstore.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.closedigit.bookstore.dto.BookDto;
import com.closedigit.bookstore.entity.Book;
import com.closedigit.bookstore.exception.BookNotFoundException;
import com.closedigit.bookstore.exception.DuplicateIsbnException;
import com.closedigit.bookstore.mapper.BookMapper;
import com.closedigit.bookstore.repository.BookRepository;
import com.closedigit.bookstore.validator.IsbnValidator;

/**
 * Service class for Book operations
 * Handles basic CRUD operations for book management
 */
@Service
@Transactional
public class BookService {

    private static final Logger logger = LoggerFactory.getLogger(BookService.class);

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final IsbnValidator isbnValidator;

    public BookService(BookRepository bookRepository, BookMapper bookMapper, IsbnValidator isbnValidator) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
        this.isbnValidator = isbnValidator;
    }

    @Transactional(readOnly = true)
    public Page<BookDto> getAllBooks(Pageable pageable) {
        logger.debug("Fetching all books with pagination and sorting: {}", pageable);
        return bookRepository.findAll(pageable)
                .map(bookMapper::toDto);
    }

    @Transactional(readOnly = true)
    public BookDto getBookById(Long id) {
        logger.debug("Fetching book with ID: {}", id);
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found with ID: " + id));
        return bookMapper.toDto(book);
    }

    public BookDto createBook(BookDto bookDto) {
        logger.debug("Creating new book: {}", bookDto.title());

        if (!isbnValidator.isValidIsbn(bookDto.isbn())) {
            throw new IllegalArgumentException("Invalid ISBN format: " + bookDto.isbn());
        }

        if (bookRepository.existsByIsbn(bookDto.isbn())) {
            throw new DuplicateIsbnException("Book with ISBN " + bookDto.isbn() + " already exists");
        }

        Book book = bookMapper.toEntity(bookDto);
        Book savedBook = bookRepository.save(book);

        logger.info("Book created successfully with ID: {}", savedBook.getId());
        return bookMapper.toDto(savedBook);
    }

    public BookDto updateBook(Long id, BookDto bookDto) {
        logger.debug("Updating book with ID: {}", id);

        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found with ID: " + id));

        if (!existingBook.getIsbn().equals(bookDto.isbn())) {
            if (!isbnValidator.isValidIsbn(bookDto.isbn())) {
                throw new IllegalArgumentException("Invalid ISBN format: " + bookDto.isbn());
            }

            if (bookRepository.existsByIsbn(bookDto.isbn())) {
                throw new DuplicateIsbnException("Book with ISBN " + bookDto.isbn() + " already exists");
            }
        }

        existingBook.setTitle(bookDto.title());
        existingBook.setAuthor(bookDto.author());
        existingBook.setPublishedDate(bookDto.publishedDate());
        existingBook.setGenre(bookDto.genre());
        existingBook.setPrice(bookDto.price());
        existingBook.setIsbn(bookDto.isbn());

        Book updatedBook = bookRepository.save(existingBook);

        logger.info("Book updated successfully with ID: {}", updatedBook.getId());
        return bookMapper.toDto(updatedBook);
    }

    public void deleteBook(Long id) {
        logger.debug("Deleting book with ID: {}", id);

        if (!bookRepository.existsById(id)) {
            throw new BookNotFoundException("Book not found with ID: " + id);
        }

        bookRepository.deleteById(id);
        logger.info("Book deleted successfully with ID: {}", id);
    }
}