package edu.temple.foodie;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;

public class EateryListAdapter extends RecyclerView.Adapter<EateryListAdapter.EateryViewHolder> {
    private static final String TAG = "EateryListAdapter ===>>>";

    public ArrayList<Eatery> eateryList;
    private InfoListFragment.InfoListInterface mListener;

    public static class EateryViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public ImageButton favoriteButton;
        public ImageButton mapButton;
        public Button CheckInButton;


        public EateryViewHolder(@NonNull View eateryView) {
            super(eateryView);
            title = (TextView) eateryView.findViewById(R.id.restInfoText);
            favoriteButton = (ImageButton) eateryView.findViewById(R.id.favoriteButton);
            CheckInButton = (Button) eateryView.findViewById(R.id.CheckInbutton);
            mapButton = (ImageButton) eateryView.findViewById(R.id.mapButton);
        }
    }

    public EateryListAdapter(Context context, ArrayList<Eatery> eateryList) {
        this.eateryList = eateryList;
        this.mListener = (InfoListFragment.InfoListInterface) context;
    }

    @NonNull
    @Override
    public EateryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_results, parent, false);
        EateryViewHolder ivh = new EateryViewHolder(v);
        return ivh;
    }

    @Override
    public void onBindViewHolder(@NonNull EateryViewHolder holder, int position) {
        final Eatery currentEatery = eateryList.get(position);

        if( currentEatery != null ) {
            holder.favoriteButton.setVisibility(View.INVISIBLE);
            holder.title.setText(currentEatery.getName() + "\n" + currentEatery.getStatus());

            holder.favoriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.favoriteClick(currentEatery.getId(),
                            currentEatery.getName()
                    );
                }
            });

            //when clicked show map with eatery selected
            holder.mapButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.viewInMapClick(currentEatery.getId());
                }
            });

            //when checkIn button is click pass the place_id, latitude and longitude to main activity
            holder.CheckInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.checkInClick(currentEatery.getId(),
                            currentEatery.getName(),
                            String.valueOf(currentEatery.getCoordinates().latitude),
                            String.valueOf(currentEatery.getCoordinates().longitude)
                    );
                }
            });

            //when the textview is clicked pass the information to Main Activity to create a menufragment
            holder.title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.resSelected(currentEatery.getName(),
                            String.valueOf(currentEatery.getCoordinates().latitude),
                            String.valueOf(currentEatery.getCoordinates().longitude)
                    );
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return eateryList.size();
    }
}
