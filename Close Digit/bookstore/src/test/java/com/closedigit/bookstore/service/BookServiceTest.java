package com.closedigit.bookstore.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.closedigit.bookstore.dto.BookDto;
import com.closedigit.bookstore.entity.Book;
import com.closedigit.bookstore.exception.BookNotFoundException;
import com.closedigit.bookstore.exception.DuplicateIsbnException;
import com.closedigit.bookstore.mapper.BookMapper;
import com.closedigit.bookstore.repository.BookRepository;
import com.closedigit.bookstore.validator.IsbnValidator;

/**
 * Unit tests for BookService
 */
@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @Mock
    private IsbnValidator isbnValidator;

    @InjectMocks
    private BookService bookService;

    private Book testBook;
    private BookDto testBookDto;

    @BeforeEach
    void setUp() {
        testBook = new Book(
                "Test Book",
                "Test Author",
                LocalDate.of(2023, 1, 15),
                new BigDecimal("19.99"),
                "9780743273565");
        testBook.setId(1L);
        testBook.setGenre("Fiction");

        testBookDto = new BookDto(
                1L,
                "Test Book",
                "Test Author",
                LocalDate.of(2023, 1, 15),
                "Fiction",
                new BigDecimal("19.99"),
                "9780743273565");
    }

    @Test
    void getAllBooks_ShouldReturnPageOfBooks() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> bookPage = new PageImpl<>(List.of(testBook));

        when(bookRepository.findAll(pageable)).thenReturn(bookPage);
        when(bookMapper.toDto(testBook)).thenReturn(testBookDto);

        Page<BookDto> result = bookService.getAllBooks(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testBookDto, result.getContent().get(0));

        verify(bookRepository).findAll(pageable);
        verify(bookMapper).toDto(testBook);
    }

    @Test
    void getBookById_WhenBookExists_ShouldReturnBook() {
        Long bookId = 1L;
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(testBook));
        when(bookMapper.toDto(testBook)).thenReturn(testBookDto);

        BookDto result = bookService.getBookById(bookId);

        assertNotNull(result);
        assertEquals(testBookDto, result);

        verify(bookRepository).findById(bookId);
        verify(bookMapper).toDto(testBook);
    }

    @Test
    void getBookById_WhenBookNotExists_ShouldThrowException() {
        Long bookId = 1L;
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> bookService.getBookById(bookId));

        verify(bookRepository).findById(bookId);
        verify(bookMapper, never()).toDto(any());
    }

    @Test
    void createBook_WhenValidBook_ShouldCreateBook() {
        when(isbnValidator.isValidIsbn(testBookDto.isbn())).thenReturn(true);
        when(bookRepository.existsByIsbn(testBookDto.isbn())).thenReturn(false);
        when(bookMapper.toEntity(testBookDto)).thenReturn(testBook);
        when(bookRepository.save(testBook)).thenReturn(testBook);
        when(bookMapper.toDto(testBook)).thenReturn(testBookDto);

        BookDto result = bookService.createBook(testBookDto);

        assertNotNull(result);
        assertEquals(testBookDto, result);

        verify(isbnValidator).isValidIsbn(testBookDto.isbn());
        verify(bookRepository).existsByIsbn(testBookDto.isbn());
        verify(bookMapper).toEntity(testBookDto);
        verify(bookRepository).save(testBook);
        verify(bookMapper).toDto(testBook);
    }

    @Test
    void createBook_WhenInvalidIsbn_ShouldThrowException() {
        when(isbnValidator.isValidIsbn(testBookDto.isbn())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> bookService.createBook(testBookDto));

        verify(isbnValidator).isValidIsbn(testBookDto.isbn());
        verify(bookRepository, never()).existsByIsbn(any());
        verify(bookRepository, never()).save(any());
    }

    @Test
    void createBook_WhenDuplicateIsbn_ShouldThrowException() {
        when(isbnValidator.isValidIsbn(testBookDto.isbn())).thenReturn(true);
        when(bookRepository.existsByIsbn(testBookDto.isbn())).thenReturn(true);

        assertThrows(DuplicateIsbnException.class, () -> bookService.createBook(testBookDto));

        verify(isbnValidator).isValidIsbn(testBookDto.isbn());
        verify(bookRepository).existsByIsbn(testBookDto.isbn());
        verify(bookRepository, never()).save(any());
    }

    @Test
    void updateBook_WhenBookExists_ShouldUpdateBook() {
        Long bookId = 1L;
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(testBook));
        when(bookRepository.save(testBook)).thenReturn(testBook);
        when(bookMapper.toDto(testBook)).thenReturn(testBookDto);

        BookDto result = bookService.updateBook(bookId, testBookDto);
        assertNotNull(result);
        assertEquals(testBookDto, result);

        verify(bookRepository).findById(bookId);
        verify(bookRepository).save(testBook);
        verify(bookMapper).toDto(testBook);
    }

    @Test
    void deleteBook_WhenBookExists_ShouldDeleteBook() {
        Long bookId = 1L;
        when(bookRepository.existsById(bookId)).thenReturn(true);

        bookService.deleteBook(bookId);

        verify(bookRepository).existsById(bookId);
        verify(bookRepository).deleteById(bookId);
    }

    @Test
    void deleteBook_WhenBookNotExists_ShouldThrowException() {
        Long bookId = 1L;
        when(bookRepository.existsById(bookId)).thenReturn(false);

        assertThrows(BookNotFoundException.class, () -> bookService.deleteBook(bookId));

        verify(bookRepository).existsById(bookId);
        verify(bookRepository, never()).deleteById(any());
    }
}