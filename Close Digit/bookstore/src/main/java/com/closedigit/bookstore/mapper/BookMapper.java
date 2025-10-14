package com.closedigit.bookstore.mapper;

import com.closedigit.bookstore.dto.BookDto;
import com.closedigit.bookstore.entity.Book;
import org.springframework.stereotype.Component;

/**
 * Mapper class for converting between Book entity and BookDto
 * Uses manual mapping for better control and understanding
 */
@Component
public class BookMapper {

    /**
     * Convert Book entity to BookDto
     */
    public BookDto toDto(Book book) {
        if (book == null) {
            return null;
        }

        return new BookDto(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getPublishedDate(),
                book.getGenre(),
                book.getPrice(),
                book.getIsbn());
    }

    /**
     * Convert BookDto to Book entity
     */
    public Book toEntity(BookDto bookDto) {
        if (bookDto == null) {
            return null;
        }

        Book book = new Book();
        book.setId(bookDto.id());
        book.setTitle(bookDto.title());
        book.setAuthor(bookDto.author());
        book.setPublishedDate(bookDto.publishedDate());
        book.setGenre(bookDto.genre());
        book.setPrice(bookDto.price());
        book.setIsbn(bookDto.isbn());

        return book;
    }

    /**
     * Update existing Book entity with BookDto data
     */
    public void updateEntityFromDto(BookDto bookDto, Book book) {
        if (bookDto == null || book == null) {
            return;
        }

        book.setTitle(bookDto.title());
        book.setAuthor(bookDto.author());
        book.setPublishedDate(bookDto.publishedDate());
        book.setGenre(bookDto.genre());
        book.setPrice(bookDto.price());
        book.setIsbn(bookDto.isbn());

        // ID not updated manually
    }
}