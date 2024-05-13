package com.example.cloudspiritualgemapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity  {

    TextView screenDescription,deleteTextGroup;
    ImageView contactButton,contactButtonInvisible;
    Button groupAddButton;
    private boolean isFirstStart = true; // Flag to track first start
    RecyclerView recyclerView;
    Adapter adapter;
    FirebaseAuth auth;
    FirebaseDatabase database;
    ProgressBar progressBar;

    String pId;
    ArrayList<ReadWriteDataDetails> arrGroups;
    List<String> groupName,groupDescription,gid;
    List<String> finalList,finalDescriptionList,finalIdList;
    CustomDialogGroupName groupEntryCustomDialog;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        screenDescription = findViewById(R.id.screen_desciption);
        groupAddButton = findViewById(R.id.group_add_button);
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        finalList =new ArrayList<>();
        database=FirebaseDatabase.getInstance();
        groupName = new ArrayList<>();
        groupDescription = new ArrayList<>();
        gid = new ArrayList<>();
        finalIdList = new ArrayList<>();
        finalDescriptionList = new ArrayList<>();
        progressBar = findViewById(R.id.progress_bar);

        arrGroups = new ArrayList<ReadWriteDataDetails>();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Spiritual Gems"); // Set the title here
        }
        contactButton = findViewById(R.id.contact_details);
        contactButtonInvisible = findViewById(R.id.contact_details_invisible);
        groupEntryCustomDialog = new CustomDialogGroupName(MainActivity.this);
        groupAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.activity_group_entry);
                EditText editGroupName = dialog.findViewById(R.id.group_name_input);
                EditText editGroupDescription = dialog.findViewById(R.id.group_description_input);
                Button btnSave = dialog.findViewById(R.id.group_save_button);

                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String grName = editGroupName.getText().toString().trim();
                        String grDescription = editGroupDescription.getText().toString().trim();

                        if (grName.isEmpty()) {
                            Toast.makeText(MainActivity.this, "Please enter a group name", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            String uid = UUID.randomUUID().toString();
                            ReadWriteDataDetails newData = new ReadWriteDataDetails(grName, uid, grDescription);

                            // Add item to Firebase
                            database.getReference()
                                    .child("Group_data")
                                    .child(user.getUid())
                                    .child(uid)
                                    .setValue(newData)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                dialog.dismiss();
                                            } else {
                                                // Failed to add item
                                                Toast.makeText(MainActivity.this, "Failed to add group: " + grName, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }
                });

                dialog.show();
            }
        });

        contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,contactClickedScreen.class);
                startActivity(intent);
            }
        });
    loadData();
    }
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Exit App");
        builder.setMessage("Do you want to close the app?");
        builder.setIcon(R.drawable.baseline_delete_24);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Implement your contact deletion logic here
                // For example, call a method to delete the contact
                // deleteContact();

                // If you don't have a deletion logic method yet, you can simply call finish() to close the activity
                finishAffinity();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked No, do nothing or handle as needed
            }
        });
        builder.show();
    }



    protected void loadData() {

        if (firebaseUser != null && isFirstStart) { // Check if the user is logged in and it's the first start
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Group_data").child(firebaseUser.getUid());

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    arrGroups.clear();
                    for (DataSnapshot snapShot : snapshot.getChildren()) {
                        ReadWriteDataDetails dataDetails = snapShot.getValue(ReadWriteDataDetails.class);
                        if (dataDetails != null) {
                            arrGroups.add(dataDetails);
                            screenDescription.setVisibility(View.INVISIBLE);
                            Toast.makeText(MainActivity.this, "load from load " , Toast.LENGTH_SHORT).show();

                        }
                    }
                    progressBar.setVisibility(View.INVISIBLE);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("MainActivity", "onCancelled: " + error.getMessage());
                }
            });

            isFirstStart = false; // Update flag after first start
            recyclerView = findViewById(R.id.group_name_list);
            adapter = new Adapter(this, arrGroups);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);
        }
    }



}