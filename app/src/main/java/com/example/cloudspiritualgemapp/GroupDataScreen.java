package com.example.cloudspiritualgemapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupDataScreen extends AppCompatActivity {
    ImageView contactButton,contactButtonInvisible;
    TextView groupScreenName ,groupContactSelect;
    RecyclerView recyclerViewinGroups,recycleAddingMember;
    private boolean isAddingMembersLayoutVisible = false;

    ImageButton sendButton;
    TextView selectedCountTextView;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseUser user;
    Bundle saved;
    ProgressBar progressBar;
    SearchView searchAddingMember;
    StorageReference storageReference;
    AdapterDataInGroups adapterDataInGroups;
    String gid,savedGroupName,itemName;
    ReadWriteDataDetails readData;
    AdapterDatainmaingroupscreen adapterData;
    ArrayList<ReadWriteContactDetails> arrContacts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_datapresentation_screen);


        groupScreenName = findViewById(R.id.group_screen_name);
        groupContactSelect = findViewById(R.id.group_contact_select);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Spiritual Gems App");
        gid = getIntent().getStringExtra("gid");

        if (savedInstanceState != null) {
            // Restore the activity state if available
             savedGroupName = savedInstanceState.getString("groupName");
            // Restore other necessary data
            if (savedGroupName != null) {
                groupScreenName.setText(savedGroupName);

            }
            // You can restore other data as needed
        } else {
            // No saved state, initialize as usual
             itemName = getIntent().getStringExtra("title");
            groupScreenName.setText(itemName);

            // Load other initial data or perform tasks
        }
        auth = FirebaseAuth.getInstance();
        arrContacts = new ArrayList<>();
        database=FirebaseDatabase.getInstance();
        user = auth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("Display Pics");
//        searchAddingMember = findViewById(R.id.searchView);
//        searchAddingMember.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                // Called when the user submits the query (e.g., presses "Search" button on keyboard)
//
//                return false; // Return true to indicate that the query has been handled
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                // Called when the query text changes (each keystroke)
//                // You can perform filtering or other operations here
//                performSearch(newText);
//                return true; // Return false to indicate that the listener did not handle the text change
//            }
//        });
        contactButton = findViewById(R.id.contact_details);
        contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.activity_addingmembers);
                switchToAddingMembersLayout();

                // Initialize RecyclerView and SearchView after setContentView
                recycleAddingMember = findViewById(R.id.adding_members_name_list);
                searchAddingMember = findViewById(R.id.searchview_adding_member);
                 sendButton = findViewById(R.id.sendButton);
                 selectedCountTextView = findViewById(R.id.selectedCountTextView);
                // Check if views are properly initialized
                if (recycleAddingMember == null || searchAddingMember == null) {
                    Log.e("GroupDataScreen", "Error: RecyclerView or SearchView is null");
                    return;
                }

                // Set up RecyclerView adapter and layout manager
                adapterDataInGroups = new AdapterDataInGroups(GroupDataScreen.this, arrContacts,sendButton,selectedCountTextView,gid);
                recycleAddingMember.setLayoutManager(new LinearLayoutManager(GroupDataScreen.this));
                recycleAddingMember.setAdapter(adapterDataInGroups);

                // Set up SearchView listener
                searchAddingMember.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        // Called when the user submits the query
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        // Called when the query text changes
                        performSearch(newText);
                        return true;
                    }
                });

                // Load data into RecyclerView
                loadData();
            }
        });
        contactButtonInvisible = findViewById(R.id.contact_details_invisible);
        loadGroupData();
        saved = savedInstanceState;

    }


    protected void loadGroupData() {

        StringBuilder groupDescriptionBuilder = new StringBuilder();
        if (user != null) { // Check if the user is logged in and it's the first start
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Group_data")
                    .child(user.getUid())
                    .child(gid)
                    .child("added_members");


            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    arrContacts.clear();
                    for (DataSnapshot snapShot : snapshot.getChildren()) {
                        ReadWriteContactDetails dataDetails = snapShot.getValue(ReadWriteContactDetails.class);
                        if (dataDetails != null) {
                            arrContacts.add(dataDetails);
                            if (groupDescriptionBuilder.length() > 0) {
                                groupDescriptionBuilder.append(", ");
                            }
                            groupDescriptionBuilder.append(dataDetails.name);
//                            Toast.makeText(GroupDataScreen.this, "load from load " , Toast.LENGTH_SHORT).show();

                        }
                    }
                    adapterData.notifyDataSetChanged();
                    updateGroupDescription(groupDescriptionBuilder.toString());

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("MainActivity", "onCancelled: " + error.getMessage());
                }
            });



            recyclerViewinGroups = findViewById(R.id.group_name_list);
            adapterData = new AdapterDatainmaingroupscreen(GroupDataScreen.this, arrContacts,gid);
            recyclerViewinGroups.setLayoutManager(new LinearLayoutManager(this));
            recyclerViewinGroups.setAdapter(adapterData);
        }
    }

    protected void loadData() {

        if (user != null) { // Check if the user is logged in and it's the first start
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("Created Contacts");

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    arrContacts.clear();
                    for (DataSnapshot snapShot : snapshot.getChildren()) {
                        ReadWriteContactDetails dataDetails = snapShot.getValue(ReadWriteContactDetails.class);
                        if (dataDetails != null) {
                            arrContacts.add(dataDetails);
//                            Toast.makeText(GroupDataScreen.this, "load from load " , Toast.LENGTH_SHORT).show();

                        }
                    }
                    adapterDataInGroups.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("MainActivity", "onCancelled: " + error.getMessage());
                }
            });
            recycleAddingMember = findViewById(R.id.adding_members_name_list);
            adapterDataInGroups = new AdapterDataInGroups(GroupDataScreen.this, arrContacts,sendButton,selectedCountTextView,gid);
            recycleAddingMember.setLayoutManager(new LinearLayoutManager(this));
            recycleAddingMember.setAdapter(adapterDataInGroups);
        }
    }
    private void updateGroupDescription(String description) {
        if (user != null && gid != null) {
            DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference()
                    .child("Group_data")
                    .child(user.getUid())
                    .child(gid);

            groupRef.child("group_description").setValue(description)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.e("Update", "Group description updated successfully: " + description);
                            } else {
                                Log.e("Update", "Failed to update group description");
                            }
                        }
                    });
        }
    }
    private void switchToAddingMembersLayout() {
        setContentView(R.layout.activity_addingmembers);
        // Initialize views for the second layout
        isAddingMembersLayoutVisible = true;

        // Set up RecyclerView, SearchView, and data loading as needed
    }

    @Override
    public void onBackPressed() {
        if (isAddingMembersLayoutVisible) {
            // If currently in the second layout, go back to the first layout
            isAddingMembersLayoutVisible = false;
            setContentView(R.layout.activity_group_datapresentation_screen);
            recyclerViewinGroups = findViewById(R.id.group_name_list);
            adapterData = new AdapterDatainmaingroupscreen(GroupDataScreen.this, arrContacts,gid);
            recyclerViewinGroups.setLayoutManager(new LinearLayoutManager(this));
            recyclerViewinGroups.setAdapter(adapterData);
            groupScreenName = findViewById(R.id.group_screen_name);
            contactButton = findViewById(R.id.contact_details);
            itemName = getIntent().getStringExtra("title");
            groupScreenName.setText(itemName);

        } else {

            loadGroupData();
            Intent intent = new Intent(GroupDataScreen.this, MainActivity.class);
            startActivity(intent);
        }
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save necessary data or state information
        outState.putString("groupName", groupScreenName.getText().toString());
        // Save other data as needed
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore saved state if needed
    }
    private void performSearch(String query) {
        ArrayList<ReadWriteContactDetails> filteredList = new ArrayList<>();
        for (ReadWriteContactDetails contact : arrContacts) {
            // Check if the contact name contains the search query (case-insensitive)
            if (contact.name.toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(contact);
            }
        }
        // Update the adapter with the filtered list
        if(filteredList.isEmpty()){
            Toast.makeText(GroupDataScreen.this, "No data found", Toast.LENGTH_SHORT).show();
            adapterDataInGroups.filterList(filteredList);
        }
        else {
            adapterDataInGroups.filterList(filteredList);
        }
    }

}
