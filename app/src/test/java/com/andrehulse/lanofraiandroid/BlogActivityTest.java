package com.andrehulse.lanofraiandroid;

import android.test.ActivityUnitTestCase;

import com.andrehulse.lanofraiandroid.activity.BlogActivity;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by Andre on 28/08/2015.
 */
public class BlogActivityTest extends ActivityUnitTestCase<BlogActivity> {

    public BlogActivityTest() {
            super(BlogActivity.class);
    }

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testOnCreate() throws Exception {
        assertEquals(1,2);
    }

    @Test
    public void failThisShiet(){
        assertEquals(true, false);
    }
}