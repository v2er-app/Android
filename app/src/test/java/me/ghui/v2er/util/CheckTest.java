package me.ghui.v2er.util;

import org.junit.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class CheckTest {

    @Test
    public void testIsEmpty_withNullString() {
        assertTrue(Check.isEmpty((CharSequence) null));
    }

    @Test
    public void testIsEmpty_withEmptyString() {
        assertTrue(Check.isEmpty(""));
    }

    @Test
    public void testIsEmpty_withNonEmptyString() {
        assertFalse(Check.isEmpty("hello"));
    }

    @Test
    public void testIsEmpty_withWhitespaceString() {
        assertFalse(Check.isEmpty(" "));
        assertFalse(Check.isEmpty("\t"));
        assertFalse(Check.isEmpty("\n"));
    }

    @Test
    public void testNotEmpty_withAllNonEmpty() {
        assertTrue(Check.notEmpty("hello", "world", "test"));
    }

    @Test
    public void testNotEmpty_withOneEmpty() {
        assertFalse(Check.notEmpty("hello", "", "test"));
    }

    @Test
    public void testNotEmpty_withOneNull() {
        assertFalse(Check.notEmpty("hello", null, "test"));
    }

    @Test
    public void testNotEmpty_withNoArguments() {
        assertTrue(Check.notEmpty());
    }

    @Test
    public void testIsEmpty_withNullList() {
        assertTrue(Check.isEmpty((List) null));
    }

    @Test
    public void testIsEmpty_withEmptyList() {
        assertTrue(Check.isEmpty(new ArrayList<>()));
    }

    @Test
    public void testIsEmpty_withNonEmptyList() {
        assertFalse(Check.isEmpty(Arrays.asList("item1", "item2")));
    }

    @Test
    public void testNotEmpty_withNullList() {
        assertFalse(Check.notEmpty((List) null));
    }

    @Test
    public void testNotEmpty_withEmptyList() {
        assertFalse(Check.notEmpty(new ArrayList<>()));
    }

    @Test
    public void testNotEmpty_withNonEmptyList() {
        assertTrue(Check.notEmpty(Arrays.asList("item1", "item2")));
    }
}