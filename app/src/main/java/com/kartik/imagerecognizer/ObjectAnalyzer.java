package com.kartik.imagerecognizer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ibm.watson.developer_cloud.android.library.camera.CameraHelper;
import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifyImagesOptions;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ImageClassification;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassification;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassifier;

import java.io.File;

public class ObjectAnalyzer extends AppCompatActivity implements View.OnClickListener{

	//Global Variables
	private VisualRecognition vrService;
	private CameraHelper cameraHelper;
	private ImageView imagePreview;
	private TextView imageAnalysisTxtView;

	/* -------------------------------------- Lifecycle Methods -------------------------------------- */
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_object_analyzer);

		imagePreview = findViewById(R.id.image_preview);
		final Button takePictureBtn = findViewById(R.id.captureImage_btn);
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
			//Show spinner when performing the image analysis.
			final ProgressDialog spinner = new ProgressDialog(this);
			spinner.show();


			//Use AsyncTask to process and analyze the image in background thread.
			AsyncTask.execute(new Runnable() {
				@Override
				public void run() {
					//classify method can handle multiple images at same time.
					VisualClassification result =
							vrService.classify(
									new ClassifyImagesOptions.Builder()
											.images(imageFile)
											.build()
							).execute();
					try {
						//getImages returns a list of imageClassification. However, as we are using only one image,
						//we will get only the first item in the list.
						ImageClassification imageClassification = result.getImages().get(0);

						VisualClassifier visualClassifier = imageClassification.getClassifiers().get(0);

						final StringBuffer outputText = new StringBuffer();
						for(VisualClassifier.VisualClass object: visualClassifier.getClasses()) {
							//get an object only if the score is above 80%.
							if(object.getScore() > 0.8f)
								outputText.append(object.getName().toUpperCase())
										.append("\n");
						}
						//show results on UIThread
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								spinner.dismiss();
								if (outputText.toString().isEmpty()) {
									imageAnalysisTxtView.setText(R.string.error_message);
								} else {
									imageAnalysisTxtView.setText(outputText);
								}
							}
						});
					} catch (Exception e){
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								spinner.dismiss();
								Toast.makeText(getApplicationContext(), "And Error Occurred", Toast.LENGTH_SHORT).show();
							}
						});
					}
				}
			});


		}

	}
}
