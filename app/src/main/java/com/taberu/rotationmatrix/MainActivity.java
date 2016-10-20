package com.taberu.rotationmatrix;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetic;

    private float mLastX = 0, mLastY = 0, mLastZ = 0;
    private float[] mAccelerometerReading = new float[3];
    private float[] mMagnetometerReading = new float[3];

    private final float[] mRotationMatrix = new float[9];
    private final float[] mOrientationAngles = new float[3];

    private boolean mInitialized;
    private final static double NOISE = 0.5;

    public void updateAll() {
        TextView tvX = (TextView) findViewById(R.id.x_axis);
        TextView tvY = (TextView) findViewById(R.id.y_axis);
        TextView tvZ = (TextView) findViewById(R.id.z_axis);

        TextView tvR = (TextView) findViewById(R.id.x_roll);
        TextView tvP = (TextView) findViewById(R.id.y_pitch);
        TextView tvA = (TextView) findViewById(R.id.z_azimuth);

        mSensorManager.getRotationMatrix(mRotationMatrix, null, mAccelerometerReading, mMagnetometerReading);
        mSensorManager.getOrientation(mRotationMatrix, mOrientationAngles);

        float x = mAccelerometerReading[0]; // X
        float y = mAccelerometerReading[1]; // Y
        float z = mAccelerometerReading[2]; // Z
        if (!mInitialized) {
            mLastX = x;
            mLastY = y;
            mLastZ = z;

            tvX.setText("0.0");
            tvY.setText("0.0");
            tvZ.setText("0.0");

            tvR.setText("0.0");
            tvP.setText("0.0");
            tvA.setText("0.0");

            mInitialized = true;
        } else {
            float deltaX = Math.abs(mLastX - x);
            float deltaY = Math.abs(mLastY - y);
            float deltaZ = Math.abs(mLastZ - z);

            //Convert from Radians to Degrees.
            mOrientationAngles[0] = (float) Math.toDegrees(mOrientationAngles[0]);
            mOrientationAngles[1] = (float) Math.toDegrees(mOrientationAngles[1]);
            mOrientationAngles[2] = (float) Math.toDegrees(mOrientationAngles[2]);

            if (deltaX < NOISE) deltaX = (float) 0.0;
            if (deltaY < NOISE) deltaY = (float) 0.0;
            if (deltaZ < NOISE) deltaZ = (float) 0.0;

            mLastX = x;
            mLastY = y;
            mLastZ = z;

            tvX.setText(Float.toString(deltaX));
            tvY.setText(Float.toString(deltaY));
            tvZ.setText(Float.toString(deltaZ));

            tvA.setText(Float.toString(mOrientationAngles[0])); // z
            tvR.setText(Float.toString(mOrientationAngles[1])); // x
            tvP.setText(Float.toString(mOrientationAngles[2])); // y
        }
    }

    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, mAccelerometerReading, 0, mAccelerometerReading.length);
        }
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, mMagnetometerReading, 0, mMagnetometerReading.length);
        }
        updateAll();
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Reagir à mudança na acuracidade
    }

    public void updateOrientationAngles() {
        mSensorManager.getRotationMatrix(mRotationMatrix, null, mAccelerometerReading,
                mMagnetometerReading);
        mSensorManager.getOrientation(mRotationMatrix, mOrientationAngles);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mInitialized = false;

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null) {
            mMagnetic = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        }
    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer,
                SensorManager.SENSOR_DELAY_NORMAL,
                SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, mMagnetic,
                SensorManager.SENSOR_DELAY_NORMAL,
                SensorManager.SENSOR_DELAY_UI);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }
}
