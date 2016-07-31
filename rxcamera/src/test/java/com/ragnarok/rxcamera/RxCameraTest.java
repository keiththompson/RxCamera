package com.ragnarok.rxcamera;

import android.content.Context;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class RxCameraTest {

    @Mock Context context;
    @Mock WindowManager windowManager;
    @Mock Display display;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(context.getSystemService(Context.WINDOW_SERVICE)).thenReturn(windowManager);
        when(windowManager.getDefaultDisplay()).thenReturn(display);
    }

    @Test public void getRotationMatrix90() {
        when(display.getRotation()).thenReturn(Surface.ROTATION_90);
        assertEquals(0, RxCamera.getMatrixRotation(context));
    }

    @Test public void getRotationMatrix270() {
        when(display.getRotation()).thenReturn(Surface.ROTATION_270);
        assertEquals(180, RxCamera.getMatrixRotation(context));
    }

    @Test public void getRotationMatrix180() {
        when(display.getRotation()).thenReturn(Surface.ROTATION_180);
        assertEquals(90, RxCamera.getMatrixRotation(context));
    }

    @Test public void getRotationMatrix0() {
        when(display.getRotation()).thenReturn(Surface.ROTATION_0);
        assertEquals(270, RxCamera.getMatrixRotation(context));
    }

    @Test public void getRotationMatrixDefault() {
        when(display.getRotation()).thenReturn(-1);
        assertEquals(270, RxCamera.getMatrixRotation(context));
    }
}
