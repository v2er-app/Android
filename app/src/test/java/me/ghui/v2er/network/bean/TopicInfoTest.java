package me.ghui.v2er.network.bean;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Arrays;
import java.util.List;

/**
 * Unit tests for TopicInfo threading and sorting functionality
 */
public class TopicInfoTest {

    @Test
    public void testExtractMentions_SimpleAtUsername() {
        TopicInfo topicInfo = new TopicInfo();
        List<String> mentions = topicInfo.extractMentions("@user1 thanks for the help!");
        assertEquals(1, mentions.size());
        assertEquals("user1", mentions.get(0));
    }

    @Test
    public void testExtractMentions_LinkFormat() {
        TopicInfo topicInfo = new TopicInfo();
        String content = "@<a href=\"/member/user1\">user1</a> what do you think?";
        List<String> mentions = topicInfo.extractMentions(content);
        assertEquals(1, mentions.size());
        assertEquals("user1", mentions.get(0));
    }

    @Test
    public void testExtractMentions_MultipleMentions() {
        TopicInfo topicInfo = new TopicInfo();
        String content = "@user1 @user2 please check this out";
        List<String> mentions = topicInfo.extractMentions(content);
        assertEquals(2, mentions.size());
        assertTrue(mentions.contains("user1"));
        assertTrue(mentions.contains("user2"));
    }

    @Test
    public void testExtractMentions_NoDuplicates() {
        TopicInfo topicInfo = new TopicInfo();
        String content = "@user1 and @user1 again";
        List<String> mentions = topicInfo.extractMentions(content);
        assertEquals(1, mentions.size());
        assertEquals("user1", mentions.get(0));
    }

    @Test
    public void testReplyIndentLevel() {
        TopicInfo.Reply reply = new TopicInfo.Reply();
        
        // Test default indent level
        assertEquals(0, reply.getIndentLevel());
        
        // Test setting indent level
        reply.setIndentLevel(2);
        assertEquals(2, reply.getIndentLevel());
        
        // Test max indent level (should cap at 3)
        reply.setIndentLevel(5);
        assertEquals(3, reply.getIndentLevel());
        
        // Test negative values (should set to 0)
        reply.setIndentLevel(-1);
        assertEquals(0, reply.getIndentLevel());
    }

    @Test
    public void testReplyMentions() {
        TopicInfo.Reply reply = new TopicInfo.Reply();
        
        // Test empty mentions
        assertFalse(reply.hasMentions());
        
        // Test with mentions
        reply.setMentionedUsers(Arrays.asList("user1", "user2"));
        assertTrue(reply.hasMentions());
        assertEquals(2, reply.getMentionedUsers().size());
    }
}