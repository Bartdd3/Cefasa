package com.arrobatecinformatica.cefasa;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.arrobatecinformatica.cefasa.domain.User;
import com.facebook.CallbackManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

public class LinkAccountsActivity extends CommonActivity
        implements GoogleApiClient.OnConnectionFailedListener, DatabaseReference.CompletionListener {

    private FirebaseAuth mAuth;

    private User user;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_accounts);


        mAuth = FirebaseAuth.getInstance();
        initViews();
        initUser();
    }


    private void accessEmailLoginData( String email, String password ){
        accessLoginData(
                "email",
                email,
                password
        );
    }

    private void accessLoginData(final String provider, String... tokens ){
        if( tokens != null
                && tokens.length > 0
                && tokens[0] != null ){

            AuthCredential credential = EmailAuthProvider.getCredential( tokens[0], tokens[1] );
//            credential = provider.equalsIgnoreCase("google") ? GoogleAuthProvider.getCredential( tokens[0], null) : credential;
//            credential = provider.equalsIgnoreCase("twitter") ? TwitterAuthProvider.getCredential( tokens[0], tokens[1] ) : credential;
//            credential = provider.equalsIgnoreCase("github") ? GithubAuthProvider.getCredential( tokens[0] ) : credential;
           // credential = provider.equalsIgnoreCase("email") ? EmailAuthProvider.getCredential( tokens[0], tokens[1] ) : credential;


            mAuth
                    .getCurrentUser()
                    .linkWithCredential( credential )
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            closeProgressBar();


                            if( !task.isSuccessful() ){
                                return;
                            }

                            initButtons();
                            showSnackbar("Conta provider "+provider+" vinculada com sucesso.");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            closeProgressBar();
                            //closeGitHubDialog();

                            showSnackbar("Error: "+e.getMessage());
                        }
                    });
        }
        else{
            mAuth.signOut();
        }
    }

    protected void initViews(){
        email = (AutoCompleteTextView) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.login_progress);

        initButtons();
    }


    private void initButtons() {

        setButtonLabel(
                R.id.email_sign_in_button,
                EmailAuthProvider.PROVIDER_ID,
                R.string.action_link,
                R.string.action_unlink,
                R.id.til_email,
                R.id.til_password
        );
    }

    private void setButtonLabel(
            int buttonId,
            String providerId,
            int linkId,
            int unlinkId,
            int... fieldsIds ){

        if( isALinkedProvider( providerId ) ){

            ((Button) findViewById( buttonId )).setText( getString( unlinkId ) );
            showHideFields( false, fieldsIds );
        }
        else{
            ((Button) findViewById( buttonId )).setText( getString( linkId ) );
            showHideFields( true, fieldsIds );
        }
    }

    private void showHideFields( boolean status, int... ids ){
        for( int id : ids ){
            findViewById( id ).setVisibility( status ? View.VISIBLE : View.GONE );
        }
    }

    private boolean isALinkedProvider( String providerId ){

        for(UserInfo userInfo : mAuth.getCurrentUser().getProviderData() ){

            if( userInfo.getProviderId().equals( providerId ) ){
                return true;
            }
        }
        return false;
    }

    protected void initUser(){
        user = new User();
        user.setEmail( email.getText().toString() );
        user.setPassword( password.getText().toString() );
    }

    public void sendLoginData( View view ){
        if( isALinkedProvider( EmailAuthProvider.PROVIDER_ID ) ){
            unlinkProvider( EmailAuthProvider.PROVIDER_ID );
            return;
        }

        openProgressBar();
        initUser();
        accessEmailLoginData( user.getEmail(), user.getPassword() );
    }


    @Override
    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

        mAuth.getCurrentUser().delete();
        mAuth.signOut();
        finish();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        showSnackbar( connectionResult.getErrorMessage() );

    }


    private void unlinkProvider(final String providerId ){

        mAuth
                .getCurrentUser()
                .unlink( providerId )
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if( !task.isSuccessful() ){
                            return;
                        }

                        initButtons();
                        showSnackbar("Conta provider "+providerId+" desvinculada com sucesso.");

                        if( isLastProvider( providerId ) ){
                            user.setId( mAuth.getCurrentUser().getUid() );
                            user.removeDB( LinkAccountsActivity.this );
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showSnackbar("Error: "+e.getMessage());
            }
        });


    }

    private boolean isLastProvider( String providerId ){
        int size = mAuth.getCurrentUser().getProviders().size();
        return(
                size == 0
                        || (size == 1 && providerId.equals(EmailAuthProvider.PROVIDER_ID) )
        );
    }
}
