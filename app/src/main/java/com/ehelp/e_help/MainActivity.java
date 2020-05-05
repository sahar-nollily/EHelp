package com.ehelp.e_help;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.ayush.imagesteganographylibrary.Text.AsyncTaskCallback.TextDecodingCallback;
import com.ayush.imagesteganographylibrary.Text.ImageSteganography;
import com.ayush.imagesteganographylibrary.Text.TextDecoding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements TextDecodingCallback, View.OnClickListener {

    Button Enter , decode, Scanner ;
    public static EditText user_id ;
    String ID ,Request_ID;
    ImageView Permission ;
    public static TextView request_id ;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Enter = (Button) findViewById(R.id.Enter);
        decode = (Button) findViewById(R.id.verify);
        Permission = (ImageView) findViewById(R.id.Permission);
        Scanner = (Button) findViewById(R.id.Scanner);
        request_id = findViewById(R.id.request_id);

        user_id = (EditText) findViewById(R.id.user_id);

        decode.setOnClickListener(this);
        Enter.setOnClickListener(this);
        Scanner.setOnClickListener(this);


    }

    void decode_img(){

        BitmapDrawable drawable = (BitmapDrawable) Permission.getDrawable();
        Bitmap bitmap_Permission = drawable.getBitmap();
        //Making the ImageSteganography object
        ImageSteganography imageSteganography = new ImageSteganography("11111",
                bitmap_Permission);

        //Making the TextDecoding object
        TextDecoding textDecoding = new TextDecoding(MainActivity.this, MainActivity.this);

        //Execute Task
        textDecoding.execute(imageSteganography);
    }

    void Save_img(){

        final ProgressDialog loading = ProgressDialog.show(MainActivity.this, "Loading Image", "Please Wait", false, false);

        ID = user_id.getText().toString();
        Request_ID = request_id.getText().toString();

        if(ID.isEmpty()){
            loading.dismiss();
            user_id.setError("الرجاء تعبئة الحقل");
        }else {

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageReference = storage.getReferenceFromUrl("gs://e-help-7a06a.appspot.com").child("img/").child(Request_ID+".PNG");

            try {

                final File file = File.createTempFile("image", "PNG");
                storageReference.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                        Permission.setImageBitmap(bitmap);
                        decode.setVisibility(View.VISIBLE);
                        loading.dismiss();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loading.dismiss();
                        Toast.makeText(MainActivity.this, "Image field to load", Toast.LENGTH_SHORT).show();



                    }
                });


            }
            catch (IOException e){
                loading.dismiss();
                e.printStackTrace();

            }

        }
    }


    @Override
    public void onStartTextEncoding() {
        //Whatever you want to do by the start of textDecoding
    }

    @Override
    public void onCompleteTextEncoding(ImageSteganography result) {

        //By the end of textDecoding

        if (result != null) {
            if (!result.isDecoded())
                Toast.makeText(MainActivity.this , "No message found", Toast.LENGTH_LONG).show();

            else {
                if (!result.isSecretKeyWrong()) {
                    Toast.makeText(MainActivity.this , "successfully verified", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(MainActivity.this , "Wrong secret key", Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    @Override
    public void onClick(View v) {
        if(v== decode){
            decode_img();
        }
        if (v== Enter){

            Save_img();
        }
        if(v == Scanner){
            startActivity(new Intent(getApplicationContext(), Scanner.class));
        }
    }
}





