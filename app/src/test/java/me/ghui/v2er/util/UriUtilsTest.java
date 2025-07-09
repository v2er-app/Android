package me.ghui.v2er.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class UriUtilsTest {

    @Test
    public void testGetLastSegment_withSimpleUrl() {
        assertEquals("page", UriUtils.getLastSegment("http://example.com/page"));
        assertEquals("file.html", UriUtils.getLastSegment("http://example.com/path/to/file.html"));
    }

    @Test
    public void testGetLastSegment_withQueryParams() {
        assertEquals("page", UriUtils.getLastSegment("http://example.com/page?param=value"));
        assertEquals("file", UriUtils.getLastSegment("http://example.com/path/file?a=1&b=2"));
    }

    @Test
    public void testGetLastSegment_withAnchor() {
        assertEquals("page", UriUtils.getLastSegment("http://example.com/page#section"));
        assertEquals("file", UriUtils.getLastSegment("http://example.com/path/file#top"));
    }

    @Test
    public void testGetLastSegment_withAnchorAndParams() {
        assertEquals("page", UriUtils.getLastSegment("http://example.com/page?param=value#section"));
    }

    @Test
    public void testGetLastSegment_withEmptyOrNull() {
        assertEquals("", UriUtils.getLastSegment(""));
        assertEquals("", UriUtils.getLastSegment(null));
    }

    @Test
    public void testGetLastSegment_withTrailingSlash() {
        assertEquals("page", UriUtils.getLastSegment("http://example.com/page/"));
    }

    // Note: getParamValue tests removed as they require Android's Uri class

    @Test
    public void testCheckSchema_withHttpUrl() {
        assertEquals("http://example.com", UriUtils.checkSchema("http://example.com"));
        assertEquals("https://example.com", UriUtils.checkSchema("https://example.com"));
    }

    @Test
    public void testCheckSchema_withoutSchema() {
        assertEquals("http://example.com", UriUtils.checkSchema("example.com"));
        assertEquals("https://i.v2ex.co/image.png", UriUtils.checkSchema("i.v2ex.co/image.png"));
    }

    @Test
    public void testCheckSchema_withDoubleSlash() {
        assertEquals("http://example.com", UriUtils.checkSchema("//example.com"));
        assertEquals("https://i.v2ex.co", UriUtils.checkSchema("//i.v2ex.co"));
    }

    @Test
    public void testCheckSchema_withEmptyOrNull() {
        assertNull(UriUtils.checkSchema(""));
        assertNull(UriUtils.checkSchema(null));
    }

    @Test
    public void testCheckSchema_withInvalidUrl() {
        assertEquals("", UriUtils.checkSchema("http://[invalid url]"));
    }

    @Test
    public void testIsValideUrl_withValidUrls() {
        assertTrue(UriUtils.isValideUrl("http://example.com"));
        assertTrue(UriUtils.isValideUrl("https://example.com/path"));
        assertTrue(UriUtils.isValideUrl("http://example.com:8080/path"));
    }

    @Test
    public void testIsValideUrl_withInvalidUrls() {
        assertFalse(UriUtils.isValideUrl("not a url"));
        assertFalse(UriUtils.isValideUrl("http://[invalid]"));
        assertFalse(UriUtils.isValideUrl(""));
    }

    @Test
    public void testTopicLink() {
        assertEquals("https://www.v2ex.com/t/12345", UriUtils.topicLink("12345"));
        assertEquals("https://www.v2ex.com/t/test", UriUtils.topicLink("test"));
    }

    @Test
    public void testGetMimeType_withImageExtensions() {
        assertEquals("data:image/png;base64,", UriUtils.getMimeType("image.png"));
        assertEquals("data:image/png;base64,", UriUtils.getMimeType("IMAGE.PNG"));
        assertEquals("data:image/jpg;base64,", UriUtils.getMimeType("photo.jpg"));
        assertEquals("data:image/jpg;base64,", UriUtils.getMimeType("photo.jpeg"));
        assertEquals("data:image/jpg;base64,", UriUtils.getMimeType("PHOTO.JPG"));
        assertEquals("data:image/gif;base64,", UriUtils.getMimeType("animation.gif"));
        assertEquals("data:image/gif;base64,", UriUtils.getMimeType("ANIMATION.GIF"));
    }

    @Test
    public void testGetMimeType_withNonImageExtensions() {
        assertEquals("", UriUtils.getMimeType("document.pdf"));
        assertEquals("", UriUtils.getMimeType("file.txt"));
        assertEquals("", UriUtils.getMimeType("no_extension"));
    }

    @Test
    public void testIsImg_withImageUrls() {
        assertTrue(UriUtils.isImg("http://example.com/image.png"));
        assertTrue(UriUtils.isImg("https://example.com/path/to/photo.jpg"));
        assertTrue(UriUtils.isImg("http://example.com/image.jpeg"));
        assertTrue(UriUtils.isImg("http://example.com/animation.gif"));
        assertTrue(UriUtils.isImg("http://example.com/IMAGE.PNG"));
        assertTrue(UriUtils.isImg("http://example.com/image.png?param=value"));
        assertTrue(UriUtils.isImg("http://example.com/image.png#anchor"));
    }

    @Test
    public void testIsImg_withNonImageUrls() {
        assertFalse(UriUtils.isImg("http://example.com/document.pdf"));
        assertFalse(UriUtils.isImg("http://example.com/page"));
        assertFalse(UriUtils.isImg("http://example.com/file.txt"));
        assertFalse(UriUtils.isImg("not_a_url.png"));
    }
}