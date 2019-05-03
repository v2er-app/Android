package me.ghui.v2er;

import junit.framework.Assert;

import org.junit.Test;

public class TestReplace {
    @Test
    public void testReplaceString() {
        String str = "abc\nfdafa";
        str = str.replaceAll("\n", "\n\n");
        Assert.assertEquals(str, "abc\n\nfdafa");
    }
}
