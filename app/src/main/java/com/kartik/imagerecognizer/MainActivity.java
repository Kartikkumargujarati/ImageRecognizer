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
import android.widget.Spinner;
import android.widget.TextView;

import com.ibm.watson.developer_cloud.android.library.camera.CameraHelper;
import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifyImagesOptions;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ImageClassification;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassification;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassifier;

import java.io.File;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


	/* -------------------------------------- Lifecycle Methods -------------------------------------- */
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final Button objectAnalyzerBtn = findViewById(R.id.objectAnalyze_btn);
		objectAnalyzerBtn.setOnClickListener(this);
		final Button faceAnalyzerBtn = findViewById(R.id.faceAnalyze_btn);
		faceAnalyzerBtn.setOnClickListener(this);
	}
/* ------------------------------------ End Lifecycle Methods ------------------------------------ */

/* ------------------------------------ Helper Methods ------------------------------------ */

	@Override
	public void onClick (final View view) {
		//On the captureImage_btn click
		switch (view.getId()) {
			case R.id.objectAnalyze_btn:
				startActivity(new Intent(this, ObjectAnalyzer.class));
				break;
			case R.id.faceAnalyze_btn:
				startActivity(new Intent(this, PersonAnalyzer.class));
				break;
		}
	}
}
