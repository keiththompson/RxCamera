package com.ragnarok.rxcamera;

import android.graphics.SurfaceTexture;
import android.test.suitebuilder.annotation.SmallTest;
import android.view.SurfaceHolder;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

@SmallTest
public class SurfaceCallbackTest {

    @Mock SurfaceCallback.SurfaceListener surfaceListener;
    @Mock SurfaceHolder surfaceHolder;
    @Mock SurfaceTexture surfaceTexture;

    private SurfaceCallback surfaceCallback;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        surfaceCallback = new SurfaceCallback();
        surfaceCallback.setSurfaceListener(surfaceListener);
    }

    @Test public void surfaceCreated() {
        surfaceCallback.surfaceCreated(surfaceHolder);
        verifyZeroInteractions(surfaceListener);
    }

    @Test public void surfaceChanged() {
        surfaceCallback.surfaceChanged(surfaceHolder, 0, 0, 0);
        verify(surfaceListener).onAvailable();
    }

    @Test public void surfaceDestroyed() {
        surfaceCallback.surfaceDestroyed(surfaceHolder);
        verify(surfaceListener).onDestroy();
    }

    @Test public void onSurfaceTextureAvailable() {
        surfaceCallback.onSurfaceTextureAvailable(surfaceTexture, 0, 0);
        verify(surfaceListener).onAvailable();
    }

    @Test public void onSurfaceTextureSizeChanged() {
        surfaceCallback.onSurfaceTextureSizeChanged(surfaceTexture, 0, 0);
        verifyZeroInteractions(surfaceListener);
    }

    @Test public void onSurfaceTextureDestroyed() {
        surfaceCallback.onSurfaceTextureDestroyed(surfaceTexture);
        verify(surfaceListener).onDestroy();
    }

    @Test public void onSurfaceTextureUpdated() {
        surfaceCallback.onSurfaceTextureUpdated(surfaceTexture);
        verifyZeroInteractions(surfaceListener);
    }
}
