package me.ghui.v2er.network.bean;

import org.junit.Test;

import static org.junit.Assert.*;

public class TopicBasicInfoTest {

    @Test
    public void testTopicBasicInfoCreation() {
        TopicBasicInfo info = new TopicBasicInfo();
        assertNotNull(info);
    }

    @Test
    public void testTopicBasicInfoSettersAndGetters() {
        TopicBasicInfo info = new TopicBasicInfo();
        
        // Test id
        info.setId("12345");
        assertEquals("12345", info.getId());
        
        // Test title
        info.setTitle("Test Topic Title");
        assertEquals("Test Topic Title", info.getTitle());
        
        // Test replies
        info.setReplies(42);
        assertEquals(42, info.getReplies());
        
        // Test node
        TopicBasicInfo.Node node = new TopicBasicInfo.Node();
        node.setTitle("Programming");
        node.setLink("/go/programming");
        info.setNode(node);
        assertNotNull(info.getNode());
        assertEquals("Programming", info.getNode().getTitle());
        assertEquals("/go/programming", info.getNode().getLink());
    }

    @Test
    public void testTopicBasicInfoWithNullValues() {
        TopicBasicInfo info = new TopicBasicInfo();
        
        // All values should be null by default
        assertNull(info.getId());
        assertNull(info.getTitle());
        assertEquals(0, info.getReplies());
        assertNull(info.getNode());
        assertNull(info.getAuthor());
    }

    @Test
    public void testNodeClass() {
        TopicBasicInfo.Node node = new TopicBasicInfo.Node();
        
        node.setTitle("Java");
        node.setLink("/go/java");
        node.setAvatar("https://example.com/avatar.png");
        
        assertEquals("Java", node.getTitle());
        assertEquals("/go/java", node.getLink());
        assertEquals("https://example.com/avatar.png", node.getAvatar());
    }

    @Test
    public void testNodeDefaultValues() {
        TopicBasicInfo.Node node = new TopicBasicInfo.Node();
        
        assertNull(node.getTitle());
        assertNull(node.getLink());
        assertNull(node.getAvatar());
    }
}