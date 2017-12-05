package com.zender.piramidaupp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginPageActivity extends AppCompatActivity {

    Button enterChat;
    Button regBtn;

    private EditText inputEmail;
    private EditText inputPassword;

    private FirebaseAuth auth;

    private DatabaseReference mUserDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        inputEmail = (EditText)findViewById(R.id.enterEmail);
        inputPassword = (EditText)findViewById(R.id.enterPassword);

        regBtn = (Button)findViewById(R.id.regist_btn);
        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intRegist = new Intent(LoginPageActivity.this, RegisterActivity.class);
                startActivity(intRegist);
            }
        });

        enterChat = (Button)findViewById(R.id.enter_in_chat_button);
        enterChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               String email = inputEmail.getText().toString().trim();
               String password = inputPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Введи данные", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Введи данные", Toast.LENGTH_SHORT).show();
                }else {
                    auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginPageActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    final FirebaseUser user = auth.getCurrentUser();

                                    if (task.isSuccessful()) {

                                        String current_user_id = auth.getCurrentUser().getUid();
                                        String deviceToken = FirebaseInstanceId.getInstance().getToken();

                                        mUserDatabase.child(current_user_id).child("device_token").setValue(deviceToken)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                    startActivity(new Intent(getApplicationContext(), ChatActivity.class));

                                            }
                                        });

                                        FirebaseUser users = FirebaseAuth.getInstance().getCurrentUser();
                                        if (users != null) {
                                            //noinspection ConstantConditions
                                            Toast.makeText(LoginPageActivity.this, "Welcome " + " " +
                                                            FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                                                    Toast.LENGTH_LONG).show();
                                        }

                                    } else {
                                        //display some message here
                                        Toast.makeText(LoginPageActivity.this, "Ошибка входа", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }

        });

    }
}