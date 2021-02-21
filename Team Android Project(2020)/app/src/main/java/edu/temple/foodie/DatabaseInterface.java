package edu.temple.foodie;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.StringDef;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DatabaseInterface {
    private static final String TAG = "DatabaseInterface ===>>>";
    private static final String API_DOMAIN = "https://findmeapp.tech/foodie";
    static final String ACCOUNT_EXT = "/account";
    static final String STORE_EXT = "/store";
    static final String GET_BALANCE = "/balance";
    static final String SET_BALANCE = "/set-balance";
    static final String ADD_FAVORITE_ACTION = "/add-favorite";
    static final String GET_FAVORITES = "/favorite-eateries";
    static final String GET_CHECKINS = "/checkins";
    static final String ADD_ACTION = "/add";
    static final String CHECKIN_ACTION = "/checkin";
    static final String LIST_ACTION = "/list";

    private final String SUCCESS = "success";
    private final String FAILED = "failed";

    private RequestQueue queue;
    private DbResponseListener callback;

    public DatabaseInterface(Context context){
        this.queue = Volley.newRequestQueue( context );
        this.callback = (DbResponseListener) context;
    }

    /**
     * make get account api call
     *
     * @param email
     */
    public void getAccount( String email ){

        Map<String, String> params = new HashMap<>();
        params.put("email", email);
        String url = API_DOMAIN + ACCOUNT_EXT;
        this.makeVolleyRequest( url, params );
    }

    /**
     * make get balance request
     *
     * @param account_id
     */
    public void getBalance( Integer account_id ){

        Map<String, String> params = new HashMap<>();
        params.put("account_id", String.valueOf(account_id));
        String url = API_DOMAIN + ACCOUNT_EXT + GET_BALANCE;
        this.makeVolleyRequest( url, params );
    }

    /**
     * make set balance request
     *
     * @param account_id
     * @param balance
     */
    public void setBalance( Integer account_id, double balance ){

        Map<String, String> params = new HashMap<>();
        params.put("account_id", String.valueOf(account_id));
        params.put("balance", String.valueOf(balance));
        String url = API_DOMAIN + ACCOUNT_EXT + SET_BALANCE;
        this.makeVolleyRequest( url, params );
    }

    /**
     * get checkins for user
     *
     * @param account_id
     */
    public void getAccountCheckins( Integer account_id ){
        Map<String, String> params = new HashMap<>();
        params.put("account_id", String.valueOf(account_id));
        String url = API_DOMAIN + ACCOUNT_EXT + GET_CHECKINS;
        this.makeVolleyRequest( url, params );
    }

    /**
     * get checkins for eatery
     *
     * @param store_id
     */
    public void getStoreCheckins( String store_id ){
        Map<String, String> params = new HashMap<>();
        params.put("store_id", store_id);
        String url = API_DOMAIN + STORE_EXT + GET_CHECKINS;
        this.makeVolleyRequest( url, params );
    }

    /**
     * make get eatery request
     *
     * @param store_id
     */
    public void getStore( Integer store_id ){

        Map<String, String> params = new HashMap<>();
        params.put("store_id", String.valueOf(store_id));
        String url = API_DOMAIN + STORE_EXT;
        this.makeVolleyRequest( url, params );
    }

    /**
     * make add eatery request
     *
     * @param name
     * @param google_id
     */
    public void addStore( String name, String google_id ){

        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("google_id", google_id);
        String url = API_DOMAIN + STORE_EXT + ADD_ACTION;
        this.makeVolleyRequest( url, params );
    }

    /**
     * get a list of all eateries
     *
     */
    public void listStores(){
        Map<String, String> params = new HashMap<>();
        String url = API_DOMAIN + STORE_EXT + LIST_ACTION;
        this.makeVolleyRequest( url, params );
    }

    /**
     * make check in to eatery request
     *
     * @param google_id
     * @param store_name
     * @param account_id
     */
    public void checkIn(String google_id, String store_name, Integer account_id){
        Map<String, String> params = new HashMap<>();
        params.put("google_id", google_id);
        params.put("name", store_name);
        params.put("account_id", String.valueOf(account_id));
        String url = API_DOMAIN + STORE_EXT + CHECKIN_ACTION;
        this.makeVolleyRequest( url, params );
    }

    /**
     * make add eatery as favorite request
     *
     * @param google_id
     * @param store_name
     * @param account_id
     */
    public void addFavorite(String google_id, String store_name, Integer account_id){
        Map<String, String> params = new HashMap<>();
        params.put("google_id", google_id);
        params.put("name", store_name);
        params.put("account_id", String.valueOf(account_id));
        String url = API_DOMAIN + STORE_EXT + ADD_FAVORITE_ACTION;
        this.makeVolleyRequest( url, params );
    }

    /**
     * get list of favorite eateries
     *
     * @param account_id
     */
    public void getFavoriteEateries(Integer account_id){
        Map<String, String> params = new HashMap<>();
        params.put("account_id", String.valueOf(account_id));
        String url = API_DOMAIN + ACCOUNT_EXT + GET_FAVORITES;
        this.makeVolleyRequest( url, params );
    }

    /**
     * make post request to webserver api
     *
     * @param url
     * @param params
     */
    private void makeVolleyRequest( String url, final Map<String, String> params ){

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parseResponse( response );
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO: Handle error
                callback.errorResponse( error.getMessage() );
            }
        }
        ){
            @Override
            protected Map<String, String> getParams()
            {
                return params;
            }
        };

        queue.add( postRequest );
    }

    /**
     * parses the response from the api
     * calls response on success
     * calls errorResponse on failure
     *
     * @param response
     */
    private void parseResponse(String response){
        System.out.println(">>> response: " + response);
        JSONObject result = null;
        try {
            result = new JSONObject(response);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (result == null){
            callback.errorResponse("Unknown error");
        }else {
            try {
                if (result.getString("result").equals(FAILED)) {
                    String error = result.getString("error");
                    if (error.isEmpty() && result.has("exception")) {
                        error = result.getString("exception");
                    }
                    callback.errorResponse(error);
                } else {
                    JSONArray data = null;
                    if (result.has("data")) {
                        data = result.getJSONArray("data");
                    }
                    callback.response(data);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Interface to get response from api
     * must be implemented by activity that uses the db interface
     */
    public interface DbResponseListener {
        //returns data on success
        void response( JSONArray data );

        //returns error message on error
        void errorResponse( String error );
    }

}