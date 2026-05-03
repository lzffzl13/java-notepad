package com.cloudnote.util;

import com.cloudnote.common.util.StringUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringUtilsTest {

    @Test
    void isEmptyShouldReturnTrueForNull() {
        assertTrue(StringUtils.isEmpty(null));
    }

    @Test
    void isEmptyShouldReturnTrueForEmpty() {
        assertTrue(StringUtils.isEmpty(""));
        assertTrue(StringUtils.isEmpty("   "));
    }

    @Test
    void isEmptyShouldReturnFalseForNonEmpty() {
        assertFalse(StringUtils.isEmpty("hello"));
        assertFalse(StringUtils.isEmpty(" hello "));
    }

    @Test
    void truncateShouldNotTruncateShortString() {
        assertEquals("abc", StringUtils.truncate("abc", 10));
    }

    @Test
    void truncateShouldTruncateLongString() {
        assertEquals("abcde...", StringUtils.truncate("abcdefghij", 5));
    }

    @Test
    void truncateShouldHandleNull() {
        assertEquals("", StringUtils.truncate(null, 5));
    }
}
