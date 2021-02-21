package edu.temple.foodie;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class MenuFragment extends Fragment {

    private View mainV;
    private RequestQueue theQueue;
    private WebView webView;

    private int theCode = 100;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";

    private String name;
    private String lat;
    private String lon;

    public MenuFragment() {
        // Required empty public constructor
    }

    public static MenuFragment newInstance(String name, String lat, String lon){
        MenuFragment fragment = new MenuFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, name);
        args.putString(ARG_PARAM2, lat);
        args.putString(ARG_PARAM3, lon);
        fragment.setArguments(args);
        return fragment;
    }

    public static MenuFragment newInstance(String param1) {
        MenuFragment fragment = new MenuFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public static MenuFragment newInstance(String param1, String param2) {
        MenuFragment fragment = new MenuFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            name = getArguments().getString(ARG_PARAM1);
            lat = getArguments().getString(ARG_PARAM2);
            lon = getArguments().getString(ARG_PARAM3);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainV = inflater.inflate(R.layout.fragment_menu, container, false);


        theQueue = Volley.newRequestQueue(getContext());
        webView = mainV.findViewById(R.id.webView);

        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        if(name != null){
            searchMenu(name, lat, lon);
        }

        return mainV;
    }

    private void searchMenu(final String name, final String lat, final String lon) {
        Toast.makeText(getContext(), "Searching", Toast.LENGTH_SHORT).show();
        String name1 = name.toLowerCase();
        name1 = name1.replace(" food truck", "");
        name1 = name1.replace("food truck", "");
        name1 = name1.replace("@", "");
        name1 = name1.replace("temple", "");
        name1 = name1.replace("university", "");

        //Api search for restaurant information
        String url = "https://developers.zomato.com/api/v2.1/search?entity_type=group&q=" + name1 +  "&lat=" + lat + "&lon=" + lon + "&sort=real_distance&order=desc";

        final String[] resInfo = new String[2];
        JsonObjectRequest theRequest = new JsonObjectRequest(Request.Method.GET, url,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    int searchSize = Integer.valueOf(getString(R.string.searchSize));
                    int size = response.getJSONArray("restaurants").length();
                    for (int i = 0 ; i < Math.min(searchSize, size) ; i ++){
                        JSONObject jsonObject = response.getJSONArray("restaurants").getJSONObject(i).getJSONObject("restaurant");
                        if(jsonObject.getString("name").toLowerCase().contains(name) ||
                                jsonObject.getString("name").toLowerCase().split(" ", 2)[0].contains(name.split(" ", 2)[0])
                                || jsonObject.getString("name").regionMatches(true,0, name.split(" ", 2)[0], 0, name.split(" ", 2)[0].length())){
                            if(jsonObject.has("menu_url")){
                                Location location = new Location("dest");
                                location.setLatitude(Double.valueOf(lat));
                                location.setLongitude(Double.valueOf(lon));
                                Location location1 = new Location("match");
                                location1.setLatitude(Double.valueOf(jsonObject.getJSONObject("location").getString("latitude")));
                                location1.setLongitude(Double.valueOf(jsonObject.getJSONObject("location").getString("longitude")));
                                if(location.distanceTo(location1) < 500) { // compare if the distance is too far it may be another restaurant with same name
                                    webView.loadUrl(jsonObject.getString("menu_url"));
                                }else{
                                    continue;
                                }
                            }
                            return;
                        }

                    }
                    Toast.makeText(getContext(), "Not menu information for " + name, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("JsonRequestError", error.toString());
            }
        }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("user-key", "0c1f2ed7063299bb7b6053fa0b1bcbc0");
                return params;
            }
        };
        theQueue.add(theRequest);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }
}