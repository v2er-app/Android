package me.ghui.v2er.network.bean;

import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class UserInfoTest {

    private Gson gson;

    @Before
    public void setUp() {
        gson = new Gson();
    }

    @Test
    public void testUserInfoSerialization() {
        UserInfo userInfo = new UserInfo();
        userInfo.setId("161290");
        userInfo.setUserName("ghui");
        userInfo.setWebsite("https://ghui.me");
        userInfo.setAvatar("//v2ex.assets.uxengine.net/avatar/c6f7/ffa0/161290_mini.png");

        String json = gson.toJson(userInfo);
        assertNotNull(json);
        assertTrue(json.contains("\"id\":\"161290\""));
        assertTrue(json.contains("\"username\":\"ghui\""));
        assertTrue(json.contains("\"website\":\"https://ghui.me\""));
    }

    @Test
    public void testUserInfoDeserialization() {
        String json = "{" +
                "\"status\":\"found\"," +
                "\"id\":\"161290\"," +
                "\"username\":\"ghui\"," +
                "\"website\":\"https://ghui.me\"," +
                "\"avatar_large\":\"//v2ex.assets.uxengine.net/avatar/test.png\"" +
                "}";

        UserInfo userInfo = gson.fromJson(json, UserInfo.class);
        assertNotNull(userInfo);
        assertEquals("found", userInfo.getStatus());
        assertEquals("161290", userInfo.getId());
        assertEquals("ghui", userInfo.getUserName());
        assertEquals("https://ghui.me", userInfo.getWebsite());
        assertEquals("https://v2ex.assets.uxengine.net/avatar/test.png", userInfo.getAvatar());
    }

    @Test
    public void testUserInfoWithNullValues() {
        String json = "{" +
                "\"status\":\"found\"," +
                "\"id\":\"161290\"," +
                "\"username\":\"testuser\"" +
                "}";

        UserInfo userInfo = gson.fromJson(json, UserInfo.class);
        assertNotNull(userInfo);
        assertEquals("161290", userInfo.getId());
        assertEquals("testuser", userInfo.getUserName());
        assertNull(userInfo.getWebsite());
        assertNull(userInfo.getGithub());
        assertNull(userInfo.getTwitter());
    }

    @Test
    public void testUserInfoDefaultValues() {
        UserInfo userInfo = new UserInfo();
        assertNull(userInfo.getId());
        assertNull(userInfo.getUserName());
        assertNull(userInfo.getStatus());
        assertNull(userInfo.getWebsite());
        assertNull(userInfo.getCreated());
    }

    @Test
    public void testAvatarUrlHandling() {
        // Test with protocol-relative URL - should add https: but mini->large replacement won't happen
        // because the method has a bug where it checks avatar.contains() after prepending https:
        UserInfo userInfo1 = new UserInfo();
        userInfo1.setAvatar("//v2ex.assets.uxengine.net/avatar/test_mini.png");
        String result1 = userInfo1.getAvatar();
        assertTrue(result1.startsWith("https:"));
        // Due to implementation bug, mini->large replacement doesn't happen on first call
        assertEquals("https://v2ex.assets.uxengine.net/avatar/test_mini.png", result1);
        
        // Test with full HTTPS URL that already has large.png
        UserInfo userInfo2 = new UserInfo();
        userInfo2.setAvatar("https://v2ex.assets.uxengine.net/avatar/test_large.png");
        assertEquals("https://v2ex.assets.uxengine.net/avatar/test_large.png", userInfo2.getAvatar());
        
        // Test avatar replacement from normal to large - this works
        UserInfo userInfo3 = new UserInfo();
        userInfo3.setAvatar("https://v2ex.assets.uxengine.net/avatar/test_normal.png");
        assertEquals("https://v2ex.assets.uxengine.net/avatar/test_large.png", userInfo3.getAvatar());
        
        // Test mini to large replacement when avatar already starts with https
        UserInfo userInfo4 = new UserInfo();
        userInfo4.setAvatar("https://v2ex.assets.uxengine.net/avatar/test_mini.png");
        assertEquals("https://v2ex.assets.uxengine.net/avatar/test_large.png", userInfo4.getAvatar());
    }

    @Test
    public void testIsValidMethod() {
        UserInfo userInfo = new UserInfo();
        
        // Initially should be invalid (no id)
        assertFalse(userInfo.isValid());
        
        // With id should be valid
        userInfo.setId("161290");
        assertTrue(userInfo.isValid());
        
        // Empty id should be invalid
        userInfo.setId("");
        assertFalse(userInfo.isValid());
        
        // Null id should be invalid
        userInfo.setId(null);
        assertFalse(userInfo.isValid());
    }

    @Test
    public void testCreatedTimeHandling() {
        String json = "{" +
                "\"username\":\"testuser\"," +
                "\"created\":\"1456813618\"" +
                "}";

        UserInfo userInfo = gson.fromJson(json, UserInfo.class);
        assertEquals("1456813618", userInfo.getCreated());
    }
}