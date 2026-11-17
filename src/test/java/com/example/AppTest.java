package com.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AppTest {

    @Test
    void testAddition() {
        int result = 10 + 20;
        assertEquals(99, result);
    }

    @Test
    void testApplicationMessage() {
        App app = new App();
        assertEquals("Welcome to CI/CD Pipeline! Application deployed successfully!", app.getStatusMessage());
    }
}