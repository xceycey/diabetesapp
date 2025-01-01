package com.example.projem;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hadi.emojiratingbar.EmojiRatingBar;
import com.hadi.emojiratingbar.RateStatus;

import java.util.HashMap;
import java.util.Map;

public class Feedback extends AppCompatActivity {
    EmojiRatingBar emojiRatingBar;
    Button gonder;
    EditText mesaj;
    ImageButton geri;
    FirebaseAuth yetki;
    FirebaseUser user;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        db = FirebaseFirestore.getInstance();
        yetki = FirebaseAuth.getInstance();
        user = yetki.getCurrentUser();
        gonder = findViewById(R.id.gonder);
        emojiRatingBar = findViewById(R.id.emojiratingbar);
        mesaj = findViewById(R.id.mesaj);
        geri = findViewById(R.id.geritus);
        geri.setOnClickListener(v -> finish());
        gonder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RateStatus rating = emojiRatingBar.getCurrentRateStatus();
                int puan = 0;
                switch (rating) {
                    case AWFUL:
                        puan = 1;
                        break;
                    case BAD:
                        puan = 2;
                        break;
                    case OKAY:
                        puan = 3;
                        break;
                    case GOOD:
                        puan = 4;
                        break;
                    case GREAT:
                        puan = 5;
                        break;
                    case EMPTY:
                        puan = -1;
                        break;
                    default:
                        break;

                }
                String ad = user.getDisplayName();
                String mail = user.getEmail();
                String kullanicimesaji = mesaj.getText().toString();
                if (kullanicimesaji.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Lütfen mesajınızı giriniz.", Toast.LENGTH_LONG).show();

                } else {
                    if (puan == -1) {
                        Toast.makeText(getApplicationContext(), "Lütfen bir oy veriniz.", Toast.LENGTH_LONG).show();
                    } else {
                        System.out.println(ad + " " + mail + " " + puan + " " + kullanicimesaji);
                        Map<String, Object> feedback = new HashMap<>();
                        feedback.put("adsoyad", ad);
                        feedback.put("mail", mail);
                        feedback.put("puan", puan);
                        feedback.put("mesaj", kullanicimesaji);
                        db.collection("feedbacks")
                                .add(feedback)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Toast.makeText(getApplicationContext(), "Geribildiriminiz iletilmiştir.", Toast.LENGTH_LONG).show();
                                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(), "Bir sorun oluştu.", Toast.LENGTH_LONG).show();
                                        Log.w(TAG, "Error adding document", e);
                                    }
                                });

                        finish();
                    }

                }


            }
        });


    }
}