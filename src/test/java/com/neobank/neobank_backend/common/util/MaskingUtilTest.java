package com.neobank.neobank_backend.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MaskingUtilTest {

    @Test
    @DisplayName("Should mask valid email properly")
    void testMaskEmail() {
        assertEquals("j***e@domain.com", MaskingUtil.maskEmail("john.doe@domain.com"));
        assertEquals("a***b@test.com", MaskingUtil.maskEmail("ab@test.com"));
        assertEquals("a***@test.com", MaskingUtil.maskEmail("a@test.com"));
        assertNull(MaskingUtil.maskEmail(null));
        assertEquals("", MaskingUtil.maskEmail(""));
    }

    @Test
    @DisplayName("Should mask phone numbers retaining last 4 digits")
    void testMaskPhone() {
        assertEquals("******7890", MaskingUtil.maskPhone("+1234567890"));
        assertEquals("****1234", MaskingUtil.maskPhone("98761234"));
        assertEquals("****", MaskingUtil.maskPhone("123"));
        assertNull(MaskingUtil.maskPhone(null));
    }

    @Test
    @DisplayName("Should mask customer names")
    void testMaskName() {
        assertEquals("J**n", MaskingUtil.maskName("John"));
        assertEquals("A***e", MaskingUtil.maskName("Alice"));
        assertEquals("J*", MaskingUtil.maskName("Jo"));
        assertEquals("J*", MaskingUtil.maskName("J"));
        assertNull(MaskingUtil.maskName(null));
    }

    @Test
    @DisplayName("Should mask generic sensitive string")
    void testMaskGeneric() {
        assertEquals("************3456", MaskingUtil.maskGeneric("1234567890123456", 4));
        assertEquals("****", MaskingUtil.maskGeneric("1234", 4));
        assertNull(MaskingUtil.maskGeneric(null, 4));
    }
}
