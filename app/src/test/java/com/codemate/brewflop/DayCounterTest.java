package com.codemate.brewflop;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

/**
 * Created by iiro on 22.9.2016.
 */
@RunWith(RobolectricTestRunner.class)
public class DayCounterTest {
    private DayCounter dayCounter;

    @Before
    public void setUp() {
        dayCounter = new DayCounter(RuntimeEnvironment.application.getApplicationContext());
    }

    @After
    public void tearDown() {
        dayCounter.clear();
    }

    @Test
    public void testIncrement() {
        assertThat(dayCounter.getDayCount(), is(0));

        dayCounter.increment();
        dayCounter.increment();
        assertThat(dayCounter.getDayCount(), is(2));
    }

    @Test
    public void testClear() {
        dayCounter.increment();
        dayCounter.increment();
        assertThat(dayCounter.getDayCount(), is(2));

        dayCounter.clear();
        assertThat(dayCounter.getDayCount(), is(0));
    }
}