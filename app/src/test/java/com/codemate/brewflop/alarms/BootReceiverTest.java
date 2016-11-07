package com.codemate.brewflop.alarms;

import android.content.Context;
import android.content.Intent;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by iiro on 22.9.2016.
 */
@RunWith(RobolectricTestRunner.class)
public class BootReceiverTest {
    private Context context;
    private BootReceiver bootReceiver;
    private DayUpdateScheduler dayUpdateScheduler;

    @Before
    public void setUp() {
        context = RuntimeEnvironment.application.getApplicationContext();
        bootReceiver = new BootReceiver();
        dayUpdateScheduler = mock(DayUpdateScheduler.class);
        bootReceiver.dayUpdateScheduler = dayUpdateScheduler;
    }

    @Test
    public void shouldUseRealDayUpdateSchedulerIfFieldNotInjected() {
        bootReceiver.dayUpdateScheduler = null;
        bootReceiver.onReceive(context, new Intent());

        assertNotNull(bootReceiver.dayUpdateScheduler);
    }

    @Test
    public void shouldSetRepeatingAlarmOnBoot() {
        bootReceiver.onReceive(context, new Intent(Intent.ACTION_BOOT_COMPLETED));

        verify(dayUpdateScheduler).setAlarm();
    }
}
