package edu.temple.foodie;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Account {

    private int id;
    private String email;


    public Account(JSONObject args) throws JSONException {
        this.id = args.getInt("account_id");
        this.email = args.getString("email");
    }

    public int getId(){ return id; }

    public String getEmail(){ return email; }
}