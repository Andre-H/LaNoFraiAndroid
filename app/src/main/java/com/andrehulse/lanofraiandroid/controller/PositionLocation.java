package com.andrehulse.lanofraiandroid.controller;

import android.location.Location;

import java.util.ArrayList;
import java.util.Observable;

import static android.util.FloatMath.cos;
import static android.util.FloatMath.sin;
import static java.lang.Math.atan2;
import static java.lang.Math.toDegrees;

/**
 * Created by Andre on 09/09/2015.
 */
public class PositionLocation extends Observable{

    static public final double sfFraiLat = -27.023893;
    static public final double sfFraiLon = -50.924207;
    private Location lastLocation;
    private float azimuth =0;
    private float pitch =0;
    private float orientation;
    private float[] gravity;
    private float[] geomagnetic;
    private ArrayList<Float> sinFilter = new ArrayList<Float>(1);
    private ArrayList<Float> cosFilter = new ArrayList<Float>(1);


    public void setLastLocation(Location lastLocation) {
        this.lastLocation = lastLocation;
    }

    public boolean hasValidLocation() {
        return lastLocation != null;
    }

    public void setTime(long time) {
        lastLocation.setTime(time);
    }

    public void setOrientation(float orientation) {
        this.orientation = orientation;
    }

    public void setGravity(float[] gravity) {
        this.gravity = gravity;
    }

    public void setGeomagnetic(float[] geomagnetic) {
        this.geomagnetic = geomagnetic;
    }

    public boolean hasGeoMagneticAndGravity() {
        return gravity != null && geomagnetic != null;
    }

    public ArrayList<Float> filter = new ArrayList<Float>();
    public float filterAverage = 0;

    public void setAzimuth(float azimuth) {
        if(pitch>-1.4) {
            this.azimuth = azimuth;
        }
    }

    public double getLatitude() {
        return lastLocation.getLatitude();
    }

    public double getLongitude() {
        return lastLocation.getLongitude();
    }

    public Float getAzimuth() {
        return azimuth;
    }

    public float[] getGeomagnetic() {
        return geomagnetic;
    }

    public float[] getGravity() {
        return gravity;
    }

    public double calculateDistance(){
		/* Haversine formula:
		a = sin²(Δφ/2) + cos(φ1).cos(φ2).sin²(Δλ/2)
		c = 2.atan2(√a, √(1−a))
		d = R.c
		where	φ is latitude, λ is longitude, R is earth’s radius (mean radius = 6,371km)
		 	note that angles need to be in radians to pass to trig functions!
	 	*/

        double radius = 6371; // km

        //point 1: current location
        //point 2: Frai

        double lat1 = getLatitude();
        double lon1 = getLongitude();

        double lat2 = sfFraiLat;
        double lon2 = sfFraiLon;

        double diffLat = Math.toRadians(lat2-lat1);
        double diffLon = Math.toRadians(lon2 - lon1);

        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.sin(diffLat / 2) * Math.sin(diffLat/2) +
                Math.sin(diffLon/2) * Math.sin(diffLon/2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return (double) (Math.round(radius * c * 100)) / 100;
    }

    public float calculateBearing(){
        double diffLon = Math.toRadians(sfFraiLon - getLongitude());
        double y = Math.sin(diffLon) * Math.cos(sfFraiLat);
        double x = Math.cos(getLatitude()) * Math.sin(sfFraiLat) -
                Math.sin(getLatitude()) * Math.cos(sfFraiLat) * Math.cos(diffLon);
        double bearing = Math.toDegrees(Math.atan2(y, x));
        return (float) (initial(getLatitude(), getLongitude(), sfFraiLat, sfFraiLon));
    }

    private double initial (double lat1, double long1, double lat2, double long2){
        return Math.round((_bearing(lat1, long1, lat2, long2) + 360.0) % 360);
    }

    static private double _bearing(double lat1, double long1, double lat2, double long2){

        double degToRad = Math.PI / 180.0;

        double phi1 = lat1 * degToRad;

        double phi2 = lat2 * degToRad;

        double lam1 = long1 * degToRad;

        double lam2 = long2 * degToRad;


        return  Math.round(Math.atan2(Math.sin(lam2-lam1)*Math.cos(phi2),

                Math.cos(phi1)*Math.sin(phi2) - Math.sin(phi1)*Math.cos(phi2)*Math.cos(lam2-lam1)

        ) * 180/Math.PI);

    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public float calculateNeedleAngle() {
        float ang = getAzimuth();

        sinFilter.add(sin(ang));
        if(sinFilter.size() > 300)
            sinFilter.remove(0);
        float sinAvg = 0;
        for(int i=0; i<sinFilter.size(); i++)
            sinAvg += sinFilter.get(i);
        sinAvg = sinAvg / sinFilter.size();

        cosFilter.add(cos(ang));
        if(cosFilter.size() > 300)
            cosFilter.remove(0);
        float cosAvg = 0;
        for(int i=0; i<cosFilter.size(); i++)
            cosAvg += cosFilter.get(i);
        cosAvg = cosAvg / cosFilter.size();

        return calculateBearing() - (float) toDegrees(atan2(sinAvg, cosAvg));
    }
}
