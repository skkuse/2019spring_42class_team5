package com.lte.lte;

import android.Manifest;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequest;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import me.gujun.android.taggroup.TagGroup;

//Uploading and modifying Pictures

public class AddModifyPicture extends AppCompatActivity implements View.OnClickListener {
    private static final String CLOUD_VISION_API_KEY = BuildConfig.API_KEY;
    public static final String FILE_NAME = "temp.jpg";
    private static final String ANDROID_CERT_HEADER = "X-Android-Cert";
    private static final String ANDROID_PACKAGE_HEADER = "X-Android-Package";
    private static final int MAX_LABEL_RESULTS = 10;

    private static final String TAG = AddModifyPicture.class.getSimpleName();
    private static final int GALLERY_PERMISSIONS_REQUEST = 0;
    private static final int GALLERY_IMAGE_REQUEST = 1;
    private static final int MAX_DIMENSION = 1200;

    private static final int NUM_LABELS = 5;

    private static int numDone = 0;
    private static int numImages = 0;

    private TextView mImageDetails;
    private ImageView mZoomImage;

    private RelativeLayout rlZoom;
    private RelativeLayout rlSelectedList;

    GridView grSelected;
    private static SelectedImgAdapter adapter;

    private static TagGroup tgHashtags;

    private static Button btnDone;
    private static Button btnDelete;

    private ArrayList<Bitmap> originalBitmaps;

    private static String labels;
    private static String[] topLabels;

    private static Boolean isModifying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_modify_picture);


        grSelected = (GridView) findViewById(R.id.gv_selected_img);

        startGalleryChooser();

        mImageDetails = findViewById(R.id.image_details);
        mZoomImage = findViewById(R.id.iv_zoom_picture);

        btnDone = findViewById(R.id.btn_done_add_modify);
        btnDelete = findViewById(R.id.btn_delete_picture);

        rlZoom = findViewById(R.id.rl_zoom_picture);
        rlSelectedList = findViewById(R.id.rl_selected_list);

        tgHashtags = (TagGroup) findViewById(R.id.tg_hashtags);

        labels = "";
        numDone = 0;
        topLabels = new String[NUM_LABELS];

        grSelected.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            if (!isModifying) {
                mZoomImage.setImageBitmap(originalBitmaps.get(position));

                rlZoom.setVisibility(View.VISIBLE);
                rlSelectedList.setVisibility(View.GONE);
            }

        });
        grSelected.setOnItemLongClickListener((parent, view, position, id) -> {
            isModifying = true;
            btnDelete.setVisibility(View.VISIBLE);
            btnDelete.setClickable(true);
            grSelected.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);
            grSelected.setItemChecked(position, !grSelected.isItemChecked(position));
            adapter.setCbVisibility(true);
            adapter.notifyDataSetChanged();
            return true;
        });

        rlZoom.setOnClickListener(this);
        btnDone.setOnClickListener(this);
        btnDelete.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_zoom_picture:
                rlZoom.setVisibility(View.GONE);
                rlSelectedList.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_delete_picture:
                btnDelete.setVisibility(View.INVISIBLE);
                isModifying = false;
                adapter.setCbVisibility(false);
                for (int i = adapter.getCount() - 1; 0 <= i; i--) {
                    if (grSelected.isItemChecked(i)) {
                        originalBitmaps.remove(i);
                        adapter.remove(i);
                        adapter.notifyDataSetChanged();
                    }
                }
                grSelected.setChoiceMode(GridView.CHOICE_MODE_NONE);
                adapter.notifyDataSetChanged();
                break;

            case R.id.btn_done_add_modify:
                break;
        }
    }


    public void startGalleryChooser() {
        if (PermissionUtils.requestPermission(this, GALLERY_PERMISSIONS_REQUEST, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(Intent.createChooser(intent, "Select a photo"),
                    GALLERY_IMAGE_REQUEST);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            ClipData clipData = data.getClipData();
            assert clipData != null;
            numImages = clipData.getItemCount();
            mImageDetails.setText(String.format("0/%s Done", String.valueOf(numImages)));
            Uri[] uri = new Uri[numImages];
            ArrayList<Bitmap> thumbBitmaps = new ArrayList<>();
            originalBitmaps = new ArrayList<>();
            for (int i = 0; i < numImages; i++)
                uri[i] = clipData.getItemAt(i).getUri();

            for (int i = 0; i < numImages; i++) {
                try {
                    originalBitmaps.add(MediaStore.Images.Media.getBitmap(getContentResolver(), uri[i]));
                    // scale the image to save on bandwidth
                    thumbBitmaps.add(
                            scaleBitmapDown(
                                    originalBitmaps.get(i)
                            ));


                } catch (IOException e) {
                    Log.d(TAG, "Image picking failed because " + e.getMessage());
                    Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show();
                }
            }

            adapter = new SelectedImgAdapter(getApplicationContext(), thumbBitmaps);
            grSelected.setAdapter(adapter);

            for (int i = 0; i < numImages; i++) {
                callCloudVision(thumbBitmaps.get(i));
            }

        }

    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == GALLERY_PERMISSIONS_REQUEST) {
            if (PermissionUtils.permissionGranted(requestCode, GALLERY_PERMISSIONS_REQUEST, grantResults)) {
                startGalleryChooser();
            }
        }
    }


    private Vision.Images.Annotate prepareAnnotationRequest(Bitmap bitmap) throws IOException {
        HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

        VisionRequestInitializer requestInitializer =
                new VisionRequestInitializer(CLOUD_VISION_API_KEY) {
                    /**
                     * We override this so we can inject important identifying fields into the HTTP
                     * headers. This enables use of a restricted cloud platform API key.
                     */
                    @Override
                    protected void initializeVisionRequest(VisionRequest<?> visionRequest)
                            throws IOException {
                        super.initializeVisionRequest(visionRequest);

                        String packageName = getPackageName();
                        visionRequest.getRequestHeaders().set(ANDROID_PACKAGE_HEADER, packageName);

                        String sig = PackageManagerUtils.getSignature(getPackageManager(), packageName);

                        visionRequest.getRequestHeaders().set(ANDROID_CERT_HEADER, sig);
                    }
                };

        Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
        builder.setVisionRequestInitializer(requestInitializer);

        Vision vision = builder.build();

        BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                new BatchAnnotateImagesRequest();
        batchAnnotateImagesRequest.setRequests(new ArrayList<AnnotateImageRequest>() {{
            AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();

            // Add the image
            Image base64EncodedImage = new Image();
            // Convert the bitmap to a JPEG
            // Just in case it's a format that Android understands but Cloud Vision
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();

            // Base64 encode the JPEG
            base64EncodedImage.encodeContent(imageBytes);
            annotateImageRequest.setImage(base64EncodedImage);

            // add the features we want
            annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
                Feature labelDetection = new Feature();
                labelDetection.setType("LABEL_DETECTION");
                labelDetection.setMaxResults(MAX_LABEL_RESULTS);
                add(labelDetection);
            }});

            // Add the list of one thing to the request
            add(annotateImageRequest);
        }});

        Vision.Images.Annotate annotateRequest =
                vision.images().annotate(batchAnnotateImagesRequest);
        // Due to a bug: requests to Vision API containing large images fail when GZipped.
        annotateRequest.setDisableGZipContent(true);
        Log.d(TAG, "created Cloud Vision request object, sending request");

        return annotateRequest;
    }


    private static class LableDetectionTask extends AsyncTask<Object, Void, String> {
        private final WeakReference<AddModifyPicture> mActivityWeakReference;
        private Vision.Images.Annotate mRequest;

        LableDetectionTask(AddModifyPicture activity, Vision.Images.Annotate annotate) {
            mActivityWeakReference = new WeakReference<>(activity);
            mRequest = annotate;
        }

        @Override
        protected String doInBackground(Object... params) {
            try {
                Log.d(TAG, "created Cloud Vision request object, sending request");
                BatchAnnotateImagesResponse response = mRequest.execute();
                return convertResponseToString(response);

            } catch (GoogleJsonResponseException e) {
                Log.d(TAG, "failed to make API request because " + e.getContent());
            } catch (IOException e) {
                Log.d(TAG, "failed to make API request because of other IOException " +
                        e.getMessage());
            }
            return "Cloud Vision API request failed. Check logs for details.";
        }

        protected void onPostExecute(String result) {
            AddModifyPicture activity = mActivityWeakReference.get();
            if (activity != null && !activity.isFinishing()) {
                TextView imageDetail = activity.findViewById(R.id.image_details);
//                imageDetail.setText(result);
                String text = imageDetail.getText().toString();
//                int numDone = Integer.parseInt(text.substring(0, text.indexOf('/'))) + 1;
                imageDetail.setText(text.replace(text.substring(0, text.indexOf('/')), String.valueOf(++numDone)));
                labels = labels.concat(result);
                if (numImages == numDone)
                    resultCalculate(result);
            }
        }
    }

    private void callCloudVision(final Bitmap bitmap) {
        // Switch text to loading

        // Do the real work in an async task, because we need to use the network anyway
        try {
            AsyncTask<Object, Void, String> labelDetectionTask = new LableDetectionTask(this, prepareAnnotationRequest(bitmap));
            labelDetectionTask.execute();
        } catch (IOException e) {
            Log.d(TAG, "failed to make API request because of other IOException " +
                    e.getMessage());
        }
    }

    private Bitmap scaleBitmapDown(Bitmap bitmap) {

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth;
        int resizedHeight;

        if (originalHeight > originalWidth) {
            resizedHeight = MAX_DIMENSION;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = MAX_DIMENSION;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else {
            resizedHeight = MAX_DIMENSION;
            resizedWidth = MAX_DIMENSION;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }

    private static String convertResponseToString(BatchAnnotateImagesResponse response) {
        StringBuilder message = new StringBuilder("I found these things:\n\n");

        List<EntityAnnotation> labels = response.getResponses().get(0).getLabelAnnotations();
        if (labels != null) {
            for (EntityAnnotation label : labels) {
                message.append(String.format(Locale.US, "%.3f: %s", label.getScore(), label.getDescription()));
                message.append("\n");
            }
        } else {
            message.append("nothing");
        }

        return message.toString();
    }

    public static void resultCalculate(String results) {
        String[] splitted = results.split("\n");
        HashMap<String, Float> label_score = new HashMap<>();
        for (String line : splitted) {
            String[] tmp = line.split(": ");
            if (line.length() == 0 || line.contains("I found these things"))
                continue;
            if (!label_score.containsKey(tmp[1])) {
                label_score.put(tmp[1], Float.parseFloat(tmp[0]));
            } else
                label_score.put(tmp[1], label_score.get(tmp[1]) + Float.parseFloat(tmp[0]));
        }

        List<Map.Entry<String, Float>> entryList = new ArrayList<>(label_score.entrySet());
        Collections.sort(entryList, (e1, e2) -> {
            float dif = e2.getValue() - e1.getValue();
            if (0 < dif)
                return 1;
            else if (0 == dif)
                return 0;
            else
                return -1;
        });

        for (int i = 0; i < NUM_LABELS; i++) {
            topLabels[i] = entryList.get(i).getKey();
        }

        tgHashtags.setTags(topLabels);


    }

}

