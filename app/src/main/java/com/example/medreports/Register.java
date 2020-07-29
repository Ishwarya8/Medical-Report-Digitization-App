package com.example.medreports;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
public class Register extends AppCompatActivity {

    int i=1;
    EditText name,email,pass,cpass,phone;
    TextView log;
    Button reg;
    RadioGroup radioGroup;
    RadioButton radioButton;
    ProgressBar p;
    FirebaseAuth fAuth;
    FirebaseFirestore fstore;
    String userID,pat_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        name=(EditText) findViewById(R.id.name);
        email=(EditText) findViewById(R.id.email);
        pass=(EditText) findViewById(R.id.password);
        cpass=(EditText) findViewById(R.id.cpass);
        phone=(EditText) findViewById(R.id.phone);
        reg=(Button)findViewById(R.id.register);
        radioGroup=(RadioGroup)findViewById(R.id.rg);
        p=(ProgressBar)findViewById(R.id.progressBar);
        log=(TextView)findViewById(R.id.login2);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        p.setVisibility(View.INVISIBLE);
        fAuth=FirebaseAuth.getInstance();
        fstore=FirebaseFirestore.getInstance();
        reg.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onClick(View v) {
                final String fname=name.getText().toString().trim();
                final String mailid=email.getText().toString().trim();
                String pas=pass.getText().toString().trim();
                String conf=cpass.getText().toString().trim();
                final String ph=phone.getText().toString().trim();

                if(fname.length()==0){
                    name.setError("Enter name");
                    return;
                }
                else if(mailid.length()==0){
                    email.setError("Enter email");
                    return;
                }
                else if(pas.length()==0){
                    pass.setError("Enter password");
                    return;
                }
                else if(conf.length()==0){
                    cpass.setError("Re type password");
                    return;
                }
                else if(ph.length()==0){
                    phone.setError("Enter phone number");
                    return;
                }
                if((phone.getText().toString()).length()<10 || (phone.getText().toString()).length()>10){
                    phone.setError("Error Enter a valid phone number!!");
                    return;
                }
                else if(pas.length()<8){
                    pass.setError("Password must be atleast 8 characters");
                    return;
                }
                else if(!(pas.equals(conf))){
                    Toast.makeText(getApplicationContext(), "Password don't match", Toast.LENGTH_SHORT).show();
                    cpass.setText("");
                }
                p.setVisibility(View.VISIBLE);
                int selectedId = radioGroup.getCheckedRadioButtonId();

                radioButton = (RadioButton) findViewById(selectedId);
                final String s = radioButton.getText().toString();
         //       Toast.makeText(getApplicationContext(),"Gender: "+s,Toast.LENGTH_LONG).show();
                if(selectedId==-1)
                {
                    Toast.makeText(getApplicationContext(),"Please select a Gender",Toast.LENGTH_LONG).show();
                }
                else {
                    fAuth.createUserWithEmailAndPassword(mailid, pas).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(Register.this, "User registered", Toast.LENGTH_LONG).show();
                                userID = fAuth.getCurrentUser().getUid();

                                Map<String,Object> user=new HashMap<>();
                                user.put("name",fname);
                                user.put("email",mailid);
                                user.put("phone",ph);
                                user.put("gender",s);
                                DocumentReference documentReference=fstore.collection("patient").document(userID);

                                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("TAG","onSuccess:user profile is created for "+userID);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("TAG","onFailure: "+e.toString());
                                    }
                                });
                                //startActivity(new Intent(getApplicationContext(),home.class));
                                Intent i = new Intent(getApplicationContext(),Login.class);
                                startActivity(i);
                                p.setVisibility(View.GONE);
                            }
                            else {
                                Toast.makeText(Register.this,"Error",Toast.LENGTH_LONG).show();
                                p.setVisibility(View.GONE);
                            }
                        }

                    });
                }
            }
        });
        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Login.class));
            }
        });
    }
    public void checkButton(View v)
    {
        int radioID = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(radioID);
     //   Toast.makeText(this,"Selected Choice: " + radioButton.getText(), Toast.LENGTH_SHORT).show();
    }
    public void clearText()
    {
        email.setText("");
        pass.setText("");
        name.setText("");
        phone.setText("");
        cpass.setText("");
        radioButton.toggle();
    }
}
