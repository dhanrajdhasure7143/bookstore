package com.closedigit.bookstore.exception;

/**
 * Exception thrown when a book is not found
 */
public class BookNotFoundException extends RuntimeException {
    
    public BookNotFoundException(String message) {
        super(message);
    }
    
    public BookNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}