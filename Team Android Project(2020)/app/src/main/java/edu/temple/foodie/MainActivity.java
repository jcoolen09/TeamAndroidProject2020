package edu.temple.foodie;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements LoginFragment.loginInterface
        , DatabaseInterface.DbResponseListener, InfoListFragment.InfoListInterface
        , AccountBalance.UpdateDatabaseAccountBalance, BalanceFragment.BalanceInterface{

    private static final String LOGIN_FRAG_TAG = "login_frag";
    private static final String BALANCE_FRAG_TAG = "balance_frag";
    private static final String INFO_LIST_FRAG_TAG = "info_list_frag";
    private static final String FAVORITES_FRAG_TAG = "favorites_frag";
    private static final String LOGIN_USER_EMAIL_PREF = "user_email";
    static final String GET_ACCOUNT_REQUEST = "get_account";
    static final String GET_ACCOUNT_BALANCE_REQUEST = "get_account_balance";
    static final String SET_ACCOUNT_BALANCE_REQUEST = "set_account_balance";
    static final String CHECKIN_REQUEST = "checkin_request";
    static final String ADD_FAVORITE_REQUEST = "add_favorite_request";
    static final String GET_FAVORITES_REQUEST = "get_favorites_request";

    private final int RC_SIGN_IN = 0;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInAccount googleSignInAccount;

    private boolean signedIn = false;
    private String userEmail = "";
    private String dbrequest = "";
    private DatabaseInterface dbInterface;
    private Account userAccount;
    private AccountBalance accountBalance;
    private HashMap<String, Eatery> eateries;
    private HashMap<String, String> favorites;
    private String selectedEateryID;

    private LoginFragment loginFragment;
    private InfoListFragment infoListFragment;
    private BalanceFragment balanceFragment;
    private FavoritesFragment favoritesFragment;

    SharedPreferences sharedPreferences;

    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        locationManager = getSystemService(LocationManager.class);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
            }
        };

        sharedPreferences = getSharedPreferences("SP", MODE_PRIVATE);
        signedIn = sharedPreferences.getBoolean(String.valueOf(R.string.signed), false);
        userEmail = sharedPreferences.getString(LOGIN_USER_EMAIL_PREF, "");

        if( userEmail.equals("") ){
            signedIn = false;
        }

        FragmentManager fm = getSupportFragmentManager();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        dbInterface = new DatabaseInterface(this);


        if( signedIn ){
            googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
            getAccountFromDb(userEmail);
            loadDefaultView();
        }else{
            loadLoginFragment();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(String.valueOf(R.string.signed), signedIn);
        editor.putString(LOGIN_USER_EMAIL_PREF, userEmail);
        editor.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbInterface = null;
    }

    /**
     * creates main menu for app
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appmenu, menu);
        menu.findItem(R.id.action_mapview).setVisible(false);
        menu.findItem(R.id.action_balanceview).setVisible(false);
        menu.findItem(R.id.action_listview).setVisible(false);
        menu.findItem(R.id.action_favorites).setVisible(false);
        if( signedIn ){
            menu.findItem(R.id.action_mapview).setVisible(true);
            menu.findItem(R.id.action_balanceview).setVisible(true);
            menu.findItem(R.id.action_listview).setVisible(true);
            menu.findItem(R.id.action_favorites).setVisible(true);
        }
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * handles click events for main menu items
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_signout:
                // User chose the "Settings" item, show the app settings UI...
                signOutClick();
                return true;

            case R.id.action_mapview:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                startMapActivity();
                return true;

            case R.id.action_balanceview:
                loadBalanceFragment();
                return true;

            case R.id.action_listview:
                loadDefaultView();
                return true;

            case R.id.action_favorites:
                loadFavoritesFragment();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    /**
     * sign in user with google credentials
     */
    private void signIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    /**
     * catch result of google sign in activity
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    /**
     * handle result of google sign in activity
     *
     * @param completedTask
     */
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask){
        try{
            googleSignInAccount = completedTask.getResult(ApiException.class);
            signedIn = true;
            userEmail = googleSignInAccount.getEmail();
            loginFragment.loggedIn(googleSignInAccount);
            getAccountFromDb(userEmail);
            invalidateOptionsMenu();
            Log.d("FOODIE",googleSignInAccount.getDisplayName() + " logged in");
            Toast.makeText(MainActivity.this, "Welcome " + googleSignInAccount.getDisplayName() + "!", Toast.LENGTH_SHORT).show();
            loadDefaultView();
            //startMapActivity();

        }catch (ApiException e){
            Log.d("signinfail", "signInResult:failed code=" + e +" " + e.getStatusCode());
        }
    }

    /**
     * Loads login fragment into main activity container
     */
    private void loadLoginFragment() {
        if( loginFragment == null ){
            loginFragment = LoginFragment.newInstance(signedIn);
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container1, loginFragment, LOGIN_FRAG_TAG)
                .commit();
    }

    /**
     * Loads balance fragment into main activity container
     */
    private void loadBalanceFragment(){
        if( balanceFragment == null ){
            balanceFragment = BalanceFragment.newInstance(accountBalance.getBalance());
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container1, balanceFragment, BALANCE_FRAG_TAG)
                .commit();
    }

    /**
     * Loads info list fragment (eateries lists) into main activity container
     */
    private void loadDefaultView(){
        if( infoListFragment == null ) {
            infoListFragment = new InfoListFragment();
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container1, infoListFragment, INFO_LIST_FRAG_TAG)
                .commit();
    }

    private void loadFavoritesFragment(){
        if( favorites != null ) {
            ArrayList<Eatery> favoriteEateries = new ArrayList<>();
            for (Map.Entry entry : this.favorites.entrySet()) {
                String id = (String) entry.getKey();
                Eatery eatery = eateries.get(id);
                favoriteEateries.add(eatery);
            }
            Log.d("Favorites", "size " + favoriteEateries.size());

            favoritesFragment = FavoritesFragment.newInstance(favoriteEateries);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container1, favoritesFragment, FAVORITES_FRAG_TAG)
                    .commit();
        }else{
            Toast.makeText(MainActivity.this,"Favorites have not been reterived yet.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * start map activity
     */
    private void startMapActivity(){
        if( signedIn ) {
            Intent intent = new Intent(MainActivity.this, MapsActivity.class);
            intent.putExtra(MapsActivity.EATERIES_EXTRA, eateries);
            intent.putExtra(MapsActivity.SELECTED_EATERY_EXTRA, selectedEateryID);
            startActivity(intent);
        }
    }

    /**
     * make get account request using db interface
     *
     * @param email
     */
    private void getAccountFromDb(String email){
        dbrequest = GET_ACCOUNT_REQUEST;
        dbInterface.getAccount(email);
    }

    /**
     * make get account balance request using db interface
     *
     * @param id
     */
    private void getAccountBalanceFromDb(Integer id){
        dbrequest = GET_ACCOUNT_BALANCE_REQUEST;
        dbInterface.getBalance(id);
    }

    /**
     * get list of favorite eateries from db
     *
     * @param id
     */
    private void getFavoritesFromDb(Integer id){
        dbrequest = GET_FAVORITES_REQUEST;
        dbInterface.getFavoriteEateries(id);
    }

    public void addToFavorites(String id, String name){
        if( favorites == null ){
            favorites = new HashMap<>();
        }
        if( !isInFavorites(id) ){
            favorites.put(id, name);
        }
    }

    public Boolean isInFavorites(String id){
        return favorites.containsKey(id);
    }

    //LOCATION permission relate function ↓
    private boolean checkPermission(){
        return (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }
    //location permission relate function ↑


    //LOGIN interface ↓
    @Override
    public void loginClick() {
        signIn();
    }

    @Override
    public void signOutClick() {
        mGoogleSignInClient.signOut();
        signedIn = false;
        invalidateOptionsMenu();
        loadLoginFragment();
        Toast.makeText(MainActivity.this, "Goodbye!", Toast.LENGTH_SHORT).show();
    }
    //login interface ↑

    //EATERIES LIST InfoList interface ↓
    @Override
    public void resSelected(String name, String lat, String lon) {
        MenuFragment menuFragment = MenuFragment.newInstance(name, lat,lon);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container1, menuFragment)
                .addToBackStack("list to detail")
                .commit();

        //Toast.makeText(this, "Loading information", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void eateriesFound(HashMap<String, Eatery> eateries) {
        this.eateries = eateries;
    }

    @Override
    public void viewInMapClick(String ID) {
        selectedEateryID = ID;
        startMapActivity();
    }

    @Override
    public void favoriteClick(String ID, String name) {
        //add the restaurant id as favorite in database
        Log.d("favorite", ID);
        dbrequest = ADD_FAVORITE_REQUEST;
        dbInterface.addFavorite(ID, name, userAccount.getId());
    }

    @Override
    public void checkInClick(String ID, String name, String lat, String lon) {
        Log.d("checkIn", ID + " " + name + " " + lat + " " + lon);
        if(checkPermission()) {
            //refresh location to get current location
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 500.0f, locationListener);
            Location currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location restaurantLocation = new Location("ResLocation");
            restaurantLocation.setLatitude(Double.valueOf(lat));
            restaurantLocation.setLongitude(Double.valueOf(lon));
            //if currentLocation to restaurantLocaiton is within 20 meter
            if(currentLocation.distanceTo(restaurantLocation) < 20)
            {
                //set restaurant checkin status in databse
                dbrequest = CHECKIN_REQUEST;
                dbInterface.checkIn(ID, name, userAccount.getId());
            }else{
//                Log.d("currentLocation", "test location" + currentLocation.getLatitude() + " " + currentLocation.getLongitude());
                Toast.makeText(this, "You are not in the restaurant", Toast.LENGTH_SHORT).show();
            }
        }else{//if the app can not access to location service request the service
            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 111);
            Toast.makeText(this, "This function needs to access to location service", Toast.LENGTH_LONG).show();
        }
    }
    //InfoList interface ↑

    //ACCOUNT BALANCE update interface
    /**
     * make set account balance request
     *
     * @param ab
     */
    @Override
    public void updateAccountBalance(AccountBalance ab){
        dbrequest = SET_ACCOUNT_BALANCE_REQUEST;
        dbInterface.setBalance(ab.getAccountId(), ab.getBalance());
    }

    //BALANCE fragment interface
    @Override
    public void addClick(Double amount) {
        accountBalance.deposit(amount);
    }

    @Override
    public void subtractClick(Double amount) {
        accountBalance.withdraw(amount);
    }
    //END Balance interface

    //DATABASE Interface
    @Override
    public void response(JSONArray data) {
        if (dbrequest.equals(GET_ACCOUNT_REQUEST)) {
            //Add code for condition
            try {
                userAccount = new Account(data.getJSONObject(0));
                //Toast.makeText(MainActivity.this, "User account id: " + userAccount.getId(), Toast.LENGTH_SHORT).show();
                getFavoritesFromDb(userAccount.getId());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }else if (dbrequest.equals(GET_ACCOUNT_BALANCE_REQUEST)){
            try {
                accountBalance = new AccountBalance(data.getJSONObject(0), this);
                //Toast.makeText(MainActivity.this, "User account balance: " + accountBalance.getBalance(), Toast.LENGTH_SHORT).show();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else if (dbrequest.equals(SET_ACCOUNT_BALANCE_REQUEST)){
            try {
                accountBalance = new AccountBalance(data.getJSONObject(0), this);
                balanceFragment.balanceUpdated(accountBalance.getBalance());
                //Toast.makeText(MainActivity.this, "User account balance: " + accountBalance.getBalance(), Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else if (dbrequest.equals(CHECKIN_REQUEST)){
            try {
                Toast.makeText(MainActivity.this, "Checked in to " + data.getJSONObject(0).getString("store_name"), Toast.LENGTH_SHORT).show();
            } catch (JSONException e){
                e.printStackTrace();
            }

        }else if (dbrequest.equals(ADD_FAVORITE_REQUEST)){
            try {
                Toast.makeText(MainActivity.this, "Added " + data.getJSONObject(0).getString("store_name") + " to favorites", Toast.LENGTH_SHORT).show();
                try {
                    JSONObject store = data.getJSONObject(0);
                    addToFavorites(store.getString("google_id"), store.getString("store_name"));
                }catch(JSONException e){
                    e.printStackTrace();
                }
            } catch (JSONException e){
                e.printStackTrace();
            }

        }else if(dbrequest.equals(GET_FAVORITES_REQUEST)){
            try {
                for (int i = 0; i < data.length(); i++) {
                    JSONObject store = data.getJSONObject(i);
                    addToFavorites(store.getString("google_id"), store.getString("store_name"));
                }
                getAccountBalanceFromDb(userAccount.getId());   //get account balance for user
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void errorResponse(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
        if (dbrequest.equals(GET_ACCOUNT_REQUEST)) {
            //Add code for condition if needed
        }else if (dbrequest.equals(GET_ACCOUNT_BALANCE_REQUEST)){
            //Add code for condition if needed
        }else if (dbrequest.equals(SET_ACCOUNT_BALANCE_REQUEST)){

        }
    }
}