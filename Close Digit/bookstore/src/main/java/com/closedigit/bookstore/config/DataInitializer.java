package com.closedigit.bookstore.config;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.closedigit.bookstore.entity.Book;
import com.closedigit.bookstore.entity.Role;
import com.closedigit.bookstore.entity.User;
import com.closedigit.bookstore.repository.BookRepository;
import com.closedigit.bookstore.repository.UserRepository;

/**
 * Sample data
 */
@Component
public class DataInitializer implements CommandLineRunner {

        private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

        private final UserRepository userRepository;
        private final BookRepository bookRepository;
        private final PasswordEncoder passwordEncoder;

        public DataInitializer(UserRepository userRepository, BookRepository bookRepository,
                        PasswordEncoder passwordEncoder) {
                this.userRepository = userRepository;
                this.bookRepository = bookRepository;
                this.passwordEncoder = passwordEncoder;
        }

        @Override
        public void run(String... args) throws Exception {
                logger.info("Initializing sample data...");
                createDefaultUsers();
                createSampleBooks();

                logger.info("Sample data initialization completed");
        }

        /**
         * Create default admin and user accounts
         */
        private void createDefaultUsers() {
                // Create admin user
                if (!userRepository.existsByUsername("admin")) {
                        User admin = new User(
                                        "admin",
                                        "admin@bookstore.com",
                                        passwordEncoder.encode("admin123"),
                                        Role.ADMIN);
                        userRepository.save(admin);
                        logger.info("Default admin user created: admin/admin123");
                }

                // Create regular user
                if (!userRepository.existsByUsername("user")) {
                        User user = new User(
                                        "user",
                                        "user@bookstore.com",
                                        passwordEncoder.encode("user123"),
                                        Role.USER);
                        userRepository.save(user);
                        logger.info("Default user created: user/user123");
                }
        }

        /**
         * Create sample books for testing
         */
        private void createSampleBooks() {
                if (bookRepository.count() == 0) {
                        // Sample books with new structure
                        Book[] sampleBooks = {
                                        createBook("Effective Java", "Joshua Bloch", LocalDate.of(2018, 1, 6),
                                                        "Programming",
                                                        new BigDecimal("32.99"), "9780134685991"),
                                        createBook("Clean Code", "Robert C. Martin", LocalDate.of(2008, 8, 11),
                                                        "Programming",
                                                        new BigDecimal("28.99"), "9780132350884"),
                                        createBook("Head First Java", "Kathy Sierra & Bert Bates",
                                                        LocalDate.of(2005, 2, 9), "Programming",
                                                        new BigDecimal("25.99"), "9780596009205"),
                                        createBook("Spring in Action", "Craig Walls", LocalDate.of(2018, 11, 27),
                                                        "Java Framework",
                                                        new BigDecimal("34.99"), "9781617294945"),
                                        createBook("Java: The Complete Reference", "Herbert Schildt",
                                                        LocalDate.of(2021, 5, 15), "Programming",
                                                        new BigDecimal("30.99"), "9781260440232"),
                                        createBook("Python Crash Course", "Eric Matthes", LocalDate.of(2019, 5, 3),
                                                        "Programming",
                                                        new BigDecimal("27.99"), "9781593279288"),
                                        createBook("Fluent Python", "Luciano Ramalho", LocalDate.of(2022, 4, 19),
                                                        "Programming",
                                                        new BigDecimal("36.99"), "9781492056355"),
                                        createBook("Design Patterns: Elements of Reusable Object-Oriented Software",
                                                        "Erich Gamma",
                                                        LocalDate.of(1994, 10, 31), "Software Design",
                                                        new BigDecimal("39.99"), "9780201633610"),
                                        createBook("Building Microservices", "Sam Newman", LocalDate.of(2021, 1, 12),
                                                        "Architecture",
                                                        new BigDecimal("33.99"), "9781492034025"),
                                        createBook("Cloud Computing: Principles and Paradigms", "Rajkumar Buyya",
                                                        LocalDate.of(2011, 2, 17), "Cloud Computing",
                                                        new BigDecimal("29.99"), "9781118002209")
                        };

                        // Save all sample books
                        for (Book book : sampleBooks) {
                                bookRepository.save(book);
                        }

                        logger.info("Created {} sample books", sampleBooks.length);
                }
        }

        /**
         * Helper method to create a book with all required fields
         */
        private Book createBook(String title, String author, LocalDate publishedDate, String genre, BigDecimal price,
                        String isbn) {
                Book book = new Book(title, author, publishedDate, price, isbn);
                book.setGenre(genre);
                return book;
        }
}