package com.closedigit.bookstore.repository;

import com.closedigit.bookstore.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Book entity
 * Extends JpaRepository for basic CRUD operations
 */
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    
    boolean existsByIsbn(String isbn);
}