package com.securepic.securepic.Fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.cameraview.CameraView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.securepic.securepic.Helpers.Cifrado;
import com.securepic.securepic.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


public class Captura extends Fragment {


    private int mCurrentFlash;
    private CameraView mCameraView;
    private Handler mBackgroundHandler;

    FirebaseDatabase database = FirebaseDatabase.getInstance();


    private static String SHA1;


    public String log = "";

    public Captura() {
    }


    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mCameraView != null){
                mCameraView.takePicture();
            }
        }
    };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_captura, container, false);
        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.take_picture);
        if (fab != null) {
            fab.setOnClickListener(mOnClickListener);
        }
        mCameraView = (CameraView) v.findViewById(R.id.camera);

        if (mCameraView != null) {
            mCameraView.addCallback(mCallback);
        }


        return v;
    }

    public static Captura newInstance(String sha1) {
        Captura fragment = new Captura();
        SHA1 = sha1;
        return fragment;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void onPause() {
        mCameraView.stop();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mCameraView.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBackgroundHandler != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                mBackgroundHandler.getLooper().quitSafely();
            } else {
                mBackgroundHandler.getLooper().quit();
            }
            mBackgroundHandler = null;
        }
    }

    private Handler getBackgroundHandler() {
        if (mBackgroundHandler == null) {
            HandlerThread thread = new HandlerThread("background");
            thread.start();
            mBackgroundHandler = new Handler(thread.getLooper());
        }
        return mBackgroundHandler;
    }

    private StorageReference mStorageRef;

    ProgressDialog progress;

    FirebaseStorage storage = FirebaseStorage.getInstance();

    private CameraView.Callback mCallback = new CameraView.Callback() {
        @Override
        public void onCameraOpened(CameraView cameraView) {
            super.onCameraOpened(cameraView);

        }

        @Override
        public void onCameraClosed(CameraView cameraView) {
            super.onCameraClosed(cameraView);
        }

        @Override
        public void onPictureTaken(final CameraView cameraView, final byte[] data) {
            super.onPictureTaken(cameraView, data);


            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4;

            Bitmap b = BitmapFactory.decodeByteArray(data, 0, data.length, options);


            final AlertDialog diag = new AlertDialog.Builder(getContext())
                    .setTitle("Preview")
                    .setView(R.layout.sample_preview)
                    .create();

            diag.show();


            ImageView pre = (ImageView) diag.findViewById(R.id.Preview);
            final EditText Name = (EditText) diag.findViewById(R.id.Nombre);
            Button Cancel = (Button) diag.findViewById(R.id.Cancel);
            Button Aceptar = (Button) diag.findViewById(R.id.Aceptar);
            pre.setImageBitmap(b);



            Cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    diag.dismiss();
                }
            });

            Aceptar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    diag.dismiss();
                    log = "Cifrando ... ";
                    getBackgroundHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            progress = ProgressDialog.show(getContext(), "","Espere un momento", true);

                        }
                    });
                    byte[] cifrado = new Cifrado().Cifrar(data, SHA1.substring(0,32).getBytes(), FirebaseAuth.getInstance().getCurrentUser().getUid().substring(0,16).getBytes());

                    SaveFile(cifrado, Name.getText().toString());

                    Upload(cifrado, Name.getText().toString());
                }
            });


        }
    };

    private void Upload(byte[] cifrado, final String nombre) {
        mStorageRef = storage.getReferenceFromUrl("gs://securepic-2c5a2.appspot.com");

        FirebaseUser u = FirebaseAuth.getInstance().getCurrentUser();
        StorageReference mountainImagesRef = mStorageRef.child("/photos/" + u.getUid() + "/"+ nombre +".dat");

        UploadTask uploadTask = mountainImagesRef.putBytes(cifrado);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Snackbar.make(getView(), "Error en la subida",Snackbar.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                DatabaseReference ref = database.getReference();

                ref.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(nombre).setValue("");

                progress.dismiss();


            }
        });
    }

    private void SaveFile(byte[] c, String nombre) {
        File file = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                nombre);
        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
            os.write(c);
            os.close();
        } catch (IOException e) {

        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
        }
    }

}
