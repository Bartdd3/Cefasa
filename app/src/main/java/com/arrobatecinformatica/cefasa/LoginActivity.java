package com.arrobatecinformatica.cefasa;

import android.content.Intent;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.arrobatecinformatica.cefasa.domain.User;
import com.facebook.CallbackManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crash.FirebaseCrash;

public class LoginActivity extends CommonActivity {

    private static final int RC_SIGN_IN_GOOGLE = 7859;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private User user;
    private CallbackManager callbackManager;

    private static final String TAG = LoginActivity.class.getSimpleName() ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = getFirebaseAuthResultHandler();
        initViews();
        initUser();

    }

    @Override
    protected void onStart() {
        super.onStart();
        verifyLogged();
    }


    @Override
    protected void onStop() {
        super.onStop();
        if( mAuthListener != null ){
            mAuth.removeAuthStateListener( mAuthListener );
        }
    }

    public void callSignUp(View view){
        Intent intent = new Intent( this, SignUpActivity.class );
        startActivity(intent);
    }

    public void sendLoginData( View view ){
        openProgressBar();
        initUser();
        verifyLogin();
    }

    private void callMainActivity(){
        Intent intent = new Intent( this, MainActivity.class );
        startActivity(intent);
        finish();
    }

    private void verifyLogged(){
        if( mAuth.getCurrentUser() != null ){
            callMainActivity();
        }
        else{
            mAuth.addAuthStateListener( mAuthListener );
        }
    }

    private void verifyLogin(){
        user.saveProviderSP( LoginActivity.this, "" );
        mAuth.signInWithEmailAndPassword(
                user.getEmail(),
                user.getPassword()
        )
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if( !task.isSuccessful() ){
                            showSnackbar("Login falhou");
                            return;
                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




    public void crash(View view){
        FirebaseCrash.log("clicou no botão e deu erro");
        FirebaseCrash.logcat(Log.INFO,TAG,"Iniciando depuração");

        try{ throw  new RuntimeException("Ocorreu um erro ao tentar recuperar os dados ");

        }catch (RuntimeException e){
            FirebaseCrash.report(e);
        }


    }

    private FirebaseAuth.AuthStateListener getFirebaseAuthResultHandler(){
        FirebaseAuth.AuthStateListener callback = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser userFirebase = firebaseAuth.getCurrentUser();

                if( userFirebase != null
                        && user.getId() == null
                        && isNameOk( user, userFirebase ) ){

                    user.saveIdSP( LoginActivity.this, userFirebase.getUid() );
                    user.setId( userFirebase.getUid() );
                    user.setNameIfNull( userFirebase.getDisplayName() );
                    user.setEmailIfNull( userFirebase.getEmail() );
                    user.saveDB();

                   /* callMainActivity();*/
                    Intent intent = new Intent( LoginActivity.this, MainActivity.class );
                    startActivity(intent);
                }
            }
        };
        return( callback );
    }

    private boolean isNameOk( User user, FirebaseUser firebaseUser ){
        return(
                user.getName() != null
                        || firebaseUser.getDisplayName() != null
        );
    }

    protected void initViews(){
        email = (AutoCompleteTextView) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.login_progress);
    }

    protected void initUser(){
        user = new User();
        user.setEmail( email.getText().toString() );
        user.setPassword( password.getText().toString() );
        user.generateCryptPassword();
    }


}
