package edu.temple.foodie;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.function.DoubleUnaryOperator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BalanceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BalanceFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private BalanceInterface mListener;
    private Double balance;
    private TextView balanceView;
    private EditText balanceEdit;
    private Button addBtn;
    private Button subBtn;

    public BalanceFragment() {
        // Required empty public constructor
    }

    public static BalanceFragment newInstance(Double balance) {
        BalanceFragment fragment = new BalanceFragment();
        Bundle args = new Bundle();
        args.putDouble(ARG_PARAM1, balance);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof LoginFragment.loginInterface){
            mListener = (BalanceFragment.BalanceInterface)context;
        }else{
            throw new RuntimeException(context + "need to implement loginInterface");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            balance = getArguments().getDouble(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mainV = inflater.inflate(R.layout.fragment_balance, container, false);
        balanceView = mainV.findViewById(R.id.balanceView);
        balanceEdit = mainV.findViewById(R.id.balanceEditText);
        addBtn = mainV.findViewById(R.id.addButton);
        subBtn = mainV.findViewById(R.id.subtractButton);

        setBalance(balance);

        mainV.findViewById(R.id.addButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.addClick( Double.parseDouble(balanceEdit.getText().toString()) );
            }
        });

        mainV.findViewById(R.id.subtractButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.subtractClick( Double.parseDouble(balanceEdit.getText().toString()) );
            }
        });

        return mainV;
    }

    public void balanceUpdated(Double balance){
        setBalance(balance);
    }

    private void setBalance(Double amount){
        this.balance = amount;
        balanceView.setText("Current Balance: $" + String.valueOf(balance));
    }

    public interface BalanceInterface{
        void addClick(Double amount);
        void subtractClick(Double amount);
    }
}