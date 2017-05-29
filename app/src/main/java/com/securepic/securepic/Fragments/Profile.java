package com.securepic.securepic.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.securepic.securepic.R;
import com.squareup.picasso.Picasso;


public class Profile extends Fragment {


    private ImageView Avatar;
    private EditText Nombre;
    private EditText Email;
    private TextView Registro;
    private Button logout;

    public Profile() {
        // Required empty public constructor
    }

    public static Profile newInstance() {
        Profile fragment = new Profile();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v =  inflater.inflate(R.layout.fragment_profile, container, false);
        Avatar = (ImageView) v.findViewById(R.id.Avatar);
        Nombre = (EditText) v.findViewById(R.id.Nombre);

        Email = (EditText) v.findViewById(R.id.Email);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Picasso.with(getContext()).load(user.getPhotoUrl()).into(Avatar);


        Email.setText(user.getEmail());
        Nombre.setText(user.getDisplayName());


        logout = (Button) v.findViewById(R.id.Logout);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getContext(), Login.class));
                getActivity().finish();
            }
        });
        return v;
    }


}
