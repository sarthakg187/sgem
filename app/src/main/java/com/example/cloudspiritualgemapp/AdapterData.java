
package com.example.cloudspiritualgemapp;

import static androidx.appcompat.content.res.AppCompatResources.getColorStateList;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.ColorUtils;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import android.graphics.Color;
import android.content.res.ColorStateList;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.collection.LLRBNode;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class AdapterData extends RecyclerView.Adapter<AdapterData.ViewHolder> {
    LayoutInflater layoutInflater;
    Context context;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseUser user;
    StorageReference storageReference;
    List<String> color, color_load;
    List<Integer> intColor;
    String uid;
    Uri intentUri, imguri = null;
    ;
    contactClickedScreen contactActivity;

    ArrayList<ReadWriteContactDetails> arrContacts;
    // Reference to MainActivity

    AdapterData(Context context, ArrayList<ReadWriteContactDetails> arrContacts) {
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.arrContacts = arrContacts;
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        user = auth.getCurrentUser();
        color = new ArrayList<>();
        intColor = new ArrayList<>();
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
            if (Objects.equals(model.residency, "Yes")) {
                holder.frResponse.setText(model.residency);
            } else {
                holder.frResponse.setText(" ");

            }
            if (Objects.equals(model.frProbable, "Yes")) {
                holder.frpResponse.setText(model.frProbable);
            } else {
                holder.frpResponse.setText(" ");

            }
            int backgroundColor = holder.a.getDrawingCacheBackgroundColor();

            if (isColorDark(backgroundColor)) {
                // Background color is darker than LightSlateGray, do something here
                // For example, change text color to white
                holder.a.setTextColor(Color.WHITE);
            } else {
                // Background color is not darker than LightSlateGray, do something else if needed
                // For example, keep the original text color
                holder.a.setTextColor(Color.BLACK); // You should define originalTextColor somewhere
            }

            if (model.uri != null) {
                imguri = Uri.parse(model.uri);
                Picasso.get().load(imguri).into(holder.contactImage);
                Log.e("ModelUri","model uri####%%#^#^#&#&");
            } else {
                holder.contactImage.setImageResource(R.drawable.baseline_person_24);
            }
            DatabaseReference colorRef = database.getReference()
                    .child("users")
                    .child(user.getUid())
                    .child("Created Contacts")
                    .child(model.uid)
                    .child("Color");


            //Snapshot got of ASSociation

            colorRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        color.clear();
                        intColor.clear();
                        for (DataSnapshot outerSnapshot : snapshot.getChildren()) {
                            String value = outerSnapshot.getValue(String.class);
                            if (value != null) {
                                Log.e("Illegal", value + "=-----------color");
                                int ac = Color.parseColor("#" + value);
                                intColor.add(ac);
                            }
                        }

                    } else {
                        for (int w = 0; w < 5; w++) {
                            color.add("#FFFFFF");
                            int ac = Color.parseColor("#FFFFFF");
                            intColor.add(ac);

                        }
                    }
                    Log.e("color", "Arraycolor-----" + intColor.size());
                    ViewCompat.setBackgroundTintList(holder.a, getColorStateList(intColor.get(0)));
                    ViewCompat.setBackgroundTintList(holder.b, getColorStateList(intColor.get(1)));
                    ViewCompat.setBackgroundTintList(holder.c, getColorStateList(intColor.get(2)));
                    ViewCompat.setBackgroundTintList(holder.d, getColorStateList(intColor.get(3)));
                    ViewCompat.setBackgroundTintList(holder.e, getColorStateList(intColor.get(4)));

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("FirebaseDatabase", "Error" + error.getMessage());
                }
            });


            Log.e("color", "Arraycolor-----" + intColor);


            //          Toast.makeText(context, "cardview " + imguri, Toast.LENGTH_SHORT).show();


//            StorageReference filereference = storageReference.child(user.getUid()).child(model.path);
//            filereference.putFile(imguri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    filereference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                        @Override
//                        public void onSuccess(Uri imguri) {
//                            intentUri = imguri;

//                            Toast.makeText(context, "Image about to populate in cardview " + imguri, Toast.LENGTH_SHORT).show();


//                            Toast.makeText(context, "Image populated in cardview " + imguri, Toast.LENGTH_SHORT).show();
//                        }
//                    });
//
//                }
//            });
//            holder.updateImage.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Dialog dialog = new Dialog(context);
//                    dialog.setContentView(R.layout.activity_data_entry);
//                    TextView updateText = dialog.findViewById(R.id.data_create_text);
//                    EditText editName = dialog.findViewById(R.id.contact_name_input);
//                    EditText editNumber = dialog.findViewById(R.id.contact_number_input);
//                    EditText editWhatsapp = dialog.findViewById(R.id.contact_whatsapp_number_input);
//                    EditText editAge = dialog.findViewById(R.id.contact_age_input);
//                    EditText editOccupation = dialog.findViewById(R.id.contact_occupation_input);
//                    EditText editAddress = dialog.findViewById(R.id.contact_address_input);
//                    EditText editStayingWith = dialog.findViewById(R.id.contact_stayingWith_input);
//                    EditText editChantingRound = dialog.findViewById(R.id.contact_chanting_input);
//                    EditText editNative = dialog.findViewById(R.id.contact_native_input);
//                    EditText editSG = dialog.findViewById(R.id.contact_SG_input);
//                    EditText editResidencyStaus = dialog.findViewById(R.id.contact_residency_joining_input);
//                    EditText editRemarks = dialog.findViewById(R.id.contact_remarks_input);
//                    Button btnSave = dialog.findViewById(R.id.contact_save_button);
//                    updateText.setText(String.valueOf("Update Contact"));
//                    btnSave.setText((String.valueOf("Update")));
//                    editName.setText((arrContacts.get(holder.getAdapterPosition())).name);
//                    editNumber.setText((arrContacts.get(holder.getAdapterPosition())).number);
//                    editWhatsapp.setText((arrContacts.get(holder.getAdapterPosition())).whatsapp);
//                    editAge.setText((arrContacts.get(holder.getAdapterPosition())).age);
//                    editOccupation.setText((arrContacts.get(holder.getAdapterPosition())).occupation);
//                    editAddress.setText((arrContacts.get(holder.getAdapterPosition())).address);
//                    editStayingWith.setText((arrContacts.get(holder.getAdapterPosition())).stayingwith);
//                    editChantingRound.setText((arrContacts.get(holder.getAdapterPosition())).chantinground);
//                    editNative.setText((arrContacts.get(holder.getAdapterPosition())).nativ);
//                    editSG.setText((arrContacts.get(holder.getAdapterPosition())).sg);
//                    editResidencyStaus.setText((arrContacts.get(holder.getAdapterPosition())).residency);
//                    editRemarks.setText((arrContacts.get(holder.getAdapterPosition())).remarks);
//                    if (btnSave != null) {
//
//                        btnUpdate.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                progressBar.setVisibility(View.VISIBLE);
//                                String name = "", number = "", whatsapp = "", age = "", occupation = "", address = "", stayingwith = "", chantinground = "", nativ = "", sg = "", residency = "", remarks = "";
//                                if (!editName.getText().toString().equals("")) {
//                                    name = editName.getText().toString();
//                                } else {
//                                    Toast.makeText(context, "Please Enter Name", Toast.LENGTH_SHORT).show();
//                                }
//                                if (!editNumber.getText().toString().equals("")) {
//                                    number = editNumber.getText().toString();
//                                } else {
//                                    Toast.makeText(context, "Please Enter Number", Toast.LENGTH_SHORT).show();
//                                }
//                                if (!editWhatsapp.getText().toString().equals("")) {
//                                    whatsapp = editWhatsapp.getText().toString();
//                                } else {
//                                    Toast.makeText(context, "Please Enter WhatsApp Number", Toast.LENGTH_SHORT).show();
//                                }
//                                if (!editAge.getText().toString().equals("")) {
//                                    age = editAge.getText().toString();
//                                } else {
//                                    Toast.makeText(context, "Please Enter Age", Toast.LENGTH_SHORT).show();
//                                }
//                                if (!editOccupation.getText().toString().equals("")) {
//                                    occupation = editOccupation.getText().toString();
//                                } else {
//                                    Toast.makeText(context, "Please Enter Occupation", Toast.LENGTH_SHORT).show();
//                                }
//                                if (!editAddress.getText().toString().equals("")) {
//                                    address = editAddress.getText().toString();
//                                } else {
//                                    Toast.makeText(context, "Please Enter Address", Toast.LENGTH_SHORT).show();
//                                }
//                                if (!editNative.getText().toString().equals("")) {
//                                    nativ = editNative.getText().toString();
//                                } else {
//                                    Toast.makeText(context, "Please Enter Native Place", Toast.LENGTH_SHORT).show();
//                                }
//                                if (!editSG.getText().toString().equals("")) {
//                                    sg = editSG.getText().toString();
//                                } else {
//                                    Toast.makeText(context, "Please Enter SG rating(1-10)", Toast.LENGTH_SHORT).show();
//                                }
//                                if (!editChantingRound.getText().toString().equals("")) {
//                                    chantinground = editChantingRound.getText().toString();
//                                } else {
//                                    Toast.makeText(context, "Please Enter Name", Toast.LENGTH_SHORT).show();
//                                }
//                                if (!editStayingWith.getText().toString().equals("")) {
//                                    stayingwith = editStayingWith.getText().toString();
//                                } else {
//                                    Toast.makeText(context, "Please Enter Staying With(parents/pg/flat)", Toast.LENGTH_SHORT).show();
//                                }
//                                if (!editResidencyStaus.getText().toString().equals("")) {
//                                    residency = editResidencyStaus.getText().toString();
//                                } else {
//                                    Toast.makeText(context, "Please Enter residency joining status(yes/n0)", Toast.LENGTH_SHORT).show();
//                                }
//                                if (!editRemarks.getText().toString().equals("")) {
//                                    remarks = editRemarks.getText().toString();
//                                } else {
//                                    Toast.makeText(context, "Please Enter remarks", Toast.LENGTH_SHORT).show();
//                                }
//
//                                FirebaseUser user = auth.getCurrentUser();
//                                if (user != null) {
//                                    // Iterate over the list of group names
//                                    String imgUrl = uri.toString();
//                                    arrContacts.add(new ReadWriteContactDetails(imgUrl, path, uid, name, number, whatsapp, age, occupation, address, stayingwith, chantinground, nativ, sg, residency, remarks));
//
//                                    database.getReference()
//                                            .child("users")
//                                            .child(user.getUid())
//                                            .child("Created Contacts")
//                                            .child(uid)
//                                            .setValue(arrContacts)
//                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                @Override
//                                                public void onComplete(@NonNull Task<Void> task) {
//                                                    if (task.isSuccessful()) {
//                                                        // Group added successfully
//                                                        adapterData.notifyItemInserted(arrContacts.size() - 1);
//                                                        recyclerView1.scrollToPosition(arrContacts.size() - 1);
//
//                                                        Toast.makeText(contactClickedScreen.this, "Successfully Created Contact", Toast.LENGTH_SHORT).show();
//                                                        dialog.dismiss();
//                                                    } else {
//                                                        // Failed to add group
//                                                        Toast.makeText(contactClickedScreen.this, "Failed to Created Contact: ", Toast.LENGTH_SHORT).show();
//                                                    }
//                                                }
//                                            });
//                                }
//
//
//                            }
//                        });
//                        dialog.show();
//                    }
//                }
//            });
            holder.row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, contactDetails.class);
                    intent.putExtra("contactDetails", model);
                    intent.putExtra("contactuid", model.uid);

                    intent.putExtra("image", imguri.toString());
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


// Assuming storageReference is your Firebase Storage root reference
                            if (model.path != null) {
                                StorageReference filereference = storageReference.child(user.getUid()).child(model.path);

// Delete the file from Firebase Storage
                                filereference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // File deleted successfully
                                        // Now you can proceed with any additional tasks, such as updating the UI or database
                                        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("users");
                                        DatabaseReference itemRef = databaseRef.child(user.getUid()).child("Created Contacts").child(model.uid);

                                        // Remove the data at the reference
                                        itemRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                // Update UI or perform other actions
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Handle any errors that may occur during deletion
                                        Log.e("DeleteFile", "Failed to delete file: " + e.getMessage());
                                        // You might want to inform the user or retry the deletion
                                    }
                                });
                            }
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
        TextView name, frResponse, frpResponse, a, b, c, d, e;
        ImageView deleteImage, updateImage, contactImage;
        RelativeLayout row;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            contactImage = itemView.findViewById(R.id.data_image);
            name = itemView.findViewById(R.id.data_name);
            frResponse = itemView.findViewById(R.id.data_fr_response);
            frpResponse = itemView.findViewById(R.id.data_frp_response);
            row = itemView.findViewById(R.id.iiiirow);
            deleteImage = itemView.findViewById(R.id.data_delete_image);
            a = itemView.findViewById(R.id.acolor);
            b = itemView.findViewById(R.id.bcolor);
            c = itemView.findViewById(R.id.ccolor);
            d = itemView.findViewById(R.id.dcolor);
            e = itemView.findViewById(R.id.ecolor);

        }
    }

    private ColorStateList getColorStateList(int colorValue) {
        return ColorStateList.valueOf(colorValue);
    }

    public boolean isColorDark(int color) {
        final double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        return !(darkness < 0.5);
    }
}
