package com.example.cloudspiritualgemapp;
import android.database.Cursor;
import android.graphics.Matrix;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import java.io.IOException;

import java.io.FileNotFoundException;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import androidx.appcompat.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class contactClickedScreen extends AppCompatActivity {
    RecyclerView recyclerView1;
    AdapterData adapterData;
    Button  btnUpload,dataAddButton;

    ImageView img;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseUser user;
    ProgressBar progressBar;
    StorageReference storageReference;
    Uri uri,downloadUri = null;
    String path = null;
    String name = "",number ="",whatsapp = "", age = "",occupation ="",address = "",stayingwith ="",chantinground = "",sg_boy = "",fr_probable = "", nativ = "",sg = "",residency = "",remarks ="";
    SearchView searchContact;
    int CAMERA_REQST_CODE = 1,GALLERY_RQST_CODE = 2;
    ArrayList<ReadWriteContactDetails> arrContacts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_button);
        dataAddButton = findViewById(R.id.data_add_button);
        getSupportActionBar().setTitle("Contacts");
        auth = FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        arrContacts = new ArrayList<>();
        user = auth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("Display Pics");
        searchContact = findViewById(R.id.searchView);
        searchContact.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Called when the user submits the query (e.g., presses "Search" button on keyboard)

                return false; // Return true to indicate that the query has been handled
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Called when the query text changes (each keystroke)
                // You can perform filtering or other operations here
                performSearch(newText);
                return true; // Return false to indicate that the listener did not handle the text change
            }
        });
        dataAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(contactClickedScreen.this);
                dialog.setContentView(R.layout.activity_data_entry);
                img = dialog.findViewById(R.id.contact_img);
                String uid = UUID.randomUUID().toString();
                btnUpload = dialog.findViewById(R.id.image_upload_button);
                btnUpload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (btnUpload.getText().toString().equals("UPLOAD IMAGE")) {
                            Intent iGallery = new Intent(Intent.ACTION_PICK);
                            iGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(iGallery, GALLERY_RQST_CODE);

                        }
                        if (btnUpload.getText().toString().equals("SAVE IMAGE")) {
                            progressBar.setVisibility(View.VISIBLE);
                            if (uri != null) {
                                path = uid + "." + getFleExtension(uri);
                                StorageReference filereference = storageReference.child(user.getUid()).child(uid + "." + getFleExtension(uri));
                                filereference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        filereference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri imguri) {
                                                downloadUri = imguri;

                                                Toast.makeText(contactClickedScreen.this, "Image about to populate  " + imguri, Toast.LENGTH_SHORT).show();
                                                Picasso.get().load(downloadUri).into(img);

                                                progressBar.setVisibility(View.GONE);
                                                btnUpload.setText("UPLOAD IMAGE");


                                                Toast.makeText(contactClickedScreen.this, "Image populated  " + imguri, Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    }
                                });

                            }
                        }

                    }
                });
                progressBar = dialog.findViewById(R.id.progress_bar);
                EditText editName = dialog.findViewById(R.id.contact_name_input);
                EditText editNumber = dialog.findViewById(R.id.contact_number_input);
                EditText editWhatsapp = dialog.findViewById(R.id.contact_whatsapp_number_input);
                EditText editAge  = dialog.findViewById(R.id.contact_age_input);
                EditText editOccupation = dialog.findViewById(R.id.contact_occupation_input);
                EditText editAddress  = dialog.findViewById(R.id.contact_address_input);
                EditText editStayingWith  = dialog.findViewById(R.id.contact_stayingWith_input);
                EditText editChantingRound  = dialog.findViewById(R.id.contact_chanting_input);
                EditText editNative  = dialog.findViewById(R.id.contact_native_input);
                EditText editSG  = dialog.findViewById(R.id.contact_SG_input);
                CheckBox residencyMember = dialog.findViewById(R.id.checkBoxFRJ);

                CheckBox sgBoy = dialog.findViewById(R.id.checkBoxSG);
                CheckBox frp = dialog.findViewById(R.id.checkBoxFRP);
                EditText editRemarks  = dialog.findViewById(R.id.contact_remarks_input);
                Button btnSave  = dialog.findViewById(R.id.contact_save_button);
                if(btnSave!= null) {
                    btnSave.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            progressBar.setVisibility(View.VISIBLE);


                            if (!editName.getText().toString().equals("")) {
                                name = editName.getText().toString();
                            } else {
                                Toast.makeText(contactClickedScreen.this, "Please Enter Name", Toast.LENGTH_SHORT).show();
                            }
                            if (!editNumber.getText().toString().equals("")) {
                                number = editNumber.getText().toString();
                            } else {
                                Toast.makeText(contactClickedScreen.this, "Please Enter Number", Toast.LENGTH_SHORT).show();
                            }
                            if (!editWhatsapp.getText().toString().equals("")) {
                                whatsapp = editWhatsapp.getText().toString();
                            } else {
                                Toast.makeText(contactClickedScreen.this, "Please Enter WhatsApp Number", Toast.LENGTH_SHORT).show();
                            }
                            if (!editAge.getText().toString().equals("")) {
                                age = editAge.getText().toString();
                            } else {
                                Toast.makeText(contactClickedScreen.this, "Please Enter Age", Toast.LENGTH_SHORT).show();
                            }
                            if (!editOccupation.getText().toString().equals("")) {
                                occupation = editOccupation.getText().toString();
                            } else {
                                Toast.makeText(contactClickedScreen.this, "Please Enter Occupation", Toast.LENGTH_SHORT).show();
                            }
                            if (!editAddress.getText().toString().equals("")) {
                                address = editAddress.getText().toString();
                            } else {
                                Toast.makeText(contactClickedScreen.this, "Please Enter Address", Toast.LENGTH_SHORT).show();
                            }
                            if (!editNative.getText().toString().equals("")) {
                                nativ = editNative.getText().toString();
                            } else {
                                Toast.makeText(contactClickedScreen.this, "Please Enter Native Place", Toast.LENGTH_SHORT).show();
                            }
                            if (!editSG.getText().toString().equals("")) {
                                sg = editSG.getText().toString();
                            } else {
                                Toast.makeText(contactClickedScreen.this, "Please Enter SG rating(1-10)", Toast.LENGTH_SHORT).show();
                            }
                            if (sgBoy.isChecked()){
                                residency = "Yes";
                            }
                            else{
                                residency = "No";
                            }
                            if (residencyMember.isChecked()){
                                sg_boy = "Yes";
                            }
                            else{
                                sg_boy = "No";
                            }
                            if (frp.isChecked()){
                                fr_probable = "Yes";
                            }else{
                                fr_probable = "No";
                            }
                            if (!editChantingRound.getText().toString().equals("")) {
                                chantinground = editChantingRound.getText().toString();
                            } else {
                                Toast.makeText(contactClickedScreen.this, "Please Enter Name", Toast.LENGTH_SHORT).show();
                            }
                            if (!editStayingWith.getText().toString().equals("")) {
                                stayingwith = editStayingWith.getText().toString();
                            } else {
                                Toast.makeText(contactClickedScreen.this, "Please Enter Staying With(parents/pg/flat)", Toast.LENGTH_SHORT).show();
                            }

                            if (!editRemarks.getText().toString().equals("")) {
                                remarks = editRemarks.getText().toString();
                            } else {
                                Toast.makeText(contactClickedScreen.this, "Please Enter remarks", Toast.LENGTH_SHORT).show();
                            }




                            FirebaseUser user = auth.getCurrentUser();
                            if (user != null) {
                                String imgUrl = null;
                                if(downloadUri!= null){
                                     imgUrl = downloadUri.toString();
                                }

                                ReadWriteContactDetails contact = new ReadWriteContactDetails(imgUrl,path,uid,name,number,whatsapp,age,occupation,address,stayingwith,chantinground,nativ,sg,residency,sg_boy,fr_probable,remarks);

                                database.getReference()
                                        .child("users")
                                        .child(user.getUid())
                                        .child("Created Contacts")
                                        .child(contact.uid)
                                        .setValue(contact)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
//                                                    // Group added successfully
//                                                    adapterData.notifyItemInserted(arrContacts.size() - 1);
//                                                    recyclerView1.scrollToPosition(arrContacts.size() - 1);

                                                    downloadUri= null;
                                                    Toast.makeText(contactClickedScreen.this, "Successfully Created Contact" , Toast.LENGTH_SHORT).show();
                                                    dialog.dismiss();
                                                } else {
                                                    // Failed to add group
                                                    Toast.makeText(contactClickedScreen.this, "Failed to Created Contact: " , Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        }
                    });
                }
                dialog.show();
            }
        });


        loadData();
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
            Toast.makeText(contactClickedScreen.this, "No data found", Toast.LENGTH_SHORT).show();
            adapterData.filterList(filteredList);
        }
        else {
            adapterData.filterList(filteredList);
        }
    }

    public String getFleExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == GALLERY_RQST_CODE) {
                uri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    Bitmap rotatedBitmap = ImageUtils.rotateBitmapIfNeeded(bitmap, getRealPathFromURI(uri));
                    img.setImageBitmap(rotatedBitmap);
                    btnUpload.setText("SAVE IMAGE");

                    Toast.makeText(contactClickedScreen.this, "Image Uploaded", Toast.LENGTH_SHORT).show();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public String getRealPathFromURI(Uri contentUri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, projection, null, null, null);
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String filePath = cursor.getString(columnIndex);
            cursor.close();
            return filePath;
        }
        return null;
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
                            Toast.makeText(contactClickedScreen.this, "load from load " , Toast.LENGTH_SHORT).show();

                        }
                    }
                    adapterData.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("MainActivity", "onCancelled: " + error.getMessage());
                }
            });
            recyclerView1 = findViewById(R.id.data_name_list);
            adapterData = new AdapterData(contactClickedScreen.this, arrContacts);
            recyclerView1.setLayoutManager(new LinearLayoutManager(this));
            recyclerView1.setAdapter(adapterData);
        }
    }
    public static class ImageUtils {

        public static Bitmap rotateBitmapIfNeeded(Bitmap bitmap, String imagePath) {
            try {
                ExifInterface exif = new ExifInterface(imagePath);
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
                Matrix matrix = new Matrix();
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        matrix.postRotate(90);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        matrix.postRotate(180);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        matrix.postRotate(270);
                        break;
                    default:
                        return bitmap; // No need to rotate
                }
                return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            } catch (IOException e) {
                e.printStackTrace();
                return bitmap; // Return original bitmap if an error occurs
            }
        }
    }

}
