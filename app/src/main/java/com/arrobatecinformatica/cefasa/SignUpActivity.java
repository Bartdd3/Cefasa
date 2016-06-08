package com.arrobatecinformatica.cefasa;

import android.support.annotation.NonNull;

import android.os.Bundle;

import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ProgressBar;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import com.arrobatecinformatica.cefasa.domain.User;

public class SignUpActivity extends CommonActivity implements DatabaseReference.CompletionListener{

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private com.arrobatecinformatica.cefasa.domain.User user;
    private AutoCompleteTextView name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if( firebaseUser == null || user.getId() != null ){                    return;
                }

                user.setId( firebaseUser.getUid() );
                user.saveDB( SignUpActivity.this );
                // ...
            }
        };

        initViews();
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void initViews() {

        name = (AutoCompleteTextView) findViewById(R.id.name);
        email = (AutoCompleteTextView) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.sign_up_progress);

    }

    @Override
    protected void initUser() {

        user = new User();
        user.setName( name.getText().toString() );
        user.setEmail( email.getText().toString() );
        user.setPassword( password.getText().toString() );


    }

    public void sendSignUpData( View view ){
        openProgressBar();
        initUser();
        saveUser();
    }

    private void saveUser(){

        mAuth.createUserWithEmailAndPassword(
                user.getEmail(),
                user.getPassword()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if( !task.isSuccessful() ){
                    closeProgressBar();
                }
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showSnackbar( e.getMessage() );
            }
        });
    }

    @Override
    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
        mAuth.signOut();

        showToast( "Conta criada com sucesso!" );
        closeProgressBar();
        finish();

    }
}
