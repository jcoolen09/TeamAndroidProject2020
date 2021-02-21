package edu.temple.foodie;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AccountBalance {

    private int id;
    private int accountId;
    private double balance;
    private UpdateDatabaseAccountBalance dbhelper;

    public AccountBalance(JSONObject args, Context context) throws JSONException {
        this.id = args.getInt("balance_id");
        this.accountId = args.getInt("account_id");
        this.balance = args.getDouble("balance");
        this.dbhelper = (UpdateDatabaseAccountBalance) context;
    }

    private AccountBalance(double b) {
        balance = b;
    }

    //Deposit method will add the amount to the current balance. On successful completion the method will return true.
    //After a change is made to balance, it is updated in the database.
    public boolean deposit(double amount) {

        balance = balance + amount;
        updateBalance();
        return true;
    }

    //Withdraw method will subtract the amount from the balance if possible. On successful completion return true.
    //After a change is made to balance, it is updated in the database.
    public boolean withdraw(double amount) {
        balance = balance - amount;
        updateBalance();
        return true;
    }

    //Retrieve the most updated balance from the database.
    public double getBalance() {
        //balance = Database.getBal() <-- just prototype, this will change.

        return balance;
    }

    public int getId(){
        return id;
    }

    public int getAccountId(){
        return  accountId;
    }

    //Send the most updated balance to database.
    public boolean updateBalance() {

        //Database.balance = balance <-- just prototype, this will change.
        dbhelper.updateAccountBalance(this);

        return true;
    }

    //force activity that uses class to implement update db account balance function
    public interface UpdateDatabaseAccountBalance {
        //update balance using db interface
        void updateAccountBalance( AccountBalance ab );
    }
}
