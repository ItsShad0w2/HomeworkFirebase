    package com.example.homework_6;


    import android.content.Intent;
    import android.os.Build;
    import android.os.Bundle;
    import android.view.View;
    import android.widget.AdapterView;
    import android.widget.ArrayAdapter;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.ListView;
    import android.widget.Spinner;
    import android.widget.Toast;

    import androidx.activity.EdgeToEdge;
    import androidx.annotation.NonNull;
    import androidx.annotation.RequiresApi;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.core.graphics.Insets;
    import androidx.core.view.ViewCompat;
    import androidx.core.view.WindowInsetsCompat;

    import com.google.firebase.database.DataSnapshot;
    import com.google.firebase.database.DatabaseError;
    import com.google.firebase.database.DatabaseReference;
    import com.google.firebase.database.ValueEventListener;

    import java.time.DateTimeException;
    import java.time.LocalDate;
    import java.util.ArrayList;

    public class EventsParticipating extends AppCompatActivity implements AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener
    {
        EditText editTextSportsBranch, editTextDate, editTextAverageTime, editTextPassed, editTextBestTime;
        Button buttonAddEvent, buttonUpdateEvent, buttonRemoveEvent;
        ListView listViewEvents;
        Spinner spinnerFilter;
        boolean passed;
        String sportsBranch;
        String date;
        String averageTimeInString;
        String bestTimeInString;
        String passedInString;
        private String athleteKeyId;
        private Event selectedEvent;
        private String eventKeyId;
        private DatabaseReference athleteEventsReference;
        private String[] filters = new String[]{"All", "By best time"};



        ArrayList<Event> events = new ArrayList<>();

        @Override
        protected void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_events_participating);

            editTextSportsBranch = findViewById(R.id.editTextSportsBranch);
            editTextAverageTime = findViewById(R.id.editTextAverageTime);
            editTextDate = findViewById(R.id.editTextDate);
            editTextPassed = findViewById(R.id.editTextPassed);
            editTextBestTime = findViewById(R.id.editTextBestTime);
            buttonAddEvent = findViewById(R.id.buttonAddEvent);
            buttonUpdateEvent = findViewById(R.id.buttonUpdate);
            buttonRemoveEvent = findViewById(R.id.buttonRemove);
            listViewEvents = findViewById(R.id.listViewEvents);
            spinnerFilter = findViewById(R.id.spinnerFilter);

            athleteKeyId = getIntent().getStringExtra("athleteKeyId");
            if(athleteKeyId != null)
            {
                athleteEventsReference = Firebase.referenceAthlete.child(athleteKeyId).child("Events");
            }

            ArrayList<String> eventNames = new ArrayList<>();
            spinnerFilter.setOnItemSelectedListener(this);
            listViewEvents.setOnItemClickListener(this);
            buttonAddEvent.setEnabled(true);
            buttonUpdateEvent.setEnabled(false);
            buttonRemoveEvent.setEnabled(false);

            buttonAddEvent.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    addEvent();
                }
            });

            buttonUpdateEvent.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    updateEvent();
                }
            });

            buttonRemoveEvent.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    removeEvent();
                    buttonRemoveEvent.setEnabled(false);
                    buttonAddEvent.setEnabled(true);
                    buttonUpdateEvent.setEnabled(false);
                }
            });

            athleteEventsReference.addValueEventListener(new ValueEventListener()
            {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                    eventNames.clear();
                    events.clear();
                    for(DataSnapshot eventSnapshot : snapshot.getChildren())
                    {
                        Event event = eventSnapshot.getValue(Event.class);
                        if(event != null)
                        {
                            events.add(event);
                            String sportsBranch = event.getSportsBranch();
                            String date = event.getDate();
                            String averageTime = String.valueOf(event.getAverageTime());
                            if(event.isPassed())
                            {
                                passedInString = "passed";
                            }
                            else
                            {
                                passedInString = "not passed";
                            }
                            String bestTime = String.valueOf(event.getBestTime());

                            eventNames.add(sportsBranch + ", happened in " + date + ", with the average time of " + averageTime + " seconds. The best time was " + bestTime + " seconds, and you " + passedInString);
                        }
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(EventsParticipating.this, android.R.layout.simple_list_item_1, eventNames);
                    listViewEvents.setAdapter(adapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error)
                {

                }
            });

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, filters);
            spinnerFilter.setAdapter(adapter);

        }

        public void addEvent()
        {
            sportsBranch = editTextSportsBranch.getText().toString();
            date = editTextDate.getText().toString();
            averageTimeInString = editTextAverageTime.getText().toString();
            passedInString = editTextPassed.getText().toString();
            bestTimeInString = editTextBestTime.getText().toString();

            if(sportsBranch.isEmpty() || date.isEmpty() || passedInString.isEmpty() || averageTimeInString.isEmpty() || bestTimeInString.isEmpty())
            {
                Toast.makeText(this, "Please make sure all of the fields are filled", Toast.LENGTH_SHORT).show();
                return;
            }

            if(!checkForBranch(sportsBranch))
            {
                Toast.makeText(this, "Please make sure that the sports branch field doesn't contain special characters", Toast.LENGTH_SHORT).show();
                return;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            {
                if(!checkForDate(date))
                {
                    Toast.makeText(this, "Please make sure to check the date field", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            if(passedInString.equals("yes") || passedInString.equals("Yes"))
            {
                passed = true;
            }
            else
            {
                if(!passedInString.equals("No") && !passedInString.equals("no"))
                {
                    Toast.makeText(this, "Please make sure to check the passing field", Toast.LENGTH_SHORT).show();
                    return;
                }

                passed = false;
            }

            double averageTime = Double.parseDouble(averageTimeInString);
            int bestTime = Integer.parseInt(bestTimeInString);

            if(bestTime < 0)
            {
                Toast.makeText(this, "Please make sure to check the best time field", Toast.LENGTH_SHORT).show();
                return;
            }

            Event event = new Event(sportsBranch, averageTime, date, passed, bestTime);
            events.add(event);
            String eventId = athleteEventsReference.push().getKey();
            if(eventId != null)
            {
                event.setKeyId(eventId);
                athleteEventsReference.child(eventId).setValue(event);
                Toast.makeText(this, "You have added an event", Toast.LENGTH_SHORT).show();
                clearFields();
            }
        }

        public void updateEvent()
        {
            sportsBranch = editTextSportsBranch.getText().toString();
            date = editTextDate.getText().toString();
            averageTimeInString = editTextAverageTime.getText().toString();
            passedInString = editTextPassed.getText().toString();
            bestTimeInString = editTextBestTime.getText().toString();

            if(sportsBranch.isEmpty() || date.isEmpty() || passedInString.isEmpty() || averageTimeInString.isEmpty() || bestTimeInString.isEmpty())
            {
                Toast.makeText(this, "Please make sure all of the fields are filled", Toast.LENGTH_SHORT).show();
                return;
            }

            if(!checkForBranch(sportsBranch))
            {
                Toast.makeText(this, "Please make sure that the sports branch field doesn't contain special characters", Toast.LENGTH_SHORT).show();
                return;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            {
                if(!checkForDate(date))
                {

                    Toast.makeText(this, "Please make sure to check the date field", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            boolean isPassed;

            if(passedInString.equals("yes") || passedInString.equals("Yes"))
            {
                isPassed = true;
            }
            else
            {
                if(!passedInString.equals("no") && !passedInString.equals("No"))
                {
                    Toast.makeText(this, "Please make sure to check the passing field", Toast.LENGTH_SHORT).show();
                    return;
                }

                isPassed = false;
            }

            double averageTime = Double.parseDouble(averageTimeInString);
            int bestTime = Integer.parseInt(bestTimeInString);

            if(bestTime < 1)
            {
                Toast.makeText(this, "Please make sure to check the best time field", Toast.LENGTH_SHORT).show();
                return;
            }

            Event event = new Event(sportsBranch, averageTime, date, isPassed, bestTime);
            event.setKeyId(eventKeyId);
            athleteEventsReference.child(eventKeyId).setValue(event);
            Toast.makeText(this, "You have updated an event", Toast.LENGTH_SHORT).show();
            buttonAddEvent.setEnabled(true);
            buttonUpdateEvent.setEnabled(false);
            buttonRemoveEvent.setEnabled(false);
            clearFields();
        }

        public void removeEvent()
        {
            athleteEventsReference.child(eventKeyId).removeValue();
            Toast.makeText(this, "You have removed an event", Toast.LENGTH_SHORT).show();
            clearFields();
        }

        public void clearFields()
        {
            editTextSportsBranch.setText("");
            editTextDate.setText("");
            editTextAverageTime.setText("");
            editTextPassed.setText("");
            editTextBestTime.setText("");
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        public boolean checkForDate(String date)
        {
            try
            {
                LocalDate.parse(date);
                return true;
            }
            catch (DateTimeException e)
            {
                return false;
            }
        }

        public boolean checkForBranch(String sportsBranch)
        {
            for(int i = 0; i < sportsBranch.length(); i++)
            {
                if((sportsBranch.charAt(i) < 65 || sportsBranch.charAt(i) > 90) && (sportsBranch.charAt(i) < 97 || sportsBranch.charAt(i) > 127))
                {
                    return false;
                }
            }

            return true;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
        {
            if(position == 1)
            {
                Toast.makeText(this, "Filter set to by best time", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, FilteredEvents.class);
                intent.putExtra("athleteKeyId", athleteKeyId);
                startActivity(intent);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent)
        {

        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            selectedEvent = events.get(position);
            eventKeyId = selectedEvent.getKeyId();
            editTextSportsBranch.setText(selectedEvent.getSportsBranch());
            editTextAverageTime.setText(String.valueOf(selectedEvent.getAverageTime()));
            editTextDate.setText(selectedEvent.getDate());
            if(selectedEvent.isPassed())
            {
                editTextPassed.setText("yes");
            }
            else
            {
                editTextPassed.setText("no");
            }
            editTextBestTime.setText(String.valueOf(selectedEvent.getBestTime()));
            buttonAddEvent.setEnabled(false);
            buttonUpdateEvent.setEnabled(true);
            buttonRemoveEvent.setEnabled(true);
        }
    }