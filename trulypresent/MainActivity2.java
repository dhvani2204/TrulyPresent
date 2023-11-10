package com.example.trulypresent;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.BaseProgressIndicator;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;
import java.util.Map;
import android.content.Intent;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

public class MainActivity2 extends AppCompatActivity {
    FirebaseAuth auth;
    Button button1, button2, button3;
    TextView textView1;
    FirebaseUser user;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private Spinner spinn;
    ImageView selectedImageView;
    Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        auth = FirebaseAuth.getInstance();

        user = auth.getCurrentUser();
        textView1 = findViewById(R.id.welcometitle1);
        button1 = findViewById(R.id.CaptAtt);
        button2 = findViewById(R.id.UplAtt);
        button3 = findViewById(R.id.ViewAtt);
        spinn = findViewById(R.id.BrD);
        selectedImageView = findViewById(R.id.selectedImageView);
        ArrayAdapter<CharSequence> adap = ArrayAdapter.createFromResource(
                this,
                R.array.spinner_options3,
                android.R.layout.simple_spinner_item
        );

        adap.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinn.setAdapter(adap);
        CollectionReference subRef = db.collection("admins").document("6UwF6l2mMcdO2tJc788dQf8X8fI3").collection("126012");

        retrieveAndDisplaySubject(subRef, "subjects", textView1);

        getPermission();
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 12);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/+");
                startActivityForResult(intent, 10);
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ModelResults.class);
                startActivity(intent);
                finish();
            }
        });
    }
        void getPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity2.this, new String[]{Manifest.permission.CAMERA}, 11);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,@NonNull String[]permissions,@NonNull int[] grantResults) {
        if(requestCode==11){
           if(grantResults.length>0){
                if(grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    this.getPermission();
                }
           }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void retrieveAndDisplaySubject(CollectionReference subRef,String subjectId, TextView text) {
        subRef
                .document(subjectId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String subjectName = document.getString("Subject Name");
                                String batch = document.getString("Batch");
                                text.setText(subjectName +"\n" + batch);
                            } else {
                                Toast.makeText(MainActivity2.this, "Subject data not found", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MainActivity2.this, "Error retrieving subject data", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    if (requestCode == 10) {
        if (data != null) {
            Uri uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                selectedImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }else if(requestCode==12) {
        bitmap=(Bitmap) data.getExtras().get("data");
        selectedImageView.setImageBitmap(bitmap);
    }
        super.onActivityResult(requestCode, resultCode, data);
    }
}