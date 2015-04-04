package com.example.secrets;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.secrets.R;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.firebase.simplelogin.SimpleLogin;

import java.util.ArrayList;

public class ListNearbySecretsActivity extends ActionBarActivity {

    private Double latitude;
    private Double longitude;
    private ArrayList<String> secrets;
    private Firebase ref;
    private SecretsAdapter secretsArrayAdapter;
    private GeoQuery geoQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_nearby_secrets);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location lastLoc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        longitude = lastLoc.getLongitude();
        latitude = lastLoc.getLatitude();

        MyLocationListener locationListener = new MyLocationListener();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        ref = new Firebase("https://intense-fire-3164.firebaseio.com");
        Firebase locationsRef = ref.child("locations");
        GeoFire geoFire = new GeoFire(locationsRef);
        geoQuery = geoFire.queryAtLocation(latitude, longitude, 0.01);
        geoQuery.addGeoQueryEventListener(new myGeoQueryEventListener());

        secrets = new ArrayList<String>();
        ListView secretsListView = (ListView) findViewById(R.id.secretsListView);
        secretsArrayAdapter = new SecretsAdapter(this);
        secretsListView.setAdapter(secretsArrayAdapter);
    }

    private class MyLocationListener implements LocationListener {
        public void onLocationChanged(Location location) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            geoQuery.setCenter(latitude, longitude);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {}

        public void onProviderEnabled(String provider) {}

        public void onProviderDisabled(String provider) {}
    }

    private class myGeoQueryEventListener implements GeoQueryEventListener {
        @Override
        public void onKeyEntered(String key, double lat, double lng) {
            Firebase secretRef = ref.child("secrets").child(key);
            secretRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    byte[] imageDecodedString = Base64.decode(dataSnapshot.child("image").getValue().toString(), Base64.DEFAULT);
                    Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageDecodedString, 0, imageDecodedString.length);
                    BitmapDrawable imageBitmapDrawable = new BitmapDrawable(getApplicationContext().getResources(), imageBitmap);
                    secretsArrayAdapter.addSecret(dataSnapshot.child("text").getValue().toString(), imageBitmapDrawable);
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {}
            });
        }

        @Override
        public void onKeyExited(String key) {
        }

        @Override
        public void onKeyMoved(String key, double lat, double lng) {
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_nearby_secrets, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_new_secret:
                Intent intent = new Intent(this, NewSecretActivity.class);
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                startActivity(intent);
                return true;
            case R.id.menu_logout:
                new SimpleLogin(ref, getApplicationContext()).logout();
                startActivity(new Intent(this, LoginActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
