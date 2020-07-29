package com.example.medreports;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class FirstFragment extends Fragment {
    public static String a;
    FirebaseAuth fAuth=FirebaseAuth.getInstance();
    FirebaseFirestore fstore=FirebaseFirestore.getInstance();
    String userid=null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_first, container, false);
    //    return inflater.inflate(R.layout.fragment_first, container, false);

        final TextView email=root.findViewById(R.id.email);
        final TextView emailh=root.findViewById(R.id.emailh);
        final TextView phone=root.findViewById(R.id.phone);
        final TextView phoneh=root.findViewById(R.id.phoneh);
        final TextView nameh=root.findViewById(R.id.nameh);
        final TextView name=root.findViewById(R.id.fname);
        final TextView gender=root.findViewById(R.id.gender);
        final TextView genderh=root.findViewById(R.id.genderh);
        final Button logout=root.findViewById(R.id.logout);
        final ImageView im=root.findViewById(R.id.imageView2);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), Login.class));
            }
        });

        userid=fAuth.getCurrentUser().getUid();
        DocumentReference documentReference = fstore.collection("patient").document(userid);

        nameh.setText("Name:");
        emailh.setText("Email:");
        phoneh.setText("Phone:");
        genderh.setText("Gender");

        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.d(TAG, "Error");
                } else {
                    //name.setText(documentSnapshot.getString("name"));
                    a=documentSnapshot.getString("name");
                    name.setText(a);

                    email.setText(documentSnapshot.getString("email"));
                    phone.setText(documentSnapshot.getString("phone"));
                    gender.setText(documentSnapshot.getString("gender"));
                }
            }
        });


        return root;

    }
}
