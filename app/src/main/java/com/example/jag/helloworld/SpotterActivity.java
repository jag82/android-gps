package com.example.jag.helloworld;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;

public class SpotterActivity extends AppCompatActivity {

    GPSSpotter spotter;
    ListView listView;
    SpotListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotter);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        listView = findViewById(R.id.list);
        adapter = new SpotListAdapter(this, new ArrayList<Spot>());
        adapter.add(new Spot(1, 2, "sample"));
        listView.setAdapter(adapter);

        spotter = new GPSSpotter(this);

        final EditText name = findViewById(R.id.name);
        final Button add = findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSpot(name.getText().toString());
                add.clearFocus();
            }
        });

        updateAdapter();
    }

    private void addSpot(String name) {
        if(name == null || name.isEmpty()) {
            return;
        }

        //get lat,lng
        Spot spot = spotter.spot(name);
        if(spot == null) {
            Toast.makeText(this, "spotter error", Toast.LENGTH_SHORT);
        } else {
            SharedPreferences prefs = getSharedPreferences("spots", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(name, spot.toString());
            editor.commit();
            updateAdapter();

        }
    }

    private void updateAdapter() {

        adapter.clear();
        SharedPreferences prefs = getSharedPreferences("spots", Context.MODE_PRIVATE);
        Map<String, ?> map = prefs.getAll();
        for(Map.Entry<String,?> entry : map.entrySet()) {
            adapter.add(Spot.fromString(entry.getValue().toString()));
        }
    }

}
