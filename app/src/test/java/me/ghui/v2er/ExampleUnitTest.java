package me.ghui.v2er;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testFindDigital() {
        String text = "你有100元红包";
        Pattern pattern = Pattern.compile("\\d[\\d.,]*(?=元)");
        Matcher matcher = pattern.matcher(text);
        String result = matcher.group();
        assertEquals("100", result);
    }
}