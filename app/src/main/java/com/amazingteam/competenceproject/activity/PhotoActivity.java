package com.amazingteam.competenceproject.activity;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.amazingteam.competenceproject.R;
import com.amazingteam.competenceproject.model.Template;
import com.amazingteam.competenceproject.ui.TemplateAdapter;
import com.amazingteam.competenceproject.util.DatabaseHelper;
import com.amazingteam.competenceproject.util.PhotoCrop;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.amazingteam.competenceproject.util.Constants.CLOTH_IMAGE_PATH;
import static com.amazingteam.competenceproject.util.Constants.CLOTH_IMAGE_PATH_TO_DELETE;
import static com.amazingteam.competenceproject.util.Constants.CLOTH_TYPE;
import static com.amazingteam.competenceproject.util.Constants.CLOTH_TYPE_PATTERN;
import static com.amazingteam.competenceproject.util.Constants.IMAGE_FOLDER_NAME;

public class PhotoActivity extends AppCompatActivity {

    private static final String TAG = PhotoActivity.class.getSimpleName();

    private static final int STATE_PREVIEW = 0;
    private static final int STATE_WAIT_LOCK = 1;
    private int captureState = STATE_PREVIEW;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter templateAdapter;
    private boolean flashOn = false;
    private boolean templateListOn = false;
    private List<Template> templates;
    private File imageFolder;
    private String imageFileName;
    private String clothTypeFromTemplate = "tshirt";
    private TextureView textureView;
    private TextureView.SurfaceTextureListener surfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
            setupCamera(width, height);
            connectCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

        }
    };
    private CameraDevice mCameraDevice;
    CameraDevice.StateCallback cameraDeviceStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            mCameraDevice = cameraDevice;
            startPreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            cameraDevice.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int i) {
            cameraDevice.close();
            mCameraDevice = null;
        }
    };
    private String cameraID;
    private Size previewSize;
    private ImageReader imageReader;
    private final ImageReader.OnImageAvailableListener onImageAvailableListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            backgroundHandler.post(new ImageSaver(reader.acquireLatestImage()));
        }
    };

    public class ImageSaver implements Runnable {
        final Image image;

        ImageSaver(Image image) {
            this.image = image;
        }

        @Override
        public void run() {
            Intent intent = new Intent(PhotoActivity.this, SelectTagsActivity.class);

            ByteBuffer byteBuffer = image.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[byteBuffer.remaining()];
            byteBuffer.get(bytes);
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(imageFileName);
                fileOutputStream.write(bytes);
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            } finally {
                image.close();
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            intent.putExtra(CLOTH_IMAGE_PATH_TO_DELETE, imageFileName);

            PhotoCrop photoCrop = new PhotoCrop(imageFileName, clothTypeFromTemplate);
            Bitmap croppedPhoto = photoCrop.getCroppedImage(getResources(), clothTypeFromTemplate);

            try {
                createImagesFileName();
            } catch (IOException e) {
                e.printStackTrace();
            }
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(imageFileName);
                croppedPhoto.compress(Bitmap.CompressFormat.PNG, 100, out);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            intent.putExtra(CLOTH_IMAGE_PATH, imageFileName);
            intent.putExtra(CLOTH_TYPE, clothTypeFromTemplate);
            startActivity(intent);
        }
    }

    private CaptureRequest.Builder captureRequestBuilder;
    private CameraCaptureSession previewCaptureSession;
    private CameraCaptureSession.CaptureCallback previewCaptureCallback = new CameraCaptureSession.CaptureCallback() {

        private void process(CaptureResult captureResult) {
            switch (captureState) {
                case STATE_PREVIEW:

                    break;
                case STATE_WAIT_LOCK:
                    captureState = STATE_PREVIEW;
                    Integer afState = captureResult.get(CaptureResult.CONTROL_AF_STATE);
                    if (afState == CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED || afState == CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED) {
                        startCaptureRequest();
                    } else {
                        startCaptureRequest();
                    }
                    break;
            }
        }

        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
            process(result);
        }
    };
    private HandlerThread backgroundHandlerThread;
    private Handler backgroundHandler;

    private static class CompareSizeByArea implements Comparator<Size> {

        @Override
        public int compare(Size lhs, Size rhs) {
            return Long.signum((long) (lhs.getWidth() * lhs.getHeight()) -
                    (long) (rhs.getWidth() * rhs.getHeight()));
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        createImageFolder();
        lockOrientation();


        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(this);
        templates = databaseHelper.getAllTemplates();
        databaseHelper.closeDataBase();
        setTemplatesRecycleView();

        recyclerView.addOnItemTouchListener(
                new TemplateAdapter.RecyclerItemClickListener(this, recyclerView, new TemplateAdapter.RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        ImageView templateView = (ImageView) findViewById(R.id.templateView);
                        templateView.setImageResource(templates.get(position).getPhotoTemplateID());
                        clothTypeFromTemplate = getClothType(templates.get(position).getName());
                        recyclerView.setVisibility(View.INVISIBLE);
                        recyclerView.setEnabled(false);
                        recyclerView.setClickable(false);

                    }

                    @Override
                    public void onShowPress(View view, int position) {
                        ImageView templateView = (ImageView) findViewById(R.id.templateView);
                        templateView.setImageResource(templates.get(position).getPhotoTemplateID());
                    }
                })
        );

        ImageButton focusButton = (ImageButton) findViewById(R.id.takePhoto);
        focusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lockFocus();
            }
        });

        ImageButton templateButton = (ImageButton) findViewById(R.id.changeClothTemplate);
        templateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView.setClickable(true);
                recyclerView.setEnabled(true);
                templateListOn = true;
            }
        });

        ImageButton flashButton = (ImageButton) findViewById(R.id.toggleLight);
        final CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            if (cameraManager != null) {
                cameraID = cameraManager.getCameraIdList()[0];
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        flashLightAlert();
        flashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flashOn) {
                    turnOffFlashLight();
                    flashOn = false;
                } else {
                    turnOnFlashLight();
                    flashOn = true;
                }
            }
        });
        textureView = (TextureView) findViewById(R.id.textureView);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (flashOn) {
            turnOnFlashLight();
        }
        startBackgroundThread();
        if (textureView.isAvailable()) {
            setupCamera(textureView.getWidth(), textureView.getHeight());
            connectCamera();
        } else {
            textureView.setSurfaceTextureListener(surfaceTextureListener);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (flashOn) {
            turnOffFlashLight();
        }
    }


    @Override
    protected void onPause() {
        closeCamera();
        stopBackgroundThread();
        super.onPause();
        if (flashOn) {
            turnOffFlashLight();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        View decorView = getWindow().getDecorView();

        if (hasFocus) {
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                    View.SYSTEM_UI_FLAG_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            );
        }
    }

    @Override
    public void onBackPressed() {
        if (templateListOn) {
            recyclerView.setVisibility(View.INVISIBLE);
            recyclerView.setEnabled(false);
            recyclerView.setClickable(false);
            templateListOn = false;
        } else {
            if (!templateListOn) {
                Intent intent = new Intent(PhotoActivity.this, WardrobeActivity.class);
                startActivity(intent);
            } else {
                super.onBackPressed();

            }
        }


    }

    private void closeCamera() {
        if (mCameraDevice != null) {
            mCameraDevice.close();
            mCameraDevice = null;
        }
    }

    private void setupCamera(int width, int height) {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            if (cameraManager != null) {
                for (String tempCameraID : cameraManager.getCameraIdList()) {
                    CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(tempCameraID);
                    if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT) {
                        continue;
                    }
                    StreamConfigurationMap map = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                    previewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class), width, height);
                    Size imageSize = chooseOptimalSize(map.getOutputSizes(ImageFormat.JPEG), width, height);
                    imageReader = ImageReader.newInstance(imageSize.getWidth(), imageSize.getHeight(), ImageFormat.JPEG, 1);
                    imageReader.setOnImageAvailableListener(onImageAvailableListener, backgroundHandler);
                    cameraID = tempCameraID;
                    return;

                }
            }
        } catch (CameraAccessException e) {
            Log.e(TAG, e.getMessage());
        }
    }


    private void connectCamera() {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        if (cameraManager != null) {
            try {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                cameraManager.openCamera(cameraID, cameraDeviceStateCallback, backgroundHandler);
            } catch (CameraAccessException e) {
                Log.e(TAG, e.getMessage());
            }
        } else {
            closeCamera();
        }
    }

    private void startPreview() {
        SurfaceTexture surfaceTexture = textureView.getSurfaceTexture();
        surfaceTexture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
        Surface previewSurface = new Surface(surfaceTexture);
        try {
            captureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(previewSurface);

            mCameraDevice.createCaptureSession(Arrays.asList(previewSurface, imageReader.getSurface()), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    previewCaptureSession = cameraCaptureSession;
                    try {
                        previewCaptureSession.setRepeatingRequest(captureRequestBuilder.build(), null, backgroundHandler);

                    } catch (CameraAccessException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    Toast.makeText(getApplicationContext(), "Unable to setup camera preview", Toast.LENGTH_LONG).show();
                }
            }, null);
        } catch (CameraAccessException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void startCaptureRequest() {
        try {
            captureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureRequestBuilder.addTarget(imageReader.getSurface());
            captureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, 90);
            CameraCaptureSession.CaptureCallback captureCallback = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureStarted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, long timestamp, long frameNumber) {
                    super.onCaptureStarted(session, request, timestamp, frameNumber);

                    try {
                        createImagesFileName();
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            };
            previewCaptureSession.capture(captureRequestBuilder.build(), captureCallback, null);
        } catch (CameraAccessException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void startBackgroundThread() {
        backgroundHandlerThread = new HandlerThread("PhotoActivity");
        backgroundHandlerThread.start();
        backgroundHandler = new Handler(backgroundHandlerThread.getLooper());
    }

    private void stopBackgroundThread() {
        backgroundHandlerThread.quitSafely();
        try {
            backgroundHandlerThread.join();
            backgroundHandlerThread = null;
            backgroundHandler = null;
        } catch (InterruptedException e) {
            Log.e(TAG, e.getMessage());
        }
    }


    private static Size chooseOptimalSize(Size[] choices, int width, int height) {
        List<Size> bigEnough = new ArrayList<>();
        for (Size option : choices) {
            if (option.getHeight() == option.getWidth() * height / width &&
                    option.getWidth() >= width && option.getHeight() >= height) {
                bigEnough.add(option);
            }
        }
        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizeByArea());
        } else {
            return choices[0];
        }
    }

    private void lockFocus() {
        captureState = STATE_WAIT_LOCK;
        captureRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CaptureRequest.CONTROL_AF_TRIGGER_START);
        try {
            previewCaptureSession.capture(captureRequestBuilder.build(), previewCaptureCallback, backgroundHandler);
        } catch (CameraAccessException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void createImagesFileName() throws IOException {
        String timestamp = new SimpleDateFormat("ddMMyyyy_HH:mm:ss").format(new Date());
        String prepend = "Image_" + timestamp + "_";
        File imageFile = File.createTempFile(prepend, ".png", imageFolder);
        imageFileName = imageFile.getAbsolutePath();
    }

    private void createImageFolder() {
        File imageFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        imageFolder = new File(imageFile, IMAGE_FOLDER_NAME);
        if (!imageFolder.exists()) {
            imageFolder.mkdirs();
        }
    }

    private void lockOrientation() {
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        }
    }

    private void setTemplatesRecycleView() {
        recyclerView = (RecyclerView) findViewById(R.id.templateRecyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setVisibility(View.INVISIBLE);
        recyclerView.setClickable(false);
        recyclerView.setEnabled(false);
        templateAdapter = new TemplateAdapter(templates, this);
        recyclerView.setAdapter(templateAdapter);
    }

    private String getClothType(String clothTypeFromTemplate) {
        String clothType;
        Pattern pattern = Pattern.compile(CLOTH_TYPE_PATTERN);
        Matcher matcher = pattern.matcher(clothTypeFromTemplate);
        if (matcher.find()) {
            clothType = matcher.group(1);
        } else clothType = clothTypeFromTemplate;
        return clothType;
    }

    public void turnOnFlashLight() {
        try {
            captureRequestBuilder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_TORCH);
            previewCaptureSession.setRepeatingRequest(captureRequestBuilder.build(), null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void turnOffFlashLight() {
        try {
            captureRequestBuilder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_OFF);
            previewCaptureSession.setRepeatingRequest(captureRequestBuilder.build(), null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void flashLightAlert() {
        Boolean isFlashAvailable = getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        if (!isFlashAvailable) {
            AlertDialog alert = new AlertDialog.Builder(PhotoActivity.this)
                    .create();
            alert.setTitle("Error !!");
            alert.setMessage("Your device doesn't support flash light!");
            alert.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            alert.show();
        }
    }


}




