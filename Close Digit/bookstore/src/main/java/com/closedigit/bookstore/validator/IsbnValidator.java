package com.closedigit.bookstore.validator;

import org.springframework.stereotype.Component;

/**
 * ISBN format validator
 */
@Component
public class IsbnValidator {

    /**
     * Valid formats:
     * - 10-digit numeric (1234567890)
     * - 13-digit numeric (9781234567890)
     * Must not contain letters or special characters
     */
    public boolean isValidIsbn(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            return false;
        }

        String cleanIsbn = isbn.trim();

        // Check for exactly 10 digits (ISBN-10)
        if (cleanIsbn.length() == 10) {
            return cleanIsbn.matches("^[0-9]{10}$");
        } 
        // Check for exactly 13 digits (ISBN-13)
        else if (cleanIsbn.length() == 13) {
            return cleanIsbn.matches("^[0-9]{13}$");
        }

        return false;
    }

}