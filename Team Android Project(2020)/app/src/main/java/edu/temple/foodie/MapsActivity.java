package edu.temple.foodie;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public static final String EATERIES_EXTRA = "eateries";
    public static final String SELECTED_EATERY_EXTRA = "selected_eatery";

    private GoogleMap mMap;
    private HashMap<String, Eatery> eateries;
    private String selectedEateryID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Intent intent = getIntent();
        eateries = (HashMap<String, Eatery>) intent.getSerializableExtra(EATERIES_EXTRA);
        selectedEateryID = (String) intent.getSerializableExtra(SELECTED_EATERY_EXTRA);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMinZoomPreference(10.0f);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // Add a marker on temple university and move the camera
        LatLng temple = new LatLng(39.9809459, -75.15295);
        mMap.addMarker(new MarkerOptions().position(temple).title("Temple University"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(temple, 15.0f));

        addEateries();
    }

    /**
     * add eatery locations to map
     *
     * TODO:
     * create places api class to retrieve actual eatery info
     */
    private void addEateries(){
        //ArrayList<Eatery> eateries = new ArrayList<>();
        //Eatery testEatery = new Eatery("abcd123e","Test Eatery",39.9809980,-75.15390);
        //eateries.add(testEatery);

        Eatery selected = null;
        for(Map.Entry entery : eateries.entrySet() ){
            Eatery eatery = (Eatery) entery.getValue();
            Marker mMarker = mMap.addMarker(new MarkerOptions().position(eatery.getCoordinates())
                    .title(eatery.getName())
                    .snippet("status: " + eatery.getStatus()));
            if( selectedEateryID != null && eatery.getId().equals(selectedEateryID) ){
                mMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                selected = eatery;
            }
            mMarker.setTag(eatery);
            eatery.setMapMarker(mMarker);
        }

        if( selected != null ) {
            selected.getMapMarker().showInfoWindow();
            mMap.animateCamera(CameraUpdateFactory.newLatLng(selected.getMapMarker().getPosition()), 250, null);
        }
    }
}