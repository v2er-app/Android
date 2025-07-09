package me.ghui.v2er.util;

import org.junit.Test;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static org.junit.Assert.*;

public class DateUtilsTest {

    @Test
    public void testParseDate_withValidTimestamp() {
        // Using a known timestamp: January 1, 2024, 12:30:00 UTC
        // 1704112200000L = January 1, 2024, 12:30:00 UTC
        long timestamp = 1704112200000L;
        
        // Create expected formatter with China locale
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm", Locale.CHINA);
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+8")); // China time zone
        String expected = formatter.format(new Date(timestamp));
        
        String actual = DateUtils.parseDate(timestamp);
        
        // The result depends on the system timezone, so we verify format
        assertNotNull(actual);
        assertEquals(5, actual.length());
        assertTrue(actual.matches("\\d{2}:\\d{2}"));
    }

    @Test
    public void testParseDate_withZeroTimestamp() {
        // Zero timestamp = January 1, 1970, 00:00:00 UTC
        String result = DateUtils.parseDate(0L);
        
        assertNotNull(result);
        assertEquals(5, result.length());
        assertTrue(result.matches("\\d{2}:\\d{2}"));
    }

    @Test
    public void testParseDate_withCurrentTime() {
        long currentTime = System.currentTimeMillis();
        String result = DateUtils.parseDate(currentTime);
        
        assertNotNull(result);
        assertEquals(5, result.length());
        assertTrue(result.matches("\\d{2}:\\d{2}"));
    }

    @Test
    public void testParseDate_withNegativeTimestamp() {
        // Negative timestamp = before January 1, 1970
        String result = DateUtils.parseDate(-1000L);
        
        assertNotNull(result);
        assertEquals(5, result.length());
        assertTrue(result.matches("\\d{2}:\\d{2}"));
    }

    @Test
    public void testParseDate_formatValidation() {
        long timestamp = System.currentTimeMillis();
        String result = DateUtils.parseDate(timestamp);
        
        // Validate format HH:mm
        String[] parts = result.split(":");
        assertEquals(2, parts.length);
        
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        
        assertTrue(hours >= 0 && hours <= 23);
        assertTrue(minutes >= 0 && minutes <= 59);
    }
}