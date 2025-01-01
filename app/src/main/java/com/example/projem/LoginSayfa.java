package com.example.projem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginSayfa extends AppCompatActivity {

    EditText mail,sifre;
    AppCompatButton girisyap;
    TextView kaydol;
    FirebaseAuth yetki;
    FirebaseUser user;
    String email,parola;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_sayfa);

        //Firebase başlatma
        yetki=FirebaseAuth.getInstance();
        user=yetki.getCurrentUser();
        mail=findViewById(R.id.edtmail);
        sifre=findViewById(R.id.edtsifre);
        /* SharedPreferences preferences=getSharedPreferences("checkbox",MODE_PRIVATE);
        String checkbox=preferences.getString("remember","");

        if(checkbox.equals("true")){
            startActivity(new Intent(LoginSayfa.this,MainActivity.class));
            finishAffinity();
            finish();
        }
        else if(checkbox.equals("false")){
            Toast.makeText(this, "Lütfen giriş yapın.", Toast.LENGTH_SHORT).show();
        }
*/
        girisyap=findViewById(R.id.btngirisyap);
        kaydol=findViewById(R.id.txtkaydol);
        kaydol.setOnClickListener(v ->{
            startActivity(new Intent(LoginSayfa.this,KayitSayfa.class));
        });
        girisyap.setOnClickListener(v -> {
            email=mail.getText().toString();
            parola=sifre.getText().toString();
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
                giris();
            }
        });
    }



    private void giris() {
        yetki.signInWithEmailAndPassword(email,parola).addOnCompleteListener(girisgorev -> {
            if(girisgorev.isSuccessful()){
                Toast.makeText(this, "Giriş başarılı.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginSayfa.this,MainActivity.class));
                finishAffinity();
                finish();
            }
        }).addOnFailureListener(hata -> {
            Toast.makeText(this, "E-mail veya şifre yanlış", Toast.LENGTH_SHORT).show();
        });
    }


    @Override
    public void onBackPressed() {
        finishAffinity();
        finish();
    }

    @Override
    protected void onStart() {
        if (user!=null){
            Intent a=new Intent(LoginSayfa.this,MainActivity.class);
            startActivity(a);
            finishAffinity();
            finish();
        }
        super.onStart();
    }
}