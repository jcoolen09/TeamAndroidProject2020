package edu.temple.foodie;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InfoListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InfoListFragment extends Fragment {

    private RequestQueue theQueue;
    private View mainV;
    private InfoListInterface mListener;

    private JSONArray resInfoArray;
    private ArrayList<String> resInfoList;
    private HashMap<String, Eatery> eateries;

    private ListView listView;


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String keyword;
    private String radius;

    public InfoListFragment() {
        // Required empty public constructor
    }


    public static InfoListFragment newInstance(String param1, String param2) {
        InfoListFragment fragment = new InfoListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof InfoListInterface){
            mListener = (InfoListInterface) context;
        }else{
            throw new RuntimeException(context + "need to implement InfoListInterface");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            keyword = getArguments().getString(ARG_PARAM1);
            radius = getArguments().getString(ARG_PARAM2);
        }else{
            keyword = null;
            radius = null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainV = inflater.inflate(R.layout.fragment_info_list, container, false);

        resInfoList = new ArrayList<>();
        theQueue = Volley.newRequestQueue(getContext());
        listView = mainV.findViewById(R.id.InfoList);

        if(keyword != null){
            googlePlaceSear(keyword, radius);
        }else {
            googlePlaceSear();
        }
        return mainV;
    }

    private void googlePlaceSear(String keyword, String radius){
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=39.9809459,-75.15295&radius=" + radius + "&keyword=" + keyword +"&key=AIzaSyCem4_VZn7K_mRY5KfpGvwP6m5zgMfADas";
        final String[] resInfo = new String[2];
        JsonObjectRequest theRequest = new JsonObjectRequest(Request.Method.GET, url,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    resInfoArray = response.getJSONArray("results");
                    for(int i = 0; i < resInfoArray.length(); i++){
                        JSONObject theObject = resInfoArray.getJSONObject(i);
                        if(theObject.getString("business_status").equals("OPERATIONAL") && theObject.getJSONObject("opening_hours").getString("open_now").equals("true")) {
                            resInfoList.add(theObject.getString("name") + "\nopen");
                        }else{
                            resInfoList.add(theObject.getString("name") + "\nclosed");
                        }
                    }
                    listView.setAdapter(new MyListAdaper(getContext(), R.layout.search_results,
                            resInfoList));
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
        );
        theQueue.add(theRequest);
    }

    private void googlePlaceSear(){
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=39.9809459,-75.15295&radius=800&keyword=food&key=AIzaSyCem4_VZn7K_mRY5KfpGvwP6m5zgMfADas";
        final String[] resInfo = new String[2];
        JsonObjectRequest theRequest = new JsonObjectRequest(Request.Method.GET, url,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    resInfoArray = response.getJSONArray("results");
                    eateries = new HashMap<>();
                    for(int i = 0; i < resInfoArray.length(); i++){
                        JSONObject theObject = resInfoArray.getJSONObject(i);
                        Eatery eatery = new Eatery(theObject);
                        eateries.put(eatery.getId(), eatery);
                        if(eatery.isOpenNow()) {
                            resInfoList.add(theObject.getString("name") + "\nopen");
                        }else{
                            resInfoList.add(theObject.getString("name") + "\nclosed");
                        }
                    }
                    mListener.eateriesFound(eateries);
                    listView.setAdapter(new MyListAdaper(getContext(), R.layout.search_results,
                            resInfoList));
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
        );
        theQueue.add(theRequest);
    }



    private class MyListAdaper extends ArrayAdapter<String> {
        private int layout;
        private List<String> mObjects;
        private MyListAdaper(Context context, int resource, List<String> objects) {
            super(context, resource, objects);
            mObjects = objects;
            layout = resource;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            JSONObject theRestaurant;
            try {
                theRestaurant = resInfoArray.getJSONObject(position);
            } catch (JSONException e) {
                theRestaurant = null;
                e.printStackTrace();
            }
            ViewHolder mainViewholder = null;
            if(convertView ==null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.title = (TextView) convertView.findViewById(R.id.restInfoText);
                viewHolder.favoriteButton = (ImageButton) convertView.findViewById(R.id.favoriteButton);
                viewHolder.CheckInButton = (Button) convertView.findViewById(R.id.CheckInbutton);
                viewHolder.mapButton = (ImageButton) convertView.findViewById(R.id.mapButton);
                convertView.setTag(viewHolder);
            }
            mainViewholder = (ViewHolder) convertView.getTag();
            //when the favorite button is click pass the place_id to main activity
            final JSONObject finalTheRestaurant = theRestaurant;
            mainViewholder.favoriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        mListener.favoriteClick(finalTheRestaurant.getString("place_id"),
                                finalTheRestaurant.getString("name")
                                );
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            //when clicked show map with eatery selected
            mainViewholder.mapButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        mListener.viewInMapClick(finalTheRestaurant.getString("place_id")
                        );
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            //when checkIn button is click pass the place_id, latitude and longitude to main activity
            mainViewholder.CheckInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        mListener.checkInClick(finalTheRestaurant.getString("place_id"),
                                finalTheRestaurant.getString("name"),
                                finalTheRestaurant.getJSONObject("geometry").getJSONObject("location").getString("lat"),
                                finalTheRestaurant.getJSONObject("geometry").getJSONObject("location").getString("lng")
                                );

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            //when the textview is clicked pass the information to Main Activity to create a menufragment
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        mListener.resSelected(finalTheRestaurant.getString("name"),
                                finalTheRestaurant.getJSONObject("geometry").getJSONObject("location").getString("lat"),
                                finalTheRestaurant.getJSONObject("geometry").getJSONObject("location").getString("lng")
                        );
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            mainViewholder.title.setText(getItem(position));


            return convertView;
        }
    }
    public class ViewHolder {


        TextView title;
        ImageButton favoriteButton;
        ImageButton mapButton;
        Button CheckInButton;
    }

    public interface InfoListInterface{
        void resSelected(String name, String lat, String lon);
        void favoriteClick(String ID, String name);
        void checkInClick(String ID, String name, String lat, String lon);
        void eateriesFound(HashMap<String, Eatery> eateries);
        void viewInMapClick(String ID);
    }

}