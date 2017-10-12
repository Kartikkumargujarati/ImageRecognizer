package com.kartik.imagerecognizer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ibm.watson.developer_cloud.android.library.camera.CameraHelper;
import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifyImagesOptions;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassification;

import java.io.File;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

	//Global Variables
	private VisualRecognition vrService;
	private CameraHelper cameraHelper;
	private ImageView imagePreview;
	private Button takePictureBtn;
	private TextView imageAnalysisTxtView;

/* -------------------------------------- Lifecycle Methods -------------------------------------- */
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		imagePreview = findViewById(R.id.image_preview);
		takePictureBtn = findViewById(R.id.captureImage_btn);
		takePictureBtn.setOnClickListener(this);
		imageAnalysisTxtView = findViewById(R.id.image_analysis_txt);

		//Instantiates a Visual Recognition service with a given API key.
		vrService = new VisualRecognition(
				VisualRecognition.VERSION_DATE_2016_05_20,
				getString(R.string.ibm_watson_api_key)
		);

		// Initialize camera helper
		cameraHelper = new CameraHelper(this);
	}
/* ------------------------------------ End Lifecycle Methods ------------------------------------ */

/* ------------------------------------ Helper Methods ------------------------------------ */

	@Override
	public void onClick (final View view) {
		//On the captureImage_btn click
		if (view.getId() == R.id.captureImage_btn){
			//open the device's default camera app to capture an image.
			cameraHelper.dispatchTakePictureIntent();
		}
	}

/* ------------------------------------ Activity Result Method ------------------------------------ */

	//Receive the image captured previous in the onClick here.
	@Override
	protected void onActivityResult (final int requestCode, final int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == CameraHelper.REQUEST_IMAGE_CAPTURE) {
			//Get the image file using the camera helper.
			final File imageFile = cameraHelper.getFile(resultCode);

			//Get the bitmap and display in the image view.
			final Bitmap image = cameraHelper.getBitmap(resultCode);
			imagePreview.setImageBitmap(image);

			//Use AsyncTask to process and analyze the image in background thread.
			AsyncTask.execute(new Runnable() {
				@Override
				public void run() {

					VisualClassification response =
							vrService.classify(
									new ClassifyImagesOptions.Builder()
											.images(imageFile)
											.build()
							).execute();

				}
			});
		}

	}
}
