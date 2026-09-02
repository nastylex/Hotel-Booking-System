package com.hotelbooking.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PasswordHasherTest {

    @Test
    void testHashProducesConsistentOutput() {
        String hash1 = PasswordHasher.hash("password123");
        String hash2 = PasswordHasher.hash("password123");
        assertEquals(hash1, hash2, "Same input should produce same hash");
    }

    @Test
    void testDifferentInputsProduceDifferentHashes() {
        String hash1 = PasswordHasher.hash("password123");
        String hash2 = PasswordHasher.hash("password456");
        assertNotEquals(hash1, hash2, "Different inputs should produce different hashes");
    }

    @Test
    void testVerifyWithCorrectPassword() {
        String hash = PasswordHasher.hash("admin123");
        assertTrue(PasswordHasher.verify("admin123", hash), "Should verify correct password");
    }

    @Test
    void testVerifyWithIncorrectPassword() {
        String hash = PasswordHasher.hash("admin123");
        assertFalse(PasswordHasher.verify("wrongpassword", hash), "Should reject wrong password");
    }

    @Test
    void testHashReturnsHex64Chars() {
        String hash = PasswordHasher.hash("test");
        assertEquals(64, hash.length(), "SHA-256 hash should be 64 hex characters");
        assertTrue(hash.matches("[0-9a-f]{64}"), "Hash should contain only hex characters");
    }

    @Test
    void testEmptyPassword() {
        String hash = PasswordHasher.hash("");
        assertNotNull(hash, "Should handle empty password");
        assertEquals(64, hash.length(), "Should produce valid hash for empty password");
    }
}
