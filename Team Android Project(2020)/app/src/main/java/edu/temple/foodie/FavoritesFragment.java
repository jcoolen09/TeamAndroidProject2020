package edu.temple.foodie;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.toolbox.Volley;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FavoritesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavoritesFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private InfoListFragment.InfoListInterface mListener;

    private ArrayList<Eatery> favorites;
    private HashMap<String, Eatery> eateries;

    private RecyclerView listView;
    private EateryListAdapter adapter;

    public FavoritesFragment() {
        // Required empty public constructor
    }

    public static FavoritesFragment newInstance(ArrayList<Eatery> favorites) {
        FavoritesFragment fragment = new FavoritesFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_PARAM1, favorites);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof InfoListFragment.InfoListInterface){
            mListener = (InfoListFragment.InfoListInterface) context;
        }else{
            throw new RuntimeException(context + "need to implement InfoListInterface");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            favorites = getArguments().getParcelableArrayList(ARG_PARAM1);
            Log.d("Favorites frag", "size " + favorites.size());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mainV = inflater.inflate(R.layout.fragment_favorites, container, false);

        listView = mainV.findViewById(R.id.eateryList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        adapter = new EateryListAdapter(getContext(), favorites);
        listView.setLayoutManager(layoutManager);
        listView.setAdapter(adapter);

        return mainV;
    }
}