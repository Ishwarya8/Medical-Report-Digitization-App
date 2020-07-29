package com.example.medreports;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
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
import org.w3c.dom.Text;
import java.util.Random;

public class Login extends AppCompatActivity {
    TextView reg,fg;
    EditText email,pass;
    RadioGroup radioGroup;
    RadioButton radioButton;
    Button log;
    FirebaseAuth fAuth;
    ProgressBar p;
    SQLiteDatabase db;
    String pat_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        reg=(TextView)findViewById(R.id.register);
        fg=(TextView)findViewById(R.id.forgot);
        email=(EditText)findViewById(R.id.emailid2);
        pass=(EditText)findViewById(R.id.password2);
        log=(Button)findViewById(R.id.login);
        p=(ProgressBar)findViewById(R.id.progressBar2);
        radioGroup = findViewById(R.id.rg3);
        p.setVisibility(View.INVISIBLE);
        fAuth=FirebaseAuth.getInstance();

        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Login.this,Register.class);
                startActivity(intent);
            }
        });

        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mailid=email.getText().toString().trim();
                String password=pass.getText().toString().trim();
                if(TextUtils.isEmpty(mailid)){
                    email.setError("Email is required");
                    clearText();
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    pass.setError("Password is required");
                    clearText();
                    return;
                }
                if(password.length()<8){
                    pass.setError("Invalid Password");
                    clearText();
                    return;
                }
                p.setVisibility(View.VISIBLE);
                int radioID = radioGroup.getCheckedRadioButtonId();
                radioButton = findViewById(radioID);
                String s = radioButton.getText().toString();

                if(s.equals("Admin") && mailid.equals("admin@med.com")&&password.equals("admin1234")){
                    Intent h=new Intent(Login.this,Admin.class);
                    startActivity(h);
                    clearText();
                }

                else {
                    fAuth.signInWithEmailAndPassword(mailid, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @SuppressLint("DefaultLocale")
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(Login.this, "Successful Login", Toast.LENGTH_LONG).show();

                                Intent i = new Intent(getApplicationContext(), Home.class);
                                startActivity(i);
                                clearText();
                                p.setVisibility(View.GONE);
                            } else {
                                Toast.makeText(getApplicationContext(), "Invalid Email Or Password", Toast.LENGTH_LONG).show();
                                p.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }
        });
        fg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText resetMail=new EditText(view.getContext());
                final AlertDialog.Builder passwordDialog=new AlertDialog.Builder(view.getContext());
                passwordDialog.setTitle("Reset Password");
                passwordDialog.setMessage("Enter Mail ID To Receive Password Reset Link");
                passwordDialog.setView(resetMail);
                passwordDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String mail=resetMail.getText().toString();
                        fAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getApplicationContext(),"Reset link successfully sent to mail",Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(),"Reset link unable to send",Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
                passwordDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                passwordDialog.create().show();
            }
        });
    }
    public void clearText() {
        email.setText("");
        pass.setText("");
    }

    public void showMessage(String title,String message)
    {
        android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }

}
