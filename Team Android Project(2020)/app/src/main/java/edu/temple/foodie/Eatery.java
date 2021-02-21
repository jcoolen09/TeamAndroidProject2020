package edu.temple.foodie;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Formatter;

public class Eatery implements Parcelable, Serializable {
    private String id;
    private String name;
    private double lat;
    private double lon;
    private String address;
    private Marker mapMarker;
    private LatLng coordinates;
    private Boolean openNow;

    public Eatery(String id, String name, Double lat, Double lon){
        this.id = id;
        this.name = name;
        this.lat = lat;
        this.lon = lon;
        this.coordinates = new LatLng(lat, lon);
    }

    public Eatery(JSONObject args) throws JSONException {
        this.id = args.getString("place_id");
        this.name = args.getString("name");
        this.address = args.getString("vicinity");
        try {
            if(args.getString("business_status").equals("OPERATIONAL")) {
                try {
                    this.openNow = args.getJSONObject("opening_hours").getBoolean("open_now");
                }catch (JSONException e){
                    this.openNow = false;
                }
            }else{
                this.openNow = false;
            }
            this.lat = args.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
            this.lon = args.getJSONObject("geometry").getJSONObject("location").getDouble("lng");
            this.coordinates = new LatLng(lat, lon);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    protected Eatery(Parcel in) {
        id = in.readString();
        name = in.readString();
        lat = in.readDouble();
        lon = in.readDouble();
        address = in.readString();
        coordinates = in.readParcelable(LatLng.class.getClassLoader());
        byte tmpOpenNow = in.readByte();
        openNow = tmpOpenNow == 0 ? null : tmpOpenNow == 1;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeDouble(lat);
        dest.writeDouble(lon);
        dest.writeString(address);
        dest.writeParcelable(coordinates, flags);
        dest.writeByte((byte) (openNow == null ? 0 : openNow ? 1 : 2));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Eatery> CREATOR = new Creator<Eatery>() {
        @Override
        public Eatery createFromParcel(Parcel in) {
            return new Eatery(in);
        }

        @Override
        public Eatery[] newArray(int size) {
            return new Eatery[size];
        }
    };

    // SETTER METHODS
    public void setMapMarker(Marker marker){
        this.mapMarker = marker;
    }

    // GETTER METHODS

    public String getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public String getAddress(){
        return address;
    }

    public Boolean isOpenNow(){
        return openNow;
    }

    public LatLng getCoordinates(){
        return coordinates;
    }

    public Marker getMapMarker(){
        return mapMarker;
    }

    public String getStatus(){
        return openNow ? "open" : "closed";
    }

}
