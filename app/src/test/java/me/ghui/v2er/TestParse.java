package me.ghui.v2er;

import org.junit.Test;

/**
 * Created by ghui on 16/05/2017.
 */

public class TestParse {

    @Test
    public void testSplitTime() {
        //  •  36 天前  •  最后回复来自
        String time = "  •  36 天前  •  最后回复来自 ";
        if (!Check.isEmpty(time)) {
            time = time.trim().split("•")[1].trim();
        }
        assert time.equals("36 天前 ");
        System.out.println(System.currentTimeMillis());
    }
}
