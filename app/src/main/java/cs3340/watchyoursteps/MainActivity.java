package cs3340.watchyoursteps;
import android.app.Activity;
import android.content.Context;
import android.hardware.*;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;

public class MainActivity extends Activity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private SensorManager sensorManager;
    private boolean mInitialized;
    private final float NOISE = (float) 2.0;
    private TextView count;
    private TextView miles;
    boolean activityRunning;
    boolean resetActivated = false; //check if reset button has been pressed
    float resetValue = 0; //this value stores the value of step counter to 0
    int stepsCount = 0;
    int milesCount = 0;
    double mLastX = 0.0;
    double mLastY = 0.0;
    double mLastZ = 0.0;
    public Button btn;

    public void onClickResetButton(View v) {
       // Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
       // vibe.vibrate(100);
        resetActivated = true;
        stepsCount = 0;
        count.setText("0");
        miles.setText("0");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        // Initialize Accelerometer sensor
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        count = (TextView) findViewById(R.id.count);
        miles = (TextView) findViewById(R.id.miles);
        mInitialized = false;
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        startSensor();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void startSensor() {
        mSensorManager.registerListener(this, mAccelerometer,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        activityRunning = false;

    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // event object contains values of acceleration, read those
        double x = event.values[0];
        double y = event.values[1];
        double z = event.values[2];


        final double alpha = 0.8; // constant for our filter below

        double[] gravity = {0, 0, 0};

        // Isolate the force of gravity with the low-pass filter.
        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

        // Remove the gravity contribution with the high-pass filter.
        x = event.values[0] - gravity[0];
        y = event.values[1] - gravity[1];
        z = event.values[2] - gravity[2];

        if (!mInitialized) {
            // sensor is used for the first time, initialize the last read values
            mLastX = x;
            mLastY = y;
            mLastZ = z;
            mInitialized = true;
        } else {
            // sensor is already initialized, and we have previously read values.
            // take difference of past and current values and decide which
            // axis acceleration was detected by comparing values

            double delX = Math.abs(mLastX - x);
            double delY = Math.abs(mLastY - y);
            double delZ = Math.abs(mLastZ - z);
            if (delX < NOISE)
                delX = (float) 0.0;
            if (delY < NOISE)
                delY = (float) 0.0;
            if (delZ < NOISE)
                delZ = (float) 0.0;
            mLastX = x;
            mLastY = y;
            mLastZ = z;

            if (delX > delY) {
                // Horizontal shake
                // do something here if you like

            } else if (delY > delX) {
                // Vertical shake
                // do something here if you like

            } else if ((delZ > delX) && (delZ > delY)) {
                // Z shake
                stepsCount = stepsCount + 1;
                if (stepsCount > 0) {
                    count.setText(String.valueOf(stepsCount));
                    milesCount = stepsCount / 2000;
                    miles.setText(String.valueOf(milesCount));
                }


            }
        }

    }
}