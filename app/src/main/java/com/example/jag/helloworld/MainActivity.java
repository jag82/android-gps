package com.example.jag.helloworld;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Context ctx;

    TextView textLatLng;
    TextView textDistance;
    RelativeLayout mapView;
    ImageView currentPos;
    ImageView nearestSpotView;

    GPSTracker gps;
    Location lastLocation;
    TextToSpeech tts;

    double xMin = Double.MAX_VALUE;
    double xMax = Double.MIN_VALUE;
    double yMin = Double.MAX_VALUE;
    double yMax = Double.MIN_VALUE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.ctx = this;

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
            }
        });

        this.textLatLng = findViewById(R.id.latlng);
        this.textDistance = findViewById(R.id.distance);
        this.mapView = findViewById(R.id.map);

        this.gps = new GPSTracker(this, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
//                Toast.makeText(ctx, "onLocationChanged: "  + location, Toast.LENGTH_LONG).show();
                updateLocation(location);
            }


            @Override
            public void onProviderDisabled(String provider) {
//                Toast.makeText(ctx, "onProviderDisabled: "  + provider, Toast.LENGTH_SHORT).show();
            }


            @Override
            public void onProviderEnabled(String provider) {
//                Toast.makeText(ctx, "onProviderEnabled: "  + provider, Toast.LENGTH_SHORT).show();
            }


            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
//                Toast.makeText(ctx, "onStatusChanged: "  + status, Toast.LENGTH_SHORT).show();
            }

        });

        drawSpots();

        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
            }
        });
        tts.setLanguage(Locale.CANADA);

        gps.startListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_spots) {
            Intent intent = new Intent(this, SpotterActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_reset) {
            SharedPreferences prefs = getSharedPreferences("spots", Context.MODE_PRIVATE);
            prefs.edit().clear().commit();
            return true;
        }



        return super.onOptionsItemSelected(item);
    }

    private void getLocation() {
        if(gps.canGetLocation()) {
            updateLocation(gps.getLocation());
        } else {
            gps.showSettingsAlert();
        }
    }

    private void updateLocation(Location location) {
        if(location == null) {
            return;
        }

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
//        textLatLng.setText("lat: " + latitude + "\nlng: " + longitude);

        if(lastLocation != null) {
            double distance = Utils.calcDistance(latitude, longitude, lastLocation.getLatitude(), lastLocation.getLongitude());
            textDistance.setText(distance + " m");
        }
        lastLocation = location;

        drawNearestSpot(location);

        if(currentPos == null) {
            currentPos = new ImageView(MainActivity.this);
            currentPos.setImageResource(android.R.drawable.ic_menu_mapmode);
            mapView.addView(currentPos);
        }
        currentPos.setX(calcX(longitude));
        currentPos.setY(calcY(latitude));

    }

    private void drawSpots() {
        mapView.removeAllViews();

        SharedPreferences prefs = getSharedPreferences("spots", Context.MODE_PRIVATE);
        Map<String, ?> map = prefs.getAll();
        Spot spot;

        //calculate borders of geocoords
        for(Map.Entry<String,?> entry : map.entrySet()) {
            spot = Spot.fromString(entry.getValue().toString());
            if(spot.lng < xMin) {
                xMin = spot.lng;
            }
            if(spot.lng > xMax) {
                xMax = spot.lng;
            }
            if(spot.lat < yMin) {
                yMin = spot.lat;
            }
            if(spot.lat > yMax) {
                yMax = spot.lat;
            }
        }

        //add markers for each spot
        ImageView img;

        for(Map.Entry<String,?> entry : map.entrySet()) {
            spot = Spot.fromString(entry.getValue().toString());
            img = new ImageView(MainActivity.this);
            img.setImageResource(android.R.drawable.btn_star);

            img.setX(calcX(spot.lng));
            img.setY(calcY(spot.lat));
            mapView.addView(img);
        }
    }

    private void drawNearestSpot(Location loc) {
        double lat = loc.getLatitude();
        double lng = loc.getLongitude();

        SharedPreferences prefs = getSharedPreferences("spots", Context.MODE_PRIVATE);
        Map<String, ?> map = prefs.getAll();

        Spot spot;
        Spot nearestSpot = null;
        double distance;
        double nearestDistance = Double.MAX_VALUE;

        for(Map.Entry<String,?> entry : map.entrySet()) {
            spot = Spot.fromString(entry.getValue().toString());
            distance = Utils.calcDistance(spot.lat, spot.lng, lat, lng);
            if(distance < nearestDistance) {
                nearestDistance = distance;
                nearestSpot = spot;
            }
        }

        textLatLng.setText(nearestDistance + "m");
        if(nearestSpot != null && nearestDistance < 20) {
            if(nearestSpotView == null) {
                nearestSpotView = new ImageView(MainActivity.this);
                nearestSpotView.setImageResource(android.R.drawable.ic_dialog_map);
                mapView.addView(nearestSpotView);
            }
            nearestSpotView.setX(calcX(nearestSpot.lng));
            nearestSpotView.setY(calcY(nearestSpot.lat));

            tts.speak(nearestSpot.name, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    private float calcX(double lng) {
        int width = 360;
        double x = width * ((lng - xMin) / (xMax - xMin));
        return (float)x;
    }

    private float calcY(double lat) {
        int height = 400;
        double y = height * ((lat - yMin) / (yMax - yMin));
        return (float)y;
    }
}

