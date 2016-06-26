package com.andrehulse.lanofraiandroid.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Matrix;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.andrehulse.lanofraiandroid.R;
import com.andrehulse.lanofraiandroid.controller.PositionLocation;

import java.util.List;

public class CadeleFraiActivity extends Activity implements LocationListener {

	private PositionLocation mPositionLocation;
	private SensorManager mSensorManager;

	private LocationManager mLocationManager;
	private String provider;
	
	private TextView mLonjura;
	private ImageView mImageHand;
	private Button mVorta;

	private SensorEventListener mEventListenerOrientation;
	private SensorEventListener mEventListenerMagnetic;
	private SensorEventListener mEventListenerAccel;

	/** Called when the activity is first created. */
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cadele_frai);

		mImageHand = (ImageView) findViewById(R.id.compass_hand);
		mLonjura = (TextView) findViewById(R.id.lonjura_txt);
		mVorta = (Button) findViewById(R.id.cadelefrai_vorta_bt);
		mVorta.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(CadeleFraiActivity.this, LaNoFraiMainActivity.class);
				startActivity(i);
			}
		});
		
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
	    provider = mLocationManager.getBestProvider(criteria, false);

		mPositionLocation = new PositionLocation();
	    mPositionLocation.setLastLocation(mLocationManager.getLastKnownLocation(provider));

		mLocationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
		List<String> providers = mLocationManager.getProviders(true);
		Location bestLocation = null;
		for (String provider : providers) {
			Location l = mLocationManager.getLastKnownLocation(provider);
			if (l == null) {
				continue;
			}
			if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
				// Found best last known location: %s", l);
				bestLocation = l;
			}
		}
		mPositionLocation.setLastLocation(bestLocation);

	    if(mPositionLocation.hasValidLocation()){
	    	mPositionLocation.setTime(System.currentTimeMillis());
	    }
	    
		initListeners();
	}
	
	private void initListeners() {
		mEventListenerOrientation = new SensorEventListener() {
			@Override
			public void onSensorChanged(android.hardware.SensorEvent event) {

				float[] values = event.values;
				mPositionLocation.setOrientation(values[0]);
				draw();

				if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
					mPositionLocation.setGravity(event.values);
				if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
					mPositionLocation.setGeomagnetic(event.values);
				if (mPositionLocation.hasGeoMagneticAndGravity()) {
					float R[] = new float[9];
					float I[] = new float[9];
					boolean success = SensorManager.getRotationMatrix(R, I, mPositionLocation.getGravity(), mPositionLocation.getGeomagnetic());
					if (success) {
						float orientation[] = new float[3];
						orientation = SensorManager.getOrientation(R, orientation);
						mPositionLocation.setAzimuth(orientation[0]); // orientation contains: azimut, pitch and roll
						mPositionLocation.setPitch(orientation[1]);
					}
				}
			}

			@Override
			public void onAccuracyChanged(android.hardware.Sensor sensor, int accuracy) {

			}
		};		

		mLocationManager.requestLocationUpdates(provider, 1000, 100, this);
	}	

	@Override
	protected void onResume() {
		super.onResume();

		mSensorManager.registerListener(mEventListenerOrientation,
				mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
				SensorManager.SENSOR_DELAY_FASTEST);

		mSensorManager.registerListener(mEventListenerOrientation,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_FASTEST);

		mSensorManager.registerListener(mEventListenerOrientation,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
				SensorManager.SENSOR_DELAY_FASTEST);

		if(mLocationManager != null){
			mLocationManager.requestSingleUpdate(provider, this, null); // min API 9
		}
	}

	@Override
	protected void onStop() {
		mSensorManager.unregisterListener(mEventListenerOrientation);
		//mSensorManager.unregisterListener(mEventListenerMagnetic);
		//mSensorManager.unregisterListener(mEventListenerAccel);
		mLocationManager.removeUpdates(this);
		super.onStop();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(mEventListenerOrientation);
		//mSensorManager.unregisterListener(mEventListenerMagnetic);
		//mSensorManager.unregisterListener(mEventListenerAccel);
		mLocationManager.removeUpdates(this);
	}


	@Override
	public void onLocationChanged(Location location) {
		mPositionLocation.setLastLocation(location);
		draw();
	}
	
	protected void onLocationReceived(Context context, Location location) {
		mPositionLocation.setLastLocation(location);
		draw();
    }

	private synchronized void draw() {
		mLonjura.setText("Numa lonjura de: "+mPositionLocation.calculateDistance()+" km.");
		Matrix matrix = new Matrix();
		mImageHand.setScaleType(ScaleType.MATRIX);   //required
		matrix.postRotate(mPositionLocation.calculateNeedleAngle(),
				mImageHand.getDrawable().getBounds().width()/2, 
				mImageHand.getDrawable().getBounds().height()/2);
		mImageHand.setImageMatrix(matrix);
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
	}
}
