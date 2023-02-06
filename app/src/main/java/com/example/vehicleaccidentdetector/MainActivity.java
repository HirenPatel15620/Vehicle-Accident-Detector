package com.example.vehicleaccidentdetector;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements SensorEventListener {

    int left = 0;
    int right = 0;
    int front = 0;
    int back = 0;
    TextView t;
    double latt = 0, logg = 0;
    TextView latitude;
    TextView longitude;
    LocationManager locationManager;
    LocationListener listener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        configureLiveLocation();
        configureAccelerometer();

    }

    public void configureLiveLocation() {

        t = (TextView) findViewById(R.id.location);
        latitude = (TextView) findViewById(R.id.latitude);
        longitude = (TextView) findViewById(R.id.longitude);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
//                t.append("\n " + location.getLongitude() + " " + location.getLatitude());
                int latDegree, latMin, latSec, logDegree, logMin, logSec;
                double lat = location.getLatitude(), log = location.getLongitude();
                latDegree = (int) lat;
                logDegree = (int) log;
                latt = lat;
                logg = log;
                latMin = (int) ((lat - latDegree)*60);
                logMin = (int) ((log - logDegree)*60);
                latSec = (int) (((lat - latDegree)*60 - latMin)*60);
                logSec = (int) (((log - logDegree)*60 - logMin)*60);
                latitude.setText("" + latDegree+"°"+latMin+"'"+latSec+"\"");
                longitude.setText("" + logDegree+"°"+logMin+"'"+logSec+"\"");
            }
        };
        configure_button();
    }

    public void configureAccelerometer() {
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {

            Sensor acceleroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (acceleroSensor != null) {
                sensorManager.registerListener((SensorEventListener) this, acceleroSensor, SensorManager.SENSOR_DELAY_NORMAL);

                int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
                if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
//                    MyMessage();
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 0);
                }
            }

        } else {
            Toast.makeText(this, "Sensor service not detected.", Toast.LENGTH_SHORT).show();
        }
    }

    public void MyMessage() {

        ArrayList<String> msg = new ArrayList<>(3);
        ArrayList<String> url = new ArrayList<>(2);
        msg.add(0,"                   !!ALERT!! \n\nHello There,  ");
        msg.add(1," \nI have added you as my Emergency Contact,  ");
        msg.add(2,"My Vehicle Has met with an Accident, Here is my Last Location....");
        url.add(0,"http://maps.google.com/?q=<");
        url.add(1,"" + latt + ">,<" + logg + ">" + "");
//        String number = "+917874930639";
        String number = "+919327384178";
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendMultipartTextMessage(number,
                null,
                msg,
                null,
                null);
        smsManager.sendMultipartTextMessage(number,
                null,
                url,
                null,
                null);
        Toast.makeText(this, "Messgae Sent", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            ((TextView) findViewById(R.id.xValue)).setText("" + event.values[0]);
            ((TextView) findViewById(R.id.yValue)).setText("" + event.values[1]);
            ((TextView) findViewById(R.id.zValue)).setText("" + event.values[2]);

            double x = event.values[0], y = event.values[1], z = event.values[2];
            if (x > 9 && left == 0) {
                MyMessage();
                left = 1;
            }
            if (x < -8 && right == 0) {
                MyMessage();
                right = 1;
            }
            if (y < -5 && front == 0) {
                MyMessage();
                front = 1;
            }
            if (y > 4 && back == 0) {
                MyMessage();
                back = 1;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 10:
                configure_button();
                break;
            default:
                break;
        }
    }
    void configure_button() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}
                        , 10);
            }
            return;
        }
        locationManager.requestLocationUpdates("gps", 5000, 0, listener);
    }
}



