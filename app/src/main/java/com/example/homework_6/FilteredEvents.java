package com.example.homework_6;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FilteredEvents extends AppCompatActivity implements AdapterView.OnItemSelectedListener
{
    Spinner spinnerFiltered;
    ListView listViewFiltered;
    private DatabaseReference athleteEventsReference;
    private String athleteKeyId;
    private String[] filters = new String[]{"By best time", "All"};
    private ArrayList<Event> filteredEvents = new ArrayList<>();
    private ArrayList<String> filteredEventsNames = new ArrayList<>();
    String passedInString;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtered_events);

        spinnerFiltered = findViewById(R.id.spinnerFiltered);
        listViewFiltered = findViewById(R.id.listViewFiltered);
        spinnerFiltered.setOnItemSelectedListener(this);


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, filters);
        spinnerFiltered.setAdapter(adapter);

        athleteKeyId = getIntent().getStringExtra("athleteKeyId");

        if(athleteKeyId != null)
        {
            athleteEventsReference = Firebase.referenceAthlete.child(athleteKeyId).child("Events");
            filterEvents();
        }

    }

    public void filterEvents()
    {
        Query query = athleteEventsReference.orderByChild("bestTime");

        query.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                filteredEventsNames.clear();
                filteredEvents.clear();
                for(DataSnapshot eventSnapshot : snapshot.getChildren())
                {
                    Event event = eventSnapshot.getValue(Event.class);
                    if(event != null)
                    {
                        filteredEvents.add(event);
                        String sportsBranch = event.getSportsBranch();
                        String date = event.getDate();
                        String averageTime = String.valueOf(event.getAverageTime());

                        if(event.isPassed())
                        {
                            passedInString = "passed";
                        }
                        else
                        {
                            passedInString = "Not passed";
                        }

                        String bestTime = String.valueOf(event.getBestTime());
                        filteredEventsNames.add(sportsBranch + ", happened in " + date + ", with the average time of " + averageTime + " seconds. The best time was " + bestTime + " seconds, and you " + passedInString);
                    }
                }

                ArrayAdapter<String> adapter = new ArrayAdapter(FilteredEvents.this, android.R.layout.simple_list_item_1, filteredEventsNames);
                listViewFiltered.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        if(position == 1)
        {
            Toast.makeText(this, "Filter set to all", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, EventsParticipating.class);
            intent.putExtra("athleteKeyId", athleteKeyId);
            startActivity(intent);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent)
    {

    }
}