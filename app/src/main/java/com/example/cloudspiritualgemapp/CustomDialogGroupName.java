
package com.example.cloudspiritualgemapp;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
public class CustomDialogGroupName extends Dialog {
    TextView groupCreateText, groupNameTag,groupDescriptionTag;
    EditText groupNameInput,groupDescriptionInput;
    Button groupSaveButton, groupCancelButton;
    FirebaseAuth auth;
    FirebaseDatabase database;

    Set<String> groupNamesList = new HashSet<>();
    Set<String> groupDescriptionList = new HashSet<>();
    private OnGroupSaveListener mListener;


    public CustomDialogGroupName(@NonNull Context context) {
        super(context, R.style.DialogStyle);
    }


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_entry);

        // Set dialog position
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes(layoutParams);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        // Customize your dialog layout
        groupCreateText = findViewById(R.id.group_create_text);
        groupNameInput = findViewById(R.id.group_name_input);
        groupDescriptionInput = findViewById(R.id.group_description_input);
        groupSaveButton = findViewById(R.id.group_save_button);
        groupCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        groupSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String groupNameVar = groupNameInput.getText().toString();
                String groupDescriptionVar = groupDescriptionInput.getText().toString();
                String newGroupName = groupNameInput.getText().toString();

                String notEmptyName ;
                if (!newGroupName.isEmpty()) {
                    groupNamesList.add(newGroupName);
                    notEmptyName = newGroupName;
                    // Optionally, update UI or perform any other actions needed
                } else {
                    Toast.makeText(getContext(), "Please enter a group name", Toast.LENGTH_SHORT).show();
                }
                if (!groupDescriptionVar.isEmpty()) {
                    groupDescriptionList.add(groupDescriptionVar);

                    // Optionally, update UI or perform any other actions needed
                } else {
                    Toast.makeText(getContext(), "Please enter a group description", Toast.LENGTH_SHORT).show();
                }
                FirebaseUser user = auth.getCurrentUser();
                if (user != null) {
                    // Iterate over the list of group names

                        String uid = UUID.randomUUID().toString();
                        HashMap<String, Object> group_map = new HashMap<>();
                        group_map.put("id", uid);
                        group_map.put("group_name", newGroupName);
                        group_map.put("group_description", groupDescriptionVar);

                        // Add the group under the user's ID
                        database.getReference()
                                .child("Group_data")
                                .child(user.getUid())
                                .child(uid)
                                .setValue(group_map)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            // Group added successfully
                                            if (mListener != null) {
                                                mListener.onGroupSaved(uid); // Pass the UID to the listener
                                            }
                                            groupNameInput.setText(null);
                                            groupDescriptionInput.setText(null);
                                            dismiss();
                                        } else {
                                            // Failed to add group
                                            Toast.makeText(getContext(), "Failed to add group: " + newGroupName, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                }

        });


    }
    public interface OnGroupSaveListener {
        void onGroupSaved(String uid);
    }
    public void setOnGroupSaveListener(OnGroupSaveListener listener) {
        mListener = listener;
    }

}