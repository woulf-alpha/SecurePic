package com.securepic.securepic.Adapters;

import android.content.Context;
import android.os.Environment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.securepic.securepic.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Toni Hernandez on 28/05/2017.
 */

public class AdapterRV  extends RecyclerView.Adapter<AdapterRV.ViewHolder>{

    ArrayList<String> files;
    Context context;

    public AdapterRV(ArrayList<String> files, Context context) {
        this.files = files;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder v, int position) {

        v.Nombre.setText(files.get(position));

        File f = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), files.get(position));
        if (f.exists()){
            v.icon.setImageResource(R.drawable.ic_local);
        }

        v.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("ADAPTER","Abriendo!");
            }
        });
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        ImageView icon;
        TextView Nombre;

        public ViewHolder(View v) {
            super(v);

            cv = (CardView) v.findViewById(R.id.cv);
            icon = (ImageView) v.findViewById(R.id.icon_item);
            Nombre = (TextView) v.findViewById(R.id.Nombre_item);


        }
    }
}
