package com.closedigit.bookstore.validator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Unit tests for IsbnValidator
 */
class IsbnValidatorTest {
    
    private IsbnValidator isbnValidator;
    
    @BeforeEach
    void setUp() {
        isbnValidator = new IsbnValidator();
    }
    
    @ParameterizedTest
    @ValueSource(strings = {
            "9780743273565",
            "9780061120084",
            "9780452284234",
            "1234567890123"
    })
    void isValidIsbn_WithValidIsbn13_ShouldReturnTrue(String isbn) {
        assertTrue(isbnValidator.isValidIsbn(isbn));
    }
    
    @ParameterizedTest
    @ValueSource(strings = {
            "0306406152",
            "0747532699",
            "1234567890",
            "9876543210"
    })
    void isValidIsbn_WithValidIsbn10_ShouldReturnTrue(String isbn) {
        assertTrue(isbnValidator.isValidIsbn(isbn));
    }
    
    @ParameterizedTest
    @ValueSource(strings = {
            "978-0-7432-7356-5", // Contains hyphens (not allowed per assignment)
            "978 0 7432 7356 5",  // Contains spaces
            "ISBN-13: 9780743273565", // Contains prefix
            "123-4-5678-9012-3", // Contains hyphens
            "0-306-40615-2",     // Contains hyphens
            "074753269X",        // Contains letter X
            "12345678901234",    // Too long (14 digits)
            "123456789",         // Too short (9 digits)
            "12345678901",       // Invalid length (11 digits)
            "123456789012",      // Invalid length (12 digits)
            "",                  // Empty string
            "   ",               // Whitespace only
            "abc1234567890",     // Contains letters
            "123456789a",        // Contains letter
            "978a743273565",     // Contains letter in middle
            "978-0743273565"     // Mixed format
    })
    void isValidIsbn_WithInvalidIsbn_ShouldReturnFalse(String isbn) {
        assertFalse(isbnValidator.isValidIsbn(isbn));
    }
    
    @Test
    void isValidIsbn_WithNull_ShouldReturnFalse() {
        assertFalse(isbnValidator.isValidIsbn(null));
    }
    

    
    @Test
    void isValidIsbn_WithLeadingTrailingSpaces_ShouldHandleCorrectly() {
        assertTrue(isbnValidator.isValidIsbn("  9780743273565  "));
        assertTrue(isbnValidator.isValidIsbn("  0306406152  "));
    }
    
    @Test
    void isValidIsbn_WithExactly10Digits_ShouldReturnTrue() {
        assertTrue(isbnValidator.isValidIsbn("0000000000"));
        assertTrue(isbnValidator.isValidIsbn("9999999999"));
    }
    
    @Test
    void isValidIsbn_WithExactly13Digits_ShouldReturnTrue() {
        assertTrue(isbnValidator.isValidIsbn("0000000000000"));
        assertTrue(isbnValidator.isValidIsbn("9999999999999"));
    }
}