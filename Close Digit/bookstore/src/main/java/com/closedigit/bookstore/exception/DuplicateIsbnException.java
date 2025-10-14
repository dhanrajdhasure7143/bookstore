package com.closedigit.bookstore.exception;

/**
 * Exception thrown when trying to create a book with duplicate ISBN
 */
public class DuplicateIsbnException extends RuntimeException {
    
    public DuplicateIsbnException(String message) {
        super(message);
    }
    
    public DuplicateIsbnException(String message, Throwable cause) {
        super(message, cause);
    }
}