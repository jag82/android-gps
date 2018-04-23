package com.example.jag.helloworld;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

public class GPSSpotter extends Service {

    private final Context ctx;

    protected LocationManager locationManager;

    public GPSSpotter(Context context) {
        this.ctx = context;
        this.locationManager = (LocationManager) ctx.getSystemService(LOCATION_SERVICE);
    }

    public Spot spot(String name) {
        Spot spot = null;
        try {

            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if(!isGPSEnabled) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                ctx.startActivity(intent);
            } else {
                if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    //TODO: request permissions?
                } else {
                    Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    spot = new Spot(location.getLatitude(), location.getLongitude(), name);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return spot;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
}
