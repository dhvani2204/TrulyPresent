package com.example.trulypresent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity1 extends AppCompatActivity {
FirebaseAuth auth;
Button button;
TextView textView;
FirebaseUser user;
FirebaseFirestore db =FirebaseFirestore.getInstance();
DocumentReference userRef;
RadioGroup optionGroup;
RadioButton option1,option2,option3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);
        auth = FirebaseAuth.getInstance();

        user = auth.getCurrentUser();
        textView = findViewById(R.id.welcometitle);
        button = findViewById(R.id.markbtn);
        option1 = findViewById(R.id.option1);
        option2 = findViewById(R.id.option2);
        option3 = findViewById(R.id.option3);
        CollectionReference subjectsCollection = db.collection("admins").document("6UwF6l2mMcdO2tJc788dQf8X8fI3").collection("126012");


        if (user != null) {
            userRef = db.collection("admins").document(user.getUid());
            userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        // Retrieve user data and update TextViews
                        String name = documentSnapshot.getString("facultyName");
                        String facultyId = documentSnapshot.getString("adminId");
                        String department = documentSnapshot.getString("department");

                        // Update TextViews with the retrieved data
                        textView.setText(name + "\nFaculty ID: " + facultyId + "\nDepartment: " + department);
                    }
                }
            });
            retrieveAndDisplaySubjectData(subjectsCollection, "subjects", option1);
            retrieveAndDisplaySubjectData(subjectsCollection, "Subject2", option2);
            retrieveAndDisplaySubjectData(subjectsCollection, "Subject3", option3);
        } else {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
        optionGroup = findViewById(R.id.optionGroup);
        // Set up a listener for the RadioGroup
        optionGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // Clear highlighting for all radio buttons
                for (int i = 0; i < group.getChildCount(); i++) {
                    RadioButton radioButton = (RadioButton) group.getChildAt(i);
                    radioButton.setBackgroundResource(android.R.color.transparent);
                }

                // Highlight the selected radio button
                RadioButton selectedRadioButton = findViewById(checkedId);
                selectedRadioButton.setBackgroundResource(R.drawable.highlight_selector);
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity2.class);
                startActivity(intent);
                finish();
            }
        });
    }
    private void retrieveAndDisplaySubjectData(CollectionReference subjectsCollection,String subjectId, RadioButton radioButton) {
      subjectsCollection
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
                                radioButton.setText(subjectName +"\n" + batch);
                            } else {
                                Toast.makeText(MainActivity1.this, "Subject data not found", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MainActivity1.this, "Error retrieving subject data", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}