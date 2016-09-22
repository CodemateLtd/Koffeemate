package com.codemate.brewflop;

import android.content.Context;
import android.content.Intent;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by iiro on 22.9.2016.
 */
@RunWith(RobolectricTestRunner.class)
public class BootReceiverTest {
    private Context context;
    private BootReceiver bootReceiver;
    private DayCounter dayCounter;

    @Before
    public void setUp() {
        context = RuntimeEnvironment.application.getApplicationContext();
        bootReceiver = new BootReceiver();
        dayCounter = mock(DayCounter.class);
        bootReceiver.dayCounter = dayCounter;
    }

    @Test
    public void shouldSetRepeatingAlarmOnBoot() {
        bootReceiver.onReceive(context, new Intent(Intent.ACTION_BOOT_COMPLETED));

        verify(dayCounter).setAlarmIfNotExists();
    }
}
