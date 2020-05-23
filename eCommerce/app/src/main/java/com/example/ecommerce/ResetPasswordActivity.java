package com.example.ecommerce;


import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ecommerce.Model.Users;
import com.example.ecommerce.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ResetPasswordActivity extends AppCompatActivity
{
    private String check = "";
    private TextView pageTitle, titleQuestions;
    private EditText phoneNumber, question1 , question2;
    private Button verifyButton;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);


        check = getIntent().getStringExtra("check");

        pageTitle = findViewById(R.id.pageTitle);
        titleQuestions = findViewById(R.id.title_questions);
        phoneNumber = findViewById(R.id.find_phone_number);
        question1 = findViewById(R.id.question_1);
        question2 = findViewById(R.id.question_2);
        verifyButton = findViewById(R.id.verify_btn);
    }



    @Override
    protected void onStart()
    {
        super.onStart();
        phoneNumber.setVisibility(View.GONE);


        if (check.equals("settings"))
        {
                pageTitle.setText("Set Questions");
                titleQuestions.setText("Please set the answers !! ");
                verifyButton.setText("Set");
                displayPreviousAnsers();

                verifyButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String answer1 = question1.getText().toString().toLowerCase();
                        String answer2 =question2.getText().toString().toLowerCase();

                        if(question1.equals("") && question2.equals(""))
                        {
                            Toast.makeText(ResetPasswordActivity.this, "Please answer both questions", Toast.LENGTH_SHORT).show();

                        }else {
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                                    .child("Users")
                                    .child(Prevalent.currentOnlineUser.getPhone());

                            HashMap<String, Object> userdataMap = new HashMap<>();
                            userdataMap.put("answer1", answer1);
                            userdataMap.put("answer2", answer2);

                            ref.child("Security Questions").updateChildren(userdataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()){

                                            Toast.makeText(ResetPasswordActivity.this, "you have answered security questions successfully", Toast.LENGTH_SHORT).show();
                                        }
                                }
                            });
                        }
                    }
                });
        }
        else if (check.equals("login"))
        {
            phoneNumber.setVisibility(View.VISIBLE);

            verifyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String phone = phoneNumber.getText().toString();
                    String answer1 = question1.getText().toString().toLowerCase();
                    final String answer2 = question2.getText().toString().toLowerCase();

                    DatabaseReference ref = FirebaseDatabase.getInstance()
                            .getReference()
                            .child("Users")
                            .child(Prevalent.currentOnlineUser.getPhone());


                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {



                            if(dataSnapshot.exists()) {
                                String ans1 = dataSnapshot.child("answer1").getValue().toString();
                                String ans2 = dataSnapshot.child("answer2").getValue().toString();

                                question1.setText(ans1);
                                question2.setText(ans2);
                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            });

        }
    }


    private void displayPreviousAnsers(){

        final String phone = phoneNumber.getText().toString();
        final String answer1 =question2.getText().toString().toLowerCase();
        final String answer2 =question2.getText().toString().toLowerCase();


        final DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference()
                .child("Users")
                .child(Prevalent.currentOnlineUser.getPhone());

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String mPhone = dataSnapshot.child("phone").getValue().toString();

                    if(phone.equals(mPhone) || phone.equals(Prevalent.currentOnlineUser.getPhone())){


                        if(dataSnapshot.hasChild("Security Questions")){
                            String ans1 = dataSnapshot.child("answer1").getValue().toString();
                            String ans2 = dataSnapshot.child("answer2").getValue().toString();

                            if(!ans1.equals(answer1)){
                                Toast.makeText(ResetPasswordActivity.this, "your 1st answer is wrong", Toast.LENGTH_SHORT).show();

                            }
                            else if(!ans2.equals(answer2)){
                                Toast.makeText(ResetPasswordActivity.this, "your 2nd answer is wrong", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(ResetPasswordActivity.this);
                                builder.setTitle("New Password");

                                final EditText newPassword = new EditText(ResetPasswordActivity.this);
                                newPassword.setHint("Write new password her ....");
                                builder.setView(newPassword);

                                builder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        if(!newPassword.getText().toString().equals("")){

                                            ref.child("password")
                                                    .setValue(newPassword.getText().toString())
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task)
                                                        {
                                                            if(task.isSuccessful()){
                                                                Toast.makeText(ResetPasswordActivity.this, "Password changes successfully", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });

                                        }
                                    }
                                });
                            }
                        }
                    }
                    else
                    {
                        Toast.makeText(ResetPasswordActivity.this, "This user phone number not exist", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}