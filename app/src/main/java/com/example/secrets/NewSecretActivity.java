package com.example.secrets;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.geofire.GeoFire;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class NewSecretActivity extends ActionBarActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1001;

    private Double latitude;
    private Double longitude;
    private Firebase myRef;
    private Firebase secretsRef;
    private Firebase locationsRef;
    private EditText secretEditText;
    private String encodedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_secret);

        Intent intent = getIntent();
        latitude = intent.getDoubleExtra("latitude", 0.0);
        longitude = intent.getDoubleExtra("longitude", 0.0);

        myRef = new Firebase("https://intense-fire-3164.firebaseio.com");
        secretsRef = myRef.child("secrets");
        locationsRef = myRef.child("locations");
        secretEditText = (EditText) findViewById(R.id.new_secret_edit_text);

        findViewById(R.id.take_photo_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });

        findViewById(R.id.submit_new_secret_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (secretEditText.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Please enter a secret", Toast.LENGTH_SHORT).show();
                } else if (encodedImage == null) {
                    Toast.makeText(getApplicationContext(), "Please enter a photo", Toast.LENGTH_SHORT).show();
                } else {
                    Map secret = new HashMap();
                    secret.put("text", secretEditText.getText().toString());
                    secret.put("image", encodedImage);
                    Firebase secretRef = secretsRef.push();
                    secretRef.setValue(secret);

                    GeoFire geoFire = new GeoFire(locationsRef);
                    geoFire.setLocation(secretRef.getName(), latitude, longitude);

                    startActivity(new Intent(getApplicationContext(), ListNearbySecretsActivity.class));
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream .toByteArray();
            encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
        }
    }
}
