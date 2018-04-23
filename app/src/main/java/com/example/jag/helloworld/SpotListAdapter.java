package com.example.jag.helloworld;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SpotListAdapter extends ArrayAdapter<Spot> {

    List<Spot> spots;
    View v;
    Context ctx;

    public SpotListAdapter(Context ctx, ArrayList<Spot> spots) {
        super(ctx, R.layout.list_spot, spots);
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable final View convertView, @NonNull ViewGroup parent) {
        this.v = convertView;
        if(v==null) {
            LayoutInflater vi = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v=vi.inflate(R.layout.list_spot, null);
        }

        final Spot spot = getItem(position);

        TextView name = v.findViewById(R.id.name);
        name.setText(spot.name);

        TextView latLng = v.findViewById(R.id.latLng);
        latLng.setText(spot.lat + ", " + spot.lng);

        Button remove = v.findViewById(R.id.remove);


        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeSpot(spot.name);
            }
        });

        return v;
    }

    private void removeSpot(String name) {
        SharedPreferences prefs = ctx.getSharedPreferences("spots", Context.MODE_PRIVATE);
        Spot spot = Spot.fromString(prefs.getString(name, null));
        remove(spot);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(name);
        editor.commit();
        remove(spot);
    }


}
