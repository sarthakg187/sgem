package com.example.cloudspiritualgemapp;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class contactDetails extends AppCompatActivity {
    private TextView textName, textNumber, textWhatsApp, textAge, textOccupation,
            textAddress, textStayingWith, textChantingRound, textNative,
            textSG, textResidency, textSgBoy, textFrprobable, textRemarks;
    ImageView img;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseUser user;
    String contact;
    List<String> yellow ;
    int iyellow= 0;
    private static final int[] associationidArray = {R.id.a0,R.id.a1,R.id.a2,R.id.a3,R.id.a4,R.id.a5,R.id.a6,R.id.a7,R.id.a8};
    private static final int[] bookidArray = {R.id.b0, R.id.b1, R.id.b2, R.id.b3, R.id.b4, R.id.b5, R.id.b6, R.id.b7, R.id.b8, R.id.b9, R.id.b10, R.id.b11,
            R.id.b12, R.id.b13, R.id.b14, R.id.b15, R.id.b16, R.id.b17, R.id.b18, R.id.b19, R.id.b20, R.id.b21, R.id.b22,
            R.id.b23, R.id.b24, R.id.b25, R.id.b26, R.id.b27, R.id.b28, R.id.b29, R.id.b30, R.id.b31, R.id.b32, R.id.b33,
            R.id.b34, R.id.b35, R.id.b36, R.id.b37, R.id.b38, R.id.b39, R.id.b40, R.id.b41, R.id.b42, R.id.b43, R.id.b44,
            R.id.b45, R.id.b46, R.id.b47, R.id.b48, R.id.b49, R.id.b50, R.id.b51, R.id.b52, R.id.b53, R.id.b54, R.id.b55,
            R.id.b56, R.id.b57, R.id.b58, R.id.b59, R.id.b60, R.id.b61, R.id.b62
    };
    private static final int[] chantArray = {R.id.c0,R.id.c1,R.id.c2};

    private static final int[] devotionArray = {R.id.d0, R.id.d1, R.id.d2, R.id.d3, R.id.d4, R.id.d5, R.id.d6, R.id.d7, R.id.d8, R.id.d9, R.id.d10, R.id.d11,
            R.id.d12, R.id.d13, R.id.d14, R.id.d15, R.id.d16, R.id.d17, R.id.d18, R.id.d19, R.id.d20, R.id.d21, R.id.d22,
            R.id.d23, R.id.d24, R.id.d25, R.id.d26, R.id.d27, R.id.d28, R.id.d29
    };
    private static final int[] expeditionArray = {R.id.e0,R.id.e1,R.id.e2};
    private Button[] a = new Button[associationidArray.length];
    private Button[] b = new Button[bookidArray.length];
    private Button[] c = new Button[chantArray.length];
    private Button[] d = new Button[devotionArray.length];
    private Button[] e = new Button[expeditionArray.length];
    List<Boolean> associationLoad,devotionList,bookList,chantList,expeditionList;
    List<String > color;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_detail);
        Intent intent = getIntent();

        if (intent != null) {
             contact = getIntent().getStringExtra("contactuid");
        }
        // Initialize TextViews
        textName = findViewById(R.id.text_name);
        textNumber = findViewById(R.id.text_number);
        textWhatsApp = findViewById(R.id.text_whatsapp);
        textAge = findViewById(R.id.text_age);
        textOccupation = findViewById(R.id.text_occupation);
        textAddress = findViewById(R.id.text_address);
        textStayingWith = findViewById(R.id.text_stayingwith);
        textChantingRound = findViewById(R.id.text_chantinground);
        textNative = findViewById(R.id.text_native);
        textSG = findViewById(R.id.text_sg);
        textResidency = findViewById(R.id.text_residency);
        textSgBoy = findViewById(R.id.text_sg_rating);
        textFrprobable = findViewById(R.id.text_residencyprobable);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        user = auth.getCurrentUser();
        textRemarks = findViewById(R.id.text_remarks);
        img = findViewById(R.id.image_view);
        associationLoad= new ArrayList<>();
        bookList = new ArrayList<>();
        devotionList = new ArrayList<>();
        chantList = new ArrayList<>();
        expeditionList = new ArrayList<>();
        color = new ArrayList<>();


        DatabaseReference colorRef = database.getReference()
                .child("users")
                .child(user.getUid())
                .child("Created Contacts")
                .child(contact)
                .child("Color");

        //Snapshot got of ASSociation

        colorRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    color.clear();
                    for (DataSnapshot outerSnapshot : snapshot.getChildren()) {
                        String value = outerSnapshot.getValue(String.class);
                        if (value != null) {
                            color.add(value);
                        }
                    }

                } else {
                    for (int w = 0; w < 5; w++) {
                        color.add("FFFFFF");

                    }
                }
                Log.e("color", "Arraycolor-----" + color);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseDatabase","Error"+error.getMessage());
            }
        });
        DatabaseReference associationRef = database.getReference()
                .child("users")
                .child(user.getUid())
                .child("Created Contacts")
                .child(contact)
                .child("Association");

        //Snapshot got of ASSociation

        associationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    associationLoad.clear();
                    for (DataSnapshot outerSnapshot : snapshot.getChildren()) {
                        Boolean value = outerSnapshot.getValue(Boolean.class);
                        if (value != null) {
                            associationLoad.add(value);
                        }
                    }

                } else {
                    for (int w = 0; w < associationidArray.length; w++) {
                        Log.e("FirebaseDatabase", "Array" + w);
                        associationLoad.add(true);

                    }
                }
                for (int w = 0; w < associationidArray.length; w++) {
                    final int index = w;  // Capture the current value of w

                    a[w] = (Button)findViewById(associationidArray[w]);

                    if (associationLoad.get(index)) {
                        associationLoad.set(index,true);
                        a[index].setText("NO");
                        a[index].setTextColor(Color.WHITE);
                        a[index].setBackgroundResource(R.drawable.gray);
                        Log.e("Buttontap", "Button NO"+associationLoad);
                    } else {

                        associationLoad.set(index,false);
                        a[index].setText("YES");
                        a[index].setTextColor(Color.BLACK);
                        a[index].setBackgroundResource(R.drawable.yellow);
                        Log.e("Buttontap", "Button YES"+associationLoad);

                    }

                }
                Log.e("FirebaseDatabase","Loaded"+associationLoad);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseDatabase","Error"+error.getMessage());
            }
        });



        for (int w = 0; w < associationidArray.length; w++) {
            final int index = w;  // Capture the current value of w

            a[w] = (Button)findViewById(associationidArray[w]);
            a[w].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("FirebaseDatabase", "Array--" + index); // Use the captured index
                         if (associationLoad.get(index)) {
                             associationLoad.set(index,false);
                             color.set(0,darkenColor(color.get(0),28));
                            a[index].setText("YES");
                            a[index].setTextColor(Color.BLACK);
                            a[index].setBackgroundResource(R.drawable.yellow);
                            Log.e("Buttontap", "Button YES"+associationLoad);
                        } else {
                             associationLoad.set(index,true);
                             color.set(0,lightenColor(color.get(0),28));
                             a[index].setText("NO");
                            a[index].setTextColor(Color.WHITE);
                            a[index].setBackgroundResource(R.drawable.gray);
                             Log.e("Buttontap", "Button NO"+associationLoad);
                         }
                    }
                });
                a[index].setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            a[index].setTextColor(Color.WHITE);  // Set text color to white

                            v.performClick();
                        }
                    }
                });
            }
        DatabaseReference bookref = database.getReference()
                .child("users")
                .child(user.getUid())
                .child("Created Contacts")
                .child(contact)
                .child("Book");

        //Snapshot got of Book

        bookref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    bookList.clear();
                    for (DataSnapshot outerSnapshot : snapshot.getChildren()) {
                        Boolean value = outerSnapshot.getValue(Boolean.class);
                        if (value != null) {
                            bookList.add(value);
                        }
                    }

                } else {
                    for (int w = 0; w < bookidArray.length; w++) {
                        Log.e("FirebaseDatabase", "Array" + w);
                        bookList.add(true);

                    }
                }
                for (int w = 0; w < bookidArray.length; w++) {
                    final int index = w;
                    b[w] = (Button)findViewById(bookidArray[w]);
                    if (bookList.get(index)) {
                        bookList.set(index,true);
                        b[index].setText("NO");
                        b[index].setTextColor(Color.WHITE);
                        b[index].setBackgroundResource(R.drawable.gray);
                        Log.e("Buttontap", "Button NO"+bookList);
                    } else {
                        bookList.set(index,false);
                        b[index].setText("YES");
                        b[index].setTextColor(Color.BLACK);
                        b[index].setBackgroundResource(R.drawable.yellow);
                        Log.e("Buttontap", "Button YES"+bookList);

                    }

                }
                Log.e("FirebaseDatabase","Loaded"+bookList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseDatabase","Error"+error.getMessage());
            }
        });



        for (int w = 0; w < bookidArray.length; w++) {
            final int index = w;  // Capture the current value of w

            b[w] = (Button)findViewById(bookidArray[w]);
            b[w].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("FirebaseDatabase", "Array--" + index); // Use the captured index
                    if (bookList.get(index)) {
                        bookList.set(index,false);
                        color.set(1,darkenColor(color.get(1),4));
                        b[index].setText("YES");
                        b[index].setTextColor(Color.BLACK);
                        b[index].setBackgroundResource(R.drawable.yellow);
                        Log.e("Buttontap", "Button YES"+bookList);
                    } else {
                        bookList.set(index,true);
                        color.set(1,lightenColor(color.get(1),4));
                        b[index].setText("NO");
                        b[index].setTextColor(Color.WHITE);
                        b[index].setBackgroundResource(R.drawable.gray);
                        Log.e("Buttontap", "Button NO"+bookList);
                    }
                }
            });
            b[index].setOnFocusChangeListener(new View.OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        b[index].setTextColor(Color.WHITE);  // Set text color to white

                        v.performClick();
                    }
                }
            });
        }
        DatabaseReference chantRef = database.getReference()
                .child("users")
                .child(user.getUid())
                .child("Created Contacts")
                .child(contact)
                .child("Chant");

        //Snapshot got of Book

        chantRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    chantList.clear();
                    for (DataSnapshot outerSnapshot : snapshot.getChildren()) {
                        Boolean value = outerSnapshot.getValue(Boolean.class);
                        if (value != null) {
                            chantList.add(value);
                        }
                    }

                } else {
                    for (int w = 0; w < chantArray.length; w++) {
                        Log.e("FirebaseDatabase", "Array" + w);
                        chantList.add(true);

                    }
                }
                for (int w = 0; w < chantArray.length; w++) {
                    final int index = w;  // Capture the current value of w

                    c[w] = (Button)findViewById(chantArray[w]);

                    if (chantList.get(index)) {
                        chantList.set(index,true);
                        c[index].setTextColor(Color.WHITE);
                        c[index].setBackgroundResource(R.drawable.gray);
                        Log.e("Buttontap", "Button NO"+chantList);
                    } else {

                        chantList.set(index,false);
                        c[index].setTextColor(Color.BLACK);
                        c[index].setBackgroundResource(R.drawable.yellow);
                        Log.e("Buttontap", "Button YES"+chantList);

                    }

                }
                Log.e("FirebaseDatabase","Loaded"+chantList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseDatabase","Error"+error.getMessage());
            }
        });



        for (int w = 0; w < chantArray.length; w++) {
            final int index = w;  // Capture the current value of w

            c[w] = (Button)findViewById(chantArray[w]);
            c[w].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("FirebaseDatabase", "Array--" + index); // Use the captured index
                    if (chantList.get(index)) {
                        color.set(2,darkenColor(color.get(2),85));

                        chantList.set(index,false);
                        c[index].setTextColor(Color.BLACK);
                        c[index].setBackgroundResource(R.drawable.yellow);
                        Log.e("Buttontap", "Button YES"+chantList);
                    } else {
                        color.set(2,lightenColor(color.get(2),85));

                        chantList.set(index,true);
                        c[index].setTextColor(Color.WHITE);
                        c[index].setBackgroundResource(R.drawable.gray);
                        Log.e("Buttontap", "Button NO"+chantList);
                    }
                }
            });
            c[index].setOnFocusChangeListener(new View.OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        c[index].setTextColor(Color.WHITE);  // Set text color to white

                        v.performClick();
                    }
                }
            });
        }
        DatabaseReference devotionRef = database.getReference()
                .child("users")
                .child(user.getUid())
                .child("Created Contacts")
                .child(contact)
                .child("Devotion");

        //Snapshot got of Book

        devotionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    devotionList.clear();
                    for (DataSnapshot outerSnapshot : snapshot.getChildren()) {
                        Boolean value = outerSnapshot.getValue(Boolean.class);
                        if (value != null) {
                            devotionList.add(value);
                        }
                    }
                } else {
                    for (int w = 0; w < devotionArray.length; w++) {
                        Log.e("FirebaseDatabase", "Array" + w);
                        devotionList.add(true);
                    }
                }
                for (int w = 0; w < 12; w++) {
                    final int index = w;  // Capture the current value of w

                    d[w] = (Button)findViewById(devotionArray[w]);

                    if (devotionList.get(index)) {
                        devotionList.set(index,true);
                        d[index].setTextColor(Color.WHITE);
                        d[index].setBackgroundResource(R.drawable.gray);
                        Log.e("Buttontap", "Button NO"+devotionList);
                    } else {
                        devotionList.set(index,false);
                        d[index].setTextColor(Color.BLACK);
                        d[index].setBackgroundResource(R.drawable.yellow);
                        Log.e("Buttontap", "Button YES"+devotionList);
                    }

                }
                for (int w = 12; w < devotionArray.length; w++) {
                    final int index = w;  // Capture the current value of w

                    d[w] = (Button)findViewById(devotionArray[w]);

                    if (devotionList.get(index)) {
                        devotionList.set(index,true);
                        d[index].setText("NO");
                        d[index].setTextColor(Color.WHITE);
                        d[index].setBackgroundResource(R.drawable.gray);
                        Log.e("Buttontap", "Button NO"+devotionList);
                    } else {
                        d[index].setText("YES");
                        devotionList.set(index,false);
                        d[index].setTextColor(Color.BLACK);
                        d[index].setBackgroundResource(R.drawable.yellow);
                        Log.e("Buttontap", "Button YES"+devotionList);

                    }

                }
                Log.e("FirebaseDatabase","Loaded"+devotionList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseDatabase","Error"+error.getMessage());
            }
        });



        for (int w = 0; w < 12; w++) {
            final int index = w;  // Capture the current value of w

            d[w] = (Button)findViewById(devotionArray[w]);
            d[w].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("FirebaseDatabase", "Array--" + index); // Use the captured index
                    if (devotionList.get(index)) {
                        devotionList.set(index,false);
                        color.set(3,darkenColor(color.get(3),8));
                        d[index].setTextColor(Color.BLACK);
                        d[index].setBackgroundResource(R.drawable.yellow);
                        Log.e("Buttontap", "Button YES"+devotionList);
                    } else {
                        devotionList.set(index,true);
                        d[index].setTextColor(Color.WHITE);
                        color.set(3,lightenColor(color.get(3),8));
                        d[index].setBackgroundResource(R.drawable.gray);
                        Log.e("Buttontap", "Button NO"+devotionList);
                    }
                }
            });

            d[index].setOnFocusChangeListener(new View.OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        d[index].setTextColor(Color.WHITE);  // Set text color to white

                        v.performClick();
                    }
                }
            });
        }
        for (int w = 12; w < devotionArray.length; w++) {
            final int index = w;  // Capture the current value of w

            d[w] = (Button)findViewById(devotionArray[w]);
            d[w].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("FirebaseDatabase", "Array--" + index); // Use the captured index
                    if (devotionList.get(index)) {
                        devotionList.set(index,false);
                        color.set(3,darkenColor(color.get(3),8));
                        d[index].setText("YES");
                        d[index].setTextColor(Color.BLACK);
                        d[index].setBackgroundResource(R.drawable.yellow);
                        Log.e("Buttontap", "Button YES"+devotionList);
                    } else {
                        devotionList.set(index,true);
                        color.set(3,lightenColor(color.get(3),8));
                        d[index].setText("NO");
                        d[index].setTextColor(Color.WHITE);
                        d[index].setBackgroundResource(R.drawable.gray);
                        Log.e("Buttontap", "Button NO"+devotionList);
                    }
                }
            });

            d[index].setOnFocusChangeListener(new View.OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        d[index].setTextColor(Color.WHITE);  // Set text color to white

                        v.performClick();
                    }
                }
            });
        }
        DatabaseReference expeditionRef = database.getReference()
                .child("users")
                .child(user.getUid())
                .child("Created Contacts")
                .child(contact)
                .child("Expedition");
        expeditionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    expeditionList.clear();
                    for (DataSnapshot outerSnapshot : snapshot.getChildren()) {
                        Boolean value = outerSnapshot.getValue(Boolean.class);
                        if (value != null) {
                            expeditionList.add(value);
                        }
                    }

                } else {
                    for (int w = 0; w < expeditionArray.length; w++) {
                        Log.e("FirebaseDatabase", "Array" + w);
                        expeditionList.add(true);

                    }
                }
                for (int w = 0; w < expeditionArray.length; w++) {
                    final int index = w;  // Capture the current value of w

                    e[w] = (Button)findViewById(expeditionArray[w]);

                    if (expeditionList.get(index)) {
                        expeditionList.set(index,true);
                        e[index].setTextColor(Color.WHITE);
                        e[index].setBackgroundResource(R.drawable.gray);
                        Log.e("Buttontap", "Button NO"+expeditionList);
                    } else {

                        expeditionList.set(index,false);
                        e[index].setTextColor(Color.BLACK);
                        e[index].setBackgroundResource(R.drawable.yellow);
                        Log.e("Buttontap", "Button YES"+expeditionList);

                    }

                }
                Log.e("FirebaseDatabase","Loaded"+expeditionList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseDatabase","Error"+error.getMessage());
            }
        });



        for (int w = 0; w < expeditionArray.length; w++) {
            final int index = w;  // Capture the current value of w

            e[w] = (Button)findViewById(expeditionArray[w]);
            e[w].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("FirebaseDatabase", "Array--" + index); // Use the captured index
                    if (expeditionList.get(index)) {
                        color.set(4,darkenColor(color.get(4),85));
                        expeditionList.set(index,false);
                        e[index].setTextColor(Color.BLACK);
                        e[index].setBackgroundResource(R.drawable.yellow);

                        Log.e("Buttontap", "Button YES"+expeditionList);
                    } else {
                        expeditionList.set(index,true);
                        color.set(4,lightenColor(color.get(4),85));

                        e[index].setTextColor(Color.WHITE);
                        e[index].setBackgroundResource(R.drawable.gray);
                        Log.e("Buttontap", "Button NO"+expeditionList);
                    }
                }
            });
            e[index].setOnFocusChangeListener(new View.OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        e[index].setTextColor(Color.WHITE);
                        v.performClick();
                    }
                }
            });
        }
        if (intent != null) {
                ReadWriteContactDetails model = (ReadWriteContactDetails) intent.getSerializableExtra("contactDetails");
                if (model != null) {
                    textName.setText(model.name);
                    textNumber.setText(model.number);
                    textWhatsApp.setText(model.whatsapp);
                    textAge.setText(model.age);
                    textOccupation.setText(model.occupation);
                    textAddress.setText(model.address);
                    textStayingWith.setText(model.stayingwith);
                    textChantingRound.setText(model.chantinground);
                    textNative.setText(model.nativ);
                    textSG.setText(model.sg);
                    textSgBoy.setText(model.sgBoy);
                    textResidency.setText(model.residency);
                    textFrprobable.setText(model.frProbable);
                    textRemarks.setText(model.remarks);
                    textName.setText(model.name);
                }
                if(model.uri!= null) {
                    Uri imguri = Uri.parse(model.uri);
                    Picasso.get().load(imguri).into(img);
                }
                else{
                    img.setImageResource(R.drawable.baseline_person_24);

                }
            }
        }

    @Override
    public void onBackPressed() {
        database.getReference()
                .child("users")
                .child(user.getUid())
                .child("Created Contacts")
                .child(contact)
                .child("Association")
                .setValue(associationLoad)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            Log.e("ListBoolean", "Uploaded-------" + associationLoad);


                        } else {
                            Log.e("ListBoolean", "Not Upoaded--------" + associationLoad);

                        }
                    }
                });
        Log.e("ListBoolean", "Uploaded-------" + associationLoad);
        database.getReference()
                .child("users")
                .child(user.getUid())
                .child("Created Contacts")
                .child(contact)
                .child("Book")
                .setValue(bookList)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            Log.e("ListBoolean", "Uploaded-------" + bookList);


                        } else {
                            Log.e("ListBoolean", "Not Upoaded--------" + bookList);

                        }
                    }
                });
        database.getReference()
                .child("users")
                .child(user.getUid())
                .child("Created Contacts")
                .child(contact)
                .child("Chant")
                .setValue(chantList)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            Log.e("ListBoolean", "Uploaded-------" + chantList);


                        } else {
                            Log.e("ListBoolean", "Not Upoaded--------" + chantList);

                        }
                    }
                });
        database.getReference()
                .child("users")
                .child(user.getUid())
                .child("Created Contacts")
                .child(contact)
                .child("Devotion")
                .setValue(devotionList)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            Log.e("ListBoolean", "Uploaded-------" + devotionList);


                        } else {
                            Log.e("ListBoolean", "Not Upoaded--------" + devotionList);

                        }
                    }
                });
        database.getReference()
                .child("users")
                .child(user.getUid())
                .child("Created Contacts")
                .child(contact)
                .child("Expedition")
                .setValue(expeditionList)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            Log.e("ListBoolean", "Uploaded-------" + expeditionList);


                        } else {
                            Log.e("ListBoolean", "Not Upoaded--------" + expeditionList);

                        }
                    }
                });
        database.getReference()
                .child("users")
                .child(user.getUid())
                .child("Created Contacts")
                .child(contact)
                .child("Color")
                .setValue(color)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            Log.e("ListBoolean", "Uploaded color-------" + color);

                        } else {
                            Log.e("ListBoolean", "Not color Upoaded--------" + color);

                        }
                    }
                });


        super.onBackPressed();



    }

//    public static String darkenColor(String hexColor, int darkenAmount) {
//        // Convert hexadecimal to RGB
//        int red = Integer.parseInt(hexColor.substring(0, 2), 16);     // FF
//        int green = Integer.parseInt(hexColor.substring(2, 4), 16);   // 8C
//        int blue = Integer.parseInt(hexColor.substring(4, 6), 16);    // 20
//
//        // Darken the color by subtracting a fixed value from each component
//        red = Math.max(0, red-darkenAmount/30);
//        green = Math.max(0, 0 ); // Adjusted for yellowish tint
//        blue = Math.max(0,0);
//
//        // Convert RGB back to hexadecimal
//        String darkHexColor = String.format("%02X%02X%02X", red, green, blue);
//        return darkHexColor;
//    }
//public static String darkenColor(String hexColor, int darkenAmount) {
//    // Convert hexadecimal to RGB
//    int red = Integer.parseInt(hexColor.substring(0, 2), 16);     // FF
//    int green = Integer.parseInt(hexColor.substring(2, 4), 16);   // 8C
//    int blue = Integer.parseInt(hexColor.substring(4, 6), 16);    // 20
//
//    // Darken the color by subtracting a fixed value from each component
//    red = Math.max(0, 0);
//    green = Math.max(0, green-darkenAmount/30 ); // Adjusted for yellowish tint
//    blue = Math.max(0,0);
//
//    // Convert RGB back to hexadecimal
//    String darkHexColor = String.format("%02X%02X%02X", red, green, blue);
//    return darkHexColor;
//}
public static String darkenColor(String hexColor, int darkenAmount) {
    // Convert hexadecimal to RGB
    int red = Integer.parseInt(hexColor.substring(0, 2), 16);     // FF
    int green = Integer.parseInt(hexColor.substring(2, 4), 16);   // 8C
    int blue = Integer.parseInt(hexColor.substring(4, 6), 16);    // 20


    // Darken the color by subtracting a fixed value from each component
    red = Math.max(0,red-darkenAmount);
    green = Math.max(0, green-darkenAmount); // Adjusted for yellowish tint
    blue = Math.max(0,blue-darkenAmount);

    // Convert RGB back to hexadecimal
    String darkHexColor = String.format("%02X%02X%02X", red, green, blue);
    return darkHexColor;
}



    public boolean isColorDark(String hexColor) {
        int red = Integer.parseInt(hexColor.substring(0, 2), 16);
        int green = Integer.parseInt(hexColor.substring(2, 4), 16);
        int blue = Integer.parseInt(hexColor.substring(4, 6), 16);

        // Calculate the darkness using luminance or other suitable methods
        double darkness = 1 - (0.299 * red + 0.587 * green + 0.114 * blue) / 255;

        // Check if the color is darker than LightSlateGray (#778899)
        return darkness > 0.5; // Adjust this threshold based on your preference
    }
    public static String lightenColor(String hexColor, int lightenAmount) {
        // Convert hexadecimal to RGB
        int red = Integer.parseInt(hexColor.substring(0, 2), 16);
        int green = Integer.parseInt(hexColor.substring(2, 4), 16);
        int blue = Integer.parseInt(hexColor.substring(4, 6), 16);

        // Lighten the color by adding a fixed value to each component
        red = Math.min(255, red + lightenAmount);
        green = Math.min(255, green + lightenAmount);
        blue = Math.min(255, blue + lightenAmount);

        // Convert RGB back to hexadecimal
        String lightHexColor = String.format("%02X%02X%02X", red, green, blue);
        return lightHexColor;
    }


}
