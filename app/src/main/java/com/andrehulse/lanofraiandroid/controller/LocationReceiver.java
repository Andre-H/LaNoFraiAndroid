package com.andrehulse.lanofraiandroid.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

public class LocationReceiver extends BroadcastReceiver{

	private static final String TAG = "LocationReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		Location loc = (Location) intent.getParcelableExtra(LocationManager.KEY_LOCATION_CHANGED);
		if (loc != null){
			onLocationReceived(context, loc);
			return;
		}
		if (intent.hasExtra(LocationManager.KEY_PROVIDER_ENABLED)){
			boolean enabled = intent.getBooleanExtra(LocationManager.KEY_PROVIDER_ENABLED, false);
			onProviderEnabledChange(enabled);
		}
	}

	protected void onLocationReceived(Context context, Location loc) {
		Log.d(TAG, this + "Recebeu localização de " + loc.getProvider() + ": "+
				loc.getLatitude() + ", " + loc.getLongitude());
	}

	protected void onProviderEnabledChange(boolean enabled) {
		Log.d(TAG, "Provider" + ((enabled)? " enabled" : " disabled"));
	}
	
}
