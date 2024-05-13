
package com.example.cloudspiritualgemapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

public class AdapterDataInGroups extends RecyclerView.Adapter<AdapterDataInGroups.ViewHolder> {
    LayoutInflater layoutInflater;
    Context context;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseUser user;
    StorageReference storageReference;

    String gid;
    Uri imguri;
    ImageView send;
    TextView selectedItemList;

    ArrayList<ReadWriteContactDetails> arrContacts;
    private ArrayList<ReadWriteContactDetails> selectedContacts = new ArrayList<>(); // To store selected items

    // Reference to MainActivity

    AdapterDataInGroups(Context context, ArrayList<ReadWriteContactDetails> arrContacts,ImageView sendButton,TextView selectedCount,String gid) {
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.arrContacts = arrContacts;
        this.send = sendButton;
        this.gid = gid;
        this.selectedItemList = selectedCount;
        auth = FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        user = auth.getCurrentUser();

        storageReference = FirebaseStorage.getInstance().getReference("Display Pics");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.activity_cardview_data, parent, false);
        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Check if the lists are empty or if the position is invalid
        ReadWriteContactDetails model = (ReadWriteContactDetails) arrContacts.get(position);
        if (model.uid != null) {
            holder.name.setText(model.name);
            if(Objects.equals(model.residency, "Yes")) {
                holder.frResponse.setText(model.residency);
            }
            else{
                holder.frResponse.setText(" ");

            }
            if(Objects.equals(model.frProbable, "Yes")) {
                holder.frpResponse.setText(model.frProbable);
            }
            else{
                holder.frpResponse.setText(" ");

            }

//            Toast.makeText(context, "cardview " + imguri, Toast.LENGTH_SHORT).show();


//            StorageReference filereference = storageReference.child(user.getUid()).child(model.path);
//            filereference.putFile(imguri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    filereference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                        @Override
//                        public void onSuccess(Uri imguri) {
//                            intentUri = imguri;

//                            Toast.makeText(context, "Image about to populate in cardview " + imguri, Toast.LENGTH_SHORT).show();
            if (selectedContacts.contains(model)) {
                // Item is selected
                holder.contactImage.setImageResource(R.drawable.baseline_check_circle_24);
            } else {
                // Item is not selected
                if(model.uri!= null) {
                    imguri = Uri.parse(model.uri);
                    Picasso.get().load(imguri).into(holder.contactImage);
                }
                else{
                    holder.contactImage.setImageResource(R.drawable.baseline_person_24);

                }
            }

//            Toast.makeText(context, "Image populated in cardview " + imguri, Toast.LENGTH_SHORT).show();

            holder.row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectedContacts.contains(model)) {
                        // Item is already selected, so deselect it
                        selectedContacts.remove(model);
                        Picasso.get().load(imguri).into(holder.contactImage);
                    } else {
                        // Item is not selected, so select it
                        selectedContacts.add(model);
                        holder.contactImage.setImageResource(R.drawable.baseline_check_circle_24);
//                        Toast.makeText(context, "contactSelected : "+ selectedContacts.size(), Toast.LENGTH_SHORT).show();
                    }
                    // Notify item changed to update UI
                    notifyItemChanged(position);
                    updateVisibilityAndText(selectedContacts.size());

                }
            });
            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Get the reference to the "added_members" node
                    DatabaseReference addedMembersRef = database.getReference()
                            .child("Group_data")
                            .child(user.getUid())
                            .child(gid)
                            .child("added_members");

                    // Check for duplicates and add only unique items
                    for (ReadWriteContactDetails contact : selectedContacts) {
                        // Check if the item already exists
                        addedMembersRef.orderByChild("uid").equalTo(contact.uid)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (!dataSnapshot.exists()) {
                                            // Item doesn't exist, so add it
                                            String key = addedMembersRef.push().getKey();
                                            if (key != null) {
                                                addedMembersRef.child(contact.uid).setValue(contact);
                                                Log.e("keyofcontact", "key----"+key);

                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        // Handle onCancelled if needed
                                    }
                                });
                    }

                    Intent intent = new Intent(context, GroupDataScreen.class);
                    intent.putExtra("addedContacts", selectedContacts);
                    intent.putExtra("gid", gid);
                    Log.e("data gid", gid);
                    context.startActivity(intent);
                }
            });



            holder.deleteImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Delete Contact");
                    builder.setMessage("Do you want to delete this Contact");
                    builder.setIcon(R.drawable.baseline_delete_24);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("users");
                            DatabaseReference itemRef = databaseRef.child(user.getUid()).child("Created Contacts")
                                    .child(model.uid);
                            // Remove the data at the reference
                            itemRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    arrContacts.remove(model);
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

    public void filterList(ArrayList<ReadWriteContactDetails> filteredList) {
        arrContacts = filteredList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        // Return the size of the group_name list to ensure consistency
        return arrContacts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, frResponse, frpResponse;
        ImageView deleteImage,updateImage,contactImage;
        RelativeLayout row;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            contactImage = itemView.findViewById(R.id.data_image);
             name = itemView.findViewById(R.id.data_name);
             frResponse = itemView.findViewById(R.id.data_fr_response);
             frpResponse = itemView.findViewById(R.id.data_frp_response);
            row = itemView.findViewById(R.id.iiiirow);
            deleteImage = itemView.findViewById(R.id.data_delete_image);


        }
    }
    private void updateVisibilityAndText(int selectedCount) {
        if (selectedCount > 0) {
            send.setVisibility(View.VISIBLE);
            selectedItemList.setVisibility(View.VISIBLE);
            selectedItemList.setText(String.valueOf(selectedContacts.size())); // Update with your string resource
        } else {
            send.setVisibility(View.INVISIBLE);
            selectedItemList.setVisibility(View.INVISIBLE);
        }
    }


}
