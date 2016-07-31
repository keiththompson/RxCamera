package com.ragnarok.rxcamera;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Point;
import android.hardware.Camera;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.WindowManager;

import com.ragnarok.rxcamera.action.RxCameraActionBuilder;
import com.ragnarok.rxcamera.config.RxCameraConfig;
import com.ragnarok.rxcamera.request.RxCameraRequestBuilder;

import rx.Observable;
import rx.functions.Func0;
import rx.functions.Func1;

public class RxCamera  {

    private final RxCameraInternal cameraInternal;

    private final Matrix rotateMatrix;

    private RxCamera(Context context, RxCameraConfig config) {
        this.cameraInternal = new RxCameraInternal(context, config);
        this.rotateMatrix = new Matrix();
        rotateMatrix.postRotate(getMatrixRotation(context), 0.5f, 0.5f);
    }

    /**
     * Open the camera
     * @param context
     * @param config
     * @return An Observable<{@link RxCamera}>
     */
    public static Observable<RxCamera> open(final Context context, final RxCameraConfig config) {

        return Observable.defer(new Func0<Observable<RxCamera>>() {
            @Override public Observable<RxCamera> call() {
                RxCamera rxCamera = new RxCamera(context, config);
                if (rxCamera.cameraInternal.openCameraInternal()) {
                    return Observable.just(rxCamera);
                }
                return Observable.error(rxCamera.cameraInternal.openCameraException());
            }
        });
    }

    /**
     * Open the camera and start preview, bind a given {@link SurfaceView}
     * @param context
     * @param config
     * @param surfaceView
     * @return An Observable<{@link RxCamera}>
     */
    public static Observable<RxCamera> openAndStartPreview(Context context, RxCameraConfig config,
                                                           final SurfaceView surfaceView) {
        return open(context, config)
            .flatMap(new Func1<RxCamera, Observable<RxCamera>>() {
                @Override public Observable<RxCamera> call(RxCamera rxCamera) {
                    return rxCamera.bindSurface(surfaceView);
                }
            }).flatMap(new Func1<RxCamera, Observable<RxCamera>>() {
                @Override public Observable<RxCamera> call(RxCamera rxCamera) {
                    return rxCamera.startPreview();
                }
            });
    }

    /**
     * Open the camera and start preview, bind a given {@link TextureView}
     * @param context
     * @param config
     * @param textureView
     * @return An Observable<{@link RxCamera}>
     */
    public static Observable<RxCamera> openAndStartPreview(final Context context, final RxCameraConfig config,
                                                           final TextureView textureView) {
        return open(context, config)
            .flatMap(new Func1<RxCamera, Observable<RxCamera>>() {
                @Override public Observable<RxCamera> call(RxCamera rxCamera) {
                    return rxCamera.bindTexture(textureView);
                }
            }).flatMap(new Func1<RxCamera, Observable<RxCamera>>() {
                @Override public Observable<RxCamera> call(RxCamera rxCamera) {
                    return rxCamera.startPreview();
                }
            });
    }

    static int getMatrixRotation(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        final int displayRotation = windowManager.getDefaultDisplay().getRotation();

        switch (displayRotation) {
            case Surface.ROTATION_90: return 0;
            case Surface.ROTATION_270: return 180;
            case Surface.ROTATION_180: return 90;

            default:
            case Surface.ROTATION_0: return  270;
        }
    }

    public Matrix getRotateMatrix() {
        return rotateMatrix;
    }

    /**
     * Bind a {@link SurfaceView} as the camera preview surface
     * @param surfaceView
     * @return An Observable<{@link RxCamera}>
     */
    public Observable<RxCamera> bindSurface(final SurfaceView surfaceView) {

        return Observable.defer(new Func0<Observable<RxCamera>>() {
            @Override public Observable<RxCamera> call() {
                if (cameraInternal.bindSurfaceInternal(surfaceView)) {
                    return Observable.just(RxCamera.this);
                }
                return Observable.error(cameraInternal.bindSurfaceFailedException());
            }
        });
    }

    /**
     * Bind a {@link TextureView} as the camera preview surface
     * @param textureView
     * @return An Observable<{@link RxCamera}>
     */
    public Observable<RxCamera> bindTexture(final TextureView textureView) {
        return Observable.defer(new Func0<Observable<RxCamera>>() {
            @Override public Observable<RxCamera> call() {
                if (cameraInternal.bindTextureInternal(textureView)) {
                    return Observable.just(RxCamera.this);
                }
                return Observable.error(cameraInternal.bindSurfaceFailedException());
            }
        });
    }

    /**
     * Start preview, must be called after bindTexture or bindSurface
     * @return An Observable<{@link RxCamera}>
     */
    public Observable<RxCamera> startPreview() {

        return Observable.defer(new Func0<Observable<RxCamera>>() {
            @Override public Observable<RxCamera> call() {
                if (cameraInternal.startPreviewInternal()) {
                    return Observable.just(RxCamera.this);
                }
                return Observable.error(cameraInternal.startPreviewFailedException());
            }
        });
    }

    /**
     * Close the camera, return an Observable as the result
     * @return an Observable<Boolean>
     */
    public Observable<Boolean> closeCameraWithResult() {
        return Observable.defer(new Func0<Observable<Boolean>>() {
            @Override public Observable<Boolean> call() {
                return Observable.just(cameraInternal.closeCameraInternal());
            }
        });
    }

    /**
     * Switch the camera, return an Observable indicated if switch success
     * @return an Observable<Boolean>
     */
    public Observable<Boolean> switchCamera() {
        return Observable.defer(new Func0<Observable<Boolean>>() {
            @Override public Observable<Boolean> call() {
                return Observable.just(cameraInternal.switchCameraInternal());
            }
        });
    }

    /**
     * @return a {@link RxCameraRequestBuilder} which you can request the camera preview frame data
     */
    public RxCameraRequestBuilder request() {
        return new RxCameraRequestBuilder(this);
    }

    /**
     * @return a {@link RxCameraActionBuilder} which you can change the camera parameter in the fly
     */
    public RxCameraActionBuilder action() {
        return new RxCameraActionBuilder(this);
    }

    /**
     * Directly close the camera
     * @return true if close success
     */
    public boolean closeCamera() {
        return cameraInternal.closeCameraInternal();
    }

    public boolean isOpenCamera() {
        return cameraInternal.isOpenCamera();
    }

    public boolean isBindSurface() {
        return cameraInternal.isBindSurface();
    }

    /**
     * The config of this camera
     * @return The {@link RxCameraConfig} for this camera
     */
    public RxCameraConfig getConfig() {
        return cameraInternal.getConfig();
    }

    /**
     * @return The native {@link android.hardware.Camera} object
     */
    @SuppressWarnings("deprecation")
    public Camera getNativeCamera() {
        return cameraInternal.getNativeCamera();
    }

    /**
     * The final preview size, mostly this is not the same as the one set in {@link RxCameraConfig}
     * @return
     */
    public Point getFinalPreviewSize() {
        return cameraInternal.getFinalPreviewSize();
    }

    public void installPreviewCallback(OnRxCameraPreviewFrameCallback previewCallback) {
        this.cameraInternal.installPreviewCallback(previewCallback);
    }

    public void uninstallPreviewCallback(OnRxCameraPreviewFrameCallback previewCallback) {
        this.cameraInternal.uninstallPreviewCallback(previewCallback);
    }

    public void installOneShotPreviewCallback(OnRxCameraPreviewFrameCallback previewFrameCallback) {
        this.cameraInternal.installOneShotPreviewCallback(previewFrameCallback);
    }

    public void uninstallOneShotPreviewCallback(OnRxCameraPreviewFrameCallback previewFrameCallback) {
        this.cameraInternal.uninstallOneShotPreviewCallback(previewFrameCallback);
    }
}
