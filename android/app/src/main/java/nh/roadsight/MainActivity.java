package nh.roadsight;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback2, SensorEventListener {

    //Layout Variables
    private TextView mtLongitude;
    private TextView mtLatitude;
    private TextView mtSpeed;
    //GPS Variables
    private LocationListener mLocationListener;
    private LocationManager mLocationManager;
    private boolean mGPSEnabled;
    private boolean mNetworkEnabled;
    private double mLongitude;
    private double mLatitude;
    private float mSpeed;
    //Shake Variables
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private long mLastTime;
    private float mX;
    private float mY;
    private float mZ;
    private static final int mShakeThreshold = 600;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mtLongitude = (TextView) findViewById(R.id.longitude);
        mtLatitude = (TextView) findViewById(R.id.latitude);
        mtSpeed = (TextView) findViewById(R.id.speed);

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer=mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        GPSPermissionCheckOver23();
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (mLocationManager != null) {
            mGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            mNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (mGPSEnabled || mNetworkEnabled) {
                Log.e("GPS Enable", "true");

                final List<String> m_lstProviders = mLocationManager.getProviders(false);
                mLocationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        Log.e("onLocationChanged", "onLocationChanged");
                        Log.e("location", "[" + location.getProvider() + "] (" + location.getLatitude() + "," + location.getLongitude() + ")");
                        mSpeed = location.getSpeed();
                        mLatitude = location.getLatitude();
                        mLongitude = location.getLongitude();

                        mtLongitude.setText("Longitude" + String.valueOf(mLongitude));
                        mtLatitude.setText("Latitude" + String.valueOf(mLatitude));
                        mtSpeed.setText(String.valueOf(mSpeed) + " km/h");

                        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        mLocationManager.removeUpdates(mLocationListener);
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                        Log.e("onStatusChanged", "onStatusChanged");
                    }

                    @Override
                    public void onProviderEnabled(String provider) {
                        Log.e("onProviderEnabled", "onProviderEnabled");
                    }

                    @Override
                    public void onProviderDisabled(String provider) {
                        Log.e("onProviderDisabled", "onProviderDisabled");
                    }
                };

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (String name : m_lstProviders) {
                            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }
                            mLocationManager.requestLocationUpdates(name, 1000, 0, mLocationListener);
                        }

                    }
                });

            } else {
                Log.e("GPS Enable", "false");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                });
            }
        }
        
    }
    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, mSensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER)
        {
            float X = event.values[0];
            float Y = event.values[0];
            float Z = event.values[0];
            long CurrentTime = System.currentTimeMillis();
            long GapOfTime = CurrentTime - mLastTime;
            if(GapOfTime > 100)
            {
                mLastTime = CurrentTime;
                float Speed = Math.abs(X+Y+Z-mX-mY-mZ)/GapOfTime*10000;
                if(Speed > mShakeThreshold)
                {
                    //이벤트 처리하는 부분 넣을것.
                    Toast.makeText(getApplicationContext(), "Shake 발생", Toast.LENGTH_SHORT).show();
                }

                mX = X;
                mY = Y;
                mZ = Z;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void GPSPermissionCheckOver23() {
        //마시멜로우 버전 이하면 if문에 걸리지 않습니다.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("GPS 사용 허가 요청");
            alertDialog.setMessage("앰버요청 발견을 알리기위해서는 사용자의 GPS 허가가 필요합니다.\n('허가'를 누르면 GPS 허가 요청창이 뜹니다.)");
            // OK 를 누르게 되면 설정창으로 이동합니다.
            alertDialog.setPositiveButton("허가",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                        }
                    });
            // Cancel 하면 종료 합니다.
            alertDialog.setNegativeButton("거절",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            alertDialog.show();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceRedrawNeeded(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

    /*
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
    }
    @Override
    public void surfaceRedrawNeededAsync(SurfaceHolder holder, Runnable drawingFinished) {
    }
    */
}
