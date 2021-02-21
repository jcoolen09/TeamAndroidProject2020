package edu.temple.foodie;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class LoginFragment extends Fragment {


    private View mainV;

    private loginInterface mListener;

    private TextView t1,t2, balanceView;
    private Button loginBtn, logoutBtn;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM_SIGNED_IN = "signed_in";


    private String mParam1;
    private String mParam2;

    private boolean signed = false;

    GoogleSignInClient mGoogleSignInClient;


    public LoginFragment() {
        // Required empty public constructor
    }


    public static LoginFragment newInstance(Boolean signed){
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_PARAM_SIGNED_IN, signed);
        fragment.setArguments(args);
        return fragment;
    }

    /*
    public static loginFragment newInstance(String param1, String param2) {
        loginFragment fragment = new loginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    */

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof loginInterface){
            mListener = (loginInterface)context;
        }else{
            throw new RuntimeException(context + "need to implement loginInterface");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            signed = getArguments().getBoolean(ARG_PARAM_SIGNED_IN);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainV = inflater.inflate(R.layout.fragment_login, container, false);

        t1 = mainV.findViewById(R.id.textView);
        t2 = mainV.findViewById(R.id.textView2);
        balanceView = mainV.findViewById(R.id.textView3);
        loginBtn = mainV.findViewById(R.id.addButton);
        logoutBtn = mainV.findViewById(R.id.signoutButton);

        if(signed){
            getEmailInfo();
        }else{
            loggedOut();
        }

        mainV.findViewById(R.id.addButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!signed) {
                    mListener.loginClick();
                }
            }
        });

        mainV.findViewById(R.id.signoutButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(signed){
                    mListener.signOutClick();
                }
            }
        });

        return mainV;
    }

    private void getEmailInfo() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);

        GoogleSignInAccount acc = GoogleSignIn.getLastSignedInAccount(getContext());
        if(acc != null){
            loggedIn(acc);
        }
    }

    private void setName(String name){
        if( t1 != null ){
            t1.setText(name);
        }
    }

    private void setEmail(String email){
        if( t2 != null ){
            t2.setText(email);
        }
    }

    /**
     * let the fragment know that user logged in
     *
     * @param account
     */
    public void loggedIn( GoogleSignInAccount account ){
        this.signed = true;
        setName("Welcome " + account.getDisplayName() + "!");
        setEmail(account.getEmail());
        if( loginBtn != null ){
            loginBtn.setVisibility(View.INVISIBLE);
            balanceView.setVisibility((View.VISIBLE));
        }
        if( logoutBtn != null ){
            logoutBtn.setVisibility(View.VISIBLE);
        }
    }

    /**
     * let the fragment know that user logged out
     */
    public void loggedOut(){
        this.signed = false;
        setName("");
        setEmail("");
        if( loginBtn != null ){
            loginBtn.setVisibility(View.VISIBLE);
            balanceView.setVisibility((View.INVISIBLE));
        }
        if( logoutBtn != null ){
            logoutBtn.setVisibility(View.INVISIBLE);
        }
    }




    public interface loginInterface{
        void loginClick();
        void signOutClick();
    }
}