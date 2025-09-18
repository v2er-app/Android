package me.ghui.v2er.network.bean;

import org.junit.Test;
import static org.junit.Assert.*;

public class DailyInfoTest {

    @Test
    public void testGetCheckinDays_withValidFormat() {
        // Test case 1: Standard format
        DailyInfo dailyInfo = new DailyInfo();
        dailyInfo.continuousLoginDayStr = "您已连续登录 123 天";
        assertEquals("123", dailyInfo.getCheckinDays());
    }

    @Test
    public void testGetCheckinDays_withDateInString() {
        // Test case 2: String contains date and other numbers
        DailyInfo dailyInfo = new DailyInfo();
        // Simulate the actual problematic string that might appear
        dailyInfo.continuousLoginDayStr = "ghui 已连续签到 12 天 2024/12/25";
        String result = dailyInfo.getCheckinDays();
        // After fix, it should correctly extract "12"
        assertEquals("12", result);
    }

    @Test
    public void testGetCheckinDays_withUserId() {
        // Test case 3: String contains user ID and days
        DailyInfo dailyInfo = new DailyInfo();
        dailyInfo.continuousLoginDayStr = "用户161290已连续签到 5 天";
        String result = dailyInfo.getCheckinDays();
        // After fix, it should correctly extract "5"
        assertEquals("5", result);
    }

    @Test
    public void testGetCheckinDays_withNull() {
        DailyInfo dailyInfo = new DailyInfo();
        dailyInfo.continuousLoginDayStr = null;
        assertEquals("", dailyInfo.getCheckinDays());
    }

    @Test
    public void testGetCheckinDays_withEmpty() {
        DailyInfo dailyInfo = new DailyInfo();
        dailyInfo.continuousLoginDayStr = "";
        assertEquals("", dailyInfo.getCheckinDays());
    }

    // Make continuousLoginDayStr accessible for testing
    private static class DailyInfo extends me.ghui.v2er.network.bean.DailyInfo {
        String continuousLoginDayStr;

        @Override
        public String getCheckinDays() {
            // Use reflection or make the field package-private in actual implementation
            try {
                java.lang.reflect.Field field = me.ghui.v2er.network.bean.DailyInfo.class.getDeclaredField("continuousLoginDayStr");
                field.setAccessible(true);
                field.set(this, continuousLoginDayStr);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return super.getCheckinDays();
        }
    }
}