package com.codemate.brewflop;

import android.content.Context;
import android.content.Intent;

import com.codemate.brewflop.alarms.BootReceiver;
import com.codemate.brewflop.alarms.DayUpdater;

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
    private DayUpdater dayUpdater;

    @Before
    public void setUp() {
        context = RuntimeEnvironment.application.getApplicationContext();
        bootReceiver = new BootReceiver();
        dayUpdater = mock(DayUpdater.class);
        bootReceiver.dayUpdater = dayUpdater;
    }

    @Test
    public void shouldSetRepeatingAlarmOnBoot() {
        bootReceiver.onReceive(context, new Intent(Intent.ACTION_BOOT_COMPLETED));

        verify(dayUpdater).setAlarm();
    }
}
