package com.example.projem;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class KayitSayfa extends AppCompatActivity {
    EditText mail,sifre,ad,soyad;
    AppCompatButton kaydol;
    TextView girisyap;
    FirebaseAuth yetki;
    FirebaseUser user;
    String email,parola;
    String isim,soyisim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kayit_sayfa);
        yetki=FirebaseAuth.getInstance();
        mail=findViewById(R.id.edtmail);
        sifre=findViewById(R.id.edtsifre);
        kaydol=findViewById(R.id.btnkaydol);
        ad=findViewById(R.id.edtad);
        soyad=findViewById(R.id.edtsoyad);
        girisyap=findViewById(R.id.txtgirisyap);




        girisyap.setOnClickListener(v->{
            startActivity(new Intent(KayitSayfa.this,LoginSayfa.class));
        });
        kaydol.setOnClickListener(v->{
            email=mail.getText().toString();
            parola=sifre.getText().toString();
            isim=ad.getText().toString();
            soyisim=soyad.getText().toString();
            if(email.isEmpty() ){
                Toast.makeText(this, "E-mail alanı boş olamaz", Toast.LENGTH_LONG).show();
            }
            if(parola.isEmpty()){
                Toast.makeText(this, "Parola alanı boş olamaz", Toast.LENGTH_LONG).show();
            }
            if(parola.isEmpty() && email.isEmpty()){
                Toast.makeText(this, "Alanlar boş olamaz", Toast.LENGTH_LONG).show();

            }
            else{
                System.out.println("deneme");
                yetki.createUserWithEmailAndPassword(email,parola).addOnCompleteListener(kayitgorev -> {
                    //kayıt başarılıysa
                    if(kayitgorev.isSuccessful()){
                        user= yetki.getCurrentUser();
                        UserProfileChangeRequest profileUpdate= new UserProfileChangeRequest.Builder().setDisplayName(isim+" "+soyisim).build();
                        user.updateProfile(profileUpdate).addOnCompleteListener(gorev -> {
                            if(gorev.isSuccessful()){
                                Log.v("Projem","Display name alındı.");
                            }
                        }).addOnFailureListener(hata -> {
                            Log.e("Projem","Display name alınamadı.");
                        });



                        Toast.makeText(KayitSayfa.this, "Kayıt başarılı", Toast.LENGTH_LONG).show();
                        yetki.signOut();
                        Intent k=new Intent(KayitSayfa.this,LoginSayfa.class);
                        startActivity(k);
                    }
                }).addOnFailureListener(hata -> {
                    //kayıt başarısızsa
                    Toast.makeText(KayitSayfa.this,hata.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

}