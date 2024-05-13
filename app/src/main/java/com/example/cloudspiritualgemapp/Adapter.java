package com.example.cloudspiritualgemapp;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    LayoutInflater layoutInflater;
    Context context;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseUser user;
    String uid;
    ArrayList<ReadWriteDataDetails> arrGroups;
    // Reference to MainActivity

    public Adapter(Context context,     ArrayList<ReadWriteDataDetails> arrGroups) {
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.arrGroups = arrGroups; // Initialize MainActivity reference
        auth = FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        user = auth.getCurrentUser();
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.activity_cardview_group, parent, false);
        return new ViewHolder(view,this);
    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Check if the lists are empty or if the position is invalid
        ReadWriteDataDetails model = (ReadWriteDataDetails) arrGroups.get(position);
        if (model.gid != null){
            holder.groupName.setText(model.group_name);

            holder.groupDescription.setText(model.group_description);

            Log.e("Adapterinfo", model.gid + " " + model.group_name + " " + model.group_description);
            holder.row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (model.group_name != null) {
                        // Start NewActivity with the clicked item's name as an extra
                        Intent intent = new Intent(context, GroupDataScreen.class);
                        intent.putExtra("title", model.group_name);
                        intent.putExtra("gid",model.gid);
                        context.startActivity(intent);

                    } else {
                        Log.e("MainActivity", "Item is null");
                    }
                }
            });
            holder.updateImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.activity_group_entry);
                    TextView updateText = dialog.findViewById(R.id.group_create_text);
                    EditText editGroupName = dialog.findViewById(R.id.group_name_input);
                    EditText editGroupDescription = dialog.findViewById(R.id.group_description_input);
                    Button btnUpdate = dialog.findViewById(R.id.group_save_button);
                    updateText.setText(String.valueOf("Update Contact"));
                    btnUpdate.setText((String.valueOf("Update")));
                    editGroupName.setText((arrGroups.get(holder.getAdapterPosition())).group_name);
                    editGroupDescription.setText((arrGroups.get(holder.getAdapterPosition())).group_description);
                    if (btnUpdate != null) {

                        btnUpdate.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String grName = "", grDescription = "";
                                if (!editGroupName.getText().toString().equals("")) {
                                    grName = editGroupName.getText().toString();
                                } else {
                                    Toast.makeText(context, "Please Enter a Group Name", Toast.LENGTH_SHORT).show();
                                }
                                if (!editGroupDescription.getText().toString().equals("")) {
                                    grDescription = editGroupDescription.getText().toString();
                                } else {
                                    Toast.makeText(context, "Please Enter a Group description", Toast.LENGTH_SHORT).show();
                                }
                                arrGroups.set(holder.getAdapterPosition(), new ReadWriteDataDetails(grName,model.gid, grDescription));
                                notifyItemChanged(holder.getAdapterPosition());

                                if (user != null) {
                                    // Iterate over the list of group names
                                    ReadWriteDataDetails newData = new ReadWriteDataDetails(grName, model.gid, grDescription);

                                    // Add item to Firebase
                                    database.getReference()
                                            .child("Group_data")
                                            .child(user.getUid())
                                            .child(model.gid)
                                            .setValue(newData)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        // Item added successfully

                                                        dialog.dismiss();
                                                    } else {
                                                        // Failed to add item
                                                        Toast.makeText(context, "Failed to add group: " , Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }


                            }
                        });
                        dialog.show();
                    }
                }
            });
            holder.deleteImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete Group");
                builder.setMessage("Do you want to delete this group");
                builder.setIcon(R.drawable.baseline_delete_24);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Group_data");
                        DatabaseReference itemRef = databaseRef.child(user.getUid()).child(model.gid);
                        // Remove the data at the reference
                        itemRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                arrGroups.remove(model);
                                notifyItemRemoved(holder.getAdapterPosition());
                            }
                        });
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
                }
            });
        }
    }



    @Override
    public int getItemCount() {
        // Return the size of the group_name list to ensure consistency
        return arrGroups.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView groupName, groupDescription;
        ImageView deleteImage,updateImage;
       ConstraintLayout row;

        public ViewHolder(@NonNull View itemView, Adapter adapter) {
            super(itemView);
            groupName = itemView.findViewById(R.id.group_name);
            groupDescription = itemView.findViewById(R.id.group_description);
            deleteImage = itemView.findViewById(R.id.delete_image);
            updateImage = itemView.findViewById(R.id.update_image);
            row = itemView.findViewById(R.id.iiRow);


        }
    }
//
//    public interface OnItemClickListener {
//        void onItemClick(String item);
//    }
}
