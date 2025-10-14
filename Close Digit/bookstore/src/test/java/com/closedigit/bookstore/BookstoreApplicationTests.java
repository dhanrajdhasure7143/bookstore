package com.closedigit.bookstore;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.transaction.annotation.Transactional;

import com.closedigit.bookstore.dto.AuthRequest;
import com.closedigit.bookstore.dto.AuthResponse;
import com.closedigit.bookstore.dto.BookDto;
import com.closedigit.bookstore.dto.RegisterRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Integration tests for the Bookstore application
 */
@SpringBootTest
@AutoConfigureWebMvc
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class BookstoreApplicationTests {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @Test
        void contextLoads() {
        }

        @Test
        void fullWorkflow_RegisterLoginCreateBookSearchBook_ShouldWork() throws Exception {
                // 1. Register a new admin user
                RegisterRequest registerRequest = new RegisterRequest(
                                "testadmin",
                                "testadmin@test.com",
                                "password123");

                MvcResult registerResult = mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registerRequest)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.token").exists())
                                .andExpect(jsonPath("$.user.username").value("testadmin"))
                                .andReturn();

                String registerResponse = registerResult.getResponse().getContentAsString();
                AuthResponse authResponse = objectMapper.readValue(registerResponse, AuthResponse.class);
                String userToken = authResponse.token();

                AuthRequest loginRequest = new AuthRequest("admin", "admin123");

                MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.token").exists())
                                .andExpect(jsonPath("$.user.username").value("admin"))
                                .andExpect(jsonPath("$.user.role").value("ADMIN"))
                                .andReturn();

                String loginResponse = loginResult.getResponse().getContentAsString();
                AuthResponse adminAuthResponse = objectMapper.readValue(loginResponse, AuthResponse.class);
                String adminToken = adminAuthResponse.token();

                // 3. Create a new book as admin
                BookDto newBook = BookDto.createRequest(
                                "Integration Test Book",
                                "Test Author",
                                LocalDate.of(2024, 1, 15),
                                "Fiction",
                                new BigDecimal("25.99"),
                                "9780123456789");

                MvcResult createBookResult = mockMvc.perform(post("/api/books")
                                .header("Authorization", "Bearer " + adminToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(newBook)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.title").value("Integration Test Book"))
                                .andExpect(jsonPath("$.author").value("Test Author"))
                                .andExpect(jsonPath("$.genre").value("Fiction"))
                                .andExpect(jsonPath("$.isbn").value("9780123456789"))
                                .andExpect(jsonPath("$.price").value(25.99))
                                .andReturn();

                String createBookResponse = createBookResult.getResponse().getContentAsString();
                BookDto createdBook = objectMapper.readValue(createBookResponse, BookDto.class);
                Long bookId = createdBook.id();

                mockMvc.perform(get("/api/books")
                                .header("Authorization", "Bearer " + userToken))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content").isArray());

                mockMvc.perform(get("/api/books/" + bookId)
                                .header("Authorization", "Bearer " + userToken))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.title").value("Integration Test Book"))
                                .andExpect(jsonPath("$.author").value("Test Author"));

                BookDto updatedBook = BookDto.updateRequest(
                                bookId,
                                "Updated Title",
                                "Test Author",
                                LocalDate.of(2024, 1, 15),
                                "Updated Fiction",
                                new BigDecimal("29.99"),
                                "9780123456789");

                mockMvc.perform(put("/api/books/" + bookId)
                                .header("Authorization", "Bearer " + userToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updatedBook)))
                                .andExpect(status().isForbidden());

                mockMvc.perform(put("/api/books/" + bookId)
                                .header("Authorization", "Bearer " + adminToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updatedBook)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.title").value("Updated Title"))
                                .andExpect(jsonPath("$.price").value(29.99));

                mockMvc.perform(get("/api/books")
                                .header("Authorization", "Bearer " + userToken)
                                .param("page", "0")
                                .param("size", "5"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content").isArray())
                                .andExpect(jsonPath("$.totalElements").exists())
                                .andExpect(jsonPath("$.totalPages").exists());

                mockMvc.perform(delete("/api/books/" + bookId)
                                .header("Authorization", "Bearer " + adminToken))
                                .andExpect(status().isNoContent());

                mockMvc.perform(get("/api/books/" + bookId)
                                .header("Authorization", "Bearer " + userToken))
                                .andExpect(status().isNotFound());
        }

        @Test
        void authentication_WithInvalidCredentials_ShouldReturnUnauthorized() throws Exception {
                AuthRequest invalidLogin = new AuthRequest("invalid", "invalid");

                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidLogin)))
                                .andExpect(status().isUnauthorized());
        }

        @Test
        void bookOperations_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
                mockMvc.perform(get("/api/books"))
                                .andExpect(status().isForbidden()); 

                BookDto newBook = BookDto.createRequest(
                                "Unauthorized Book",
                                "Unknown Author",
                                LocalDate.of(2024, 1, 1),
                                "Mystery",
                                new BigDecimal("10.00"),
                                "9780000000000");

                mockMvc.perform(post("/api/books")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(newBook)))
                                .andExpect(status().isForbidden()); 
        }

        @Test
        void bookFiltering_ByGenreAndYear_ShouldWork() throws Exception {
                AuthRequest loginRequest = new AuthRequest("admin", "admin123");
                MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest)))
                                .andExpect(status().isOk())
                                .andReturn();

                String loginResponse = loginResult.getResponse().getContentAsString();
                AuthResponse authResponse = objectMapper.readValue(loginResponse, AuthResponse.class);
                String adminToken = authResponse.token();

                BookDto fictionBook = BookDto.createRequest(
                                "Fiction Book",
                                "Fiction Author",
                                LocalDate.of(2023, 5, 10),
                                "Fiction",
                                new BigDecimal("19.99"),
                                "9781111111111");

                BookDto sciFiBook = BookDto.createRequest(
                                "SciFi Book",
                                "SciFi Author",
                                LocalDate.of(2024, 3, 15),
                                "Science Fiction",
                                new BigDecimal("24.99"),
                                "9782222222222");

                MvcResult fictionResult = mockMvc.perform(post("/api/books")
                                .header("Authorization", "Bearer " + adminToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(fictionBook)))
                                .andExpect(status().isCreated())
                                .andReturn();

                MvcResult sciFiResult = mockMvc.perform(post("/api/books")
                                .header("Authorization", "Bearer " + adminToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(sciFiBook)))
                                .andExpect(status().isCreated())
                                .andReturn();

                mockMvc.perform(get("/api/books")
                                .header("Authorization", "Bearer " + adminToken))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content").isArray());

                String fictionResponse = fictionResult.getResponse().getContentAsString();
                BookDto createdFiction = objectMapper.readValue(fictionResponse, BookDto.class);

                String sciFiResponse = sciFiResult.getResponse().getContentAsString();
                BookDto createdSciFi = objectMapper.readValue(sciFiResponse, BookDto.class);

                mockMvc.perform(delete("/api/books/" + createdFiction.id())
                                .header("Authorization", "Bearer " + adminToken))
                                .andExpect(status().isNoContent());

                mockMvc.perform(delete("/api/books/" + createdSciFi.id())
                                .header("Authorization", "Bearer " + adminToken))
                                .andExpect(status().isNoContent());
        }

        @Test
        void bookValidation_WithInvalidData_ShouldReturnBadRequest() throws Exception {
                AuthRequest loginRequest = new AuthRequest("admin", "admin123");
                MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest)))
                                .andExpect(status().isOk())
                                .andReturn();

                String loginResponse = loginResult.getResponse().getContentAsString();
                AuthResponse authResponse = objectMapper.readValue(loginResponse, AuthResponse.class);
                String adminToken = authResponse.token();

                BookDto invalidTitleBook = new BookDto(
                                null,
                                "A".repeat(101),
                                "Valid Author",
                                LocalDate.of(2024, 1, 1),
                                "Fiction",
                                new BigDecimal("19.99"),
                                "9780123456789");

                mockMvc.perform(post("/api/books")
                                .header("Authorization", "Bearer " + adminToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidTitleBook)))
                                .andExpect(status().isBadRequest());

                BookDto invalidAuthorBook = new BookDto(
                                null,
                                "Valid Title",
                                "A".repeat(51),
                                LocalDate.of(2024, 1, 1),
                                "Fiction",
                                new BigDecimal("19.99"),
                                "9780123456789");

                mockMvc.perform(post("/api/books")
                                .header("Authorization", "Bearer " + adminToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidAuthorBook)))
                                .andExpect(status().isBadRequest());

                BookDto invalidPriceBook = new BookDto(
                                null,
                                "Valid Title",
                                "Valid Author",
                                LocalDate.of(2024, 1, 1),
                                "Fiction",
                                new BigDecimal("-10.00"), 
                                "9780123456789");

                mockMvc.perform(post("/api/books")
                                .header("Authorization", "Bearer " + adminToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidPriceBook)))
                                .andExpect(status().isBadRequest());
        }
}
