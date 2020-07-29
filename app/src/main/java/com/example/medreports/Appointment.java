package com.example.medreports;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Appointment extends AppCompatActivity implements OnItemSelectedListener {
    TextView editText;
    DatePickerDialog.OnDateSetListener setListener;
    private Spinner sp;
    CheckBox dia,chol,thy;
    Button buttonOrder;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    String slo;
    FirebaseAuth fAuth=FirebaseAuth.getInstance();
    FirebaseFirestore fstore=FirebaseFirestore.getInstance();
    String userid=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);

        FirstFragment f=new FirstFragment();
        final String i = f.a;

        dia=(CheckBox)findViewById(R.id.checkBox);
        chol=(CheckBox)findViewById(R.id.checkBox2);
        thy=(CheckBox)findViewById(R.id.checkBox3);
        buttonOrder=(Button)findViewById(R.id.button);

        editText = (TextView) findViewById(R.id.etApp);
        sp=findViewById(R.id.Spinnerid);
        sp.setOnItemSelectedListener(this);

        List<String> categories = new ArrayList<String>();

        categories.add("09:00 A.M");
        categories.add("10:00 A.M");
        categories.add("11:00 A.M");
        categories.add("04:00 P.M");
        categories.add("05:00 P.M");
        categories.add("06:00 P.M");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sp.setAdapter(dataAdapter);

        Calendar cal = Calendar.getInstance();
        final int year = cal.get(Calendar.YEAR);
        final int month = cal.get(Calendar.MONTH);
        final int day = cal.get(Calendar.DAY_OF_MONTH);

        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        Appointment.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                final String date = month + "/" + day + "/" + year;
                editText.setText(date);
            }
        };

        buttonOrder.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if(editText.getText().toString().length()==0){
                    editText.setError("Date is required");
                    return;
                }
                int totalamount=0;
                StringBuilder result=new StringBuilder();
                result.append("Selected Items:");
                if(dia.isChecked()){
                    result.append("\nDiabetes 500Rs");
                    totalamount+=500;
                }
                if(chol.isChecked()){
                    result.append("\nCholestrol 350Rs");
                    totalamount+=350;
                }
                if(thy.isChecked()){
                    result.append("\nThyroid 470Rs");
                    totalamount+=470;
                }
                int selectedItemOfMySpinner = sp.getSelectedItemPosition();
                String actualPositionOfMySpinner = (String) sp.getItemAtPosition(selectedItemOfMySpinner);

                if (actualPositionOfMySpinner.isEmpty()) {
                    setSpinnerError(sp,"Slot must be selected");
                }
                result.append("\nTotal Bill Amount: "+totalamount+"Rs");
                //Displaying the message on the toast
                Toast.makeText(getApplicationContext(), result.toString(), Toast.LENGTH_LONG).show();
                slo=sp.getSelectedItem().toString();
            //    Toast.makeText(getApplicationContext(),"Slot is "+slo,Toast.LENGTH_LONG).show();
                userid=fAuth.getCurrentUser().getUid();
                DocumentReference documentReference = fstore.collection("appointment").document(userid);
                String apDate=editText.getText().toString();
                Map<String,Object> user=new HashMap<>();
                user.put("name",i);
                user.put("date",apDate);
                user.put("slot",slo);
                user.put("test",result.toString());

                final int finalTotalamount = totalamount;
                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TAG","onSuccess:user profile is created for "+userid);
                        Toast.makeText(getApplicationContext(), "Your Appointment is booked! Total bill amount is "+finalTotalamount, Toast.LENGTH_LONG).show();
                        Intent h=new Intent(Appointment.this,Home.class);
                        startActivity(h);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("TAG","onFailure: "+e.toString());
                    }
                });
            }

        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    public void showMessage(String title,String message)
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }
    private void setSpinnerError(Spinner spinner, String error){
        View selectedView = spinner.getSelectedView();
        if (selectedView != null && selectedView instanceof TextView) {
            spinner.requestFocus();
            TextView selectedTextView = (TextView) selectedView;
            selectedTextView.setError("error"); // any name of the error will do
            selectedTextView.setTextColor(Color.RED); //text color in which you want your error message to be displayed
            selectedTextView.setText(error); // actual error message
            spinner.performClick(); // to open the spinner list if error is found.

        }
    }
}
