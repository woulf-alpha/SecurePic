package com.securepic.securepic.Fragments;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.securepic.securepic.Adapters.AdapterRV;
import com.securepic.securepic.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Dashboard extends Fragment {


    private static String SHA1;
    RecyclerView Rv;
    AdapterRV adapterRV;
    LinearLayoutManager linearLayoutManager;
    ArrayList<String> files;

    public Dashboard() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        files = new ArrayList<String>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        ref.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot f: dataSnapshot.getChildren()) {
                    files.add(f.getKey());
                }

                adapterRV.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        View v = inflater.inflate(R.layout.fragment_dashboard, container, false);

        linearLayoutManager = new LinearLayoutManager(getContext());

        Rv = (RecyclerView) v.findViewById(R.id.RV);

        Rv.setLayoutManager(linearLayoutManager);
        adapterRV = new AdapterRV(files, getContext());

        Rv.setAdapter(adapterRV);

        return v;
    }




    public static Dashboard newInstance(String iv) {
        Dashboard fragment = new Dashboard();
        SHA1 = iv;
        return fragment;
    }


}
