package com.arrobatecinformatica.cefasa;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.arrobatecinformatica.cefasa.adapter.UserRecyclerAdapter;
import com.arrobatecinformatica.cefasa.adapter.UserViewHolder;
import com.arrobatecinformatica.cefasa.domain.User;
import com.arrobatecinformatica.cefasa.domain.Util.LibraryClass;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private UserRecyclerAdapter adapter;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if( firebaseAuth.getCurrentUser() == null  ){
                    Intent intent = new Intent( MainActivity.this, LoginActivity.class );
                    startActivity( intent );
                    finish();
                }
            }
        };

        mAuth = FirebaseAuth.getInstance();
        mAuth.addAuthStateListener( authStateListener );
        databaseReference = LibraryClass.getFirebase();
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    private void init(){
        RecyclerView rvUsers = (RecyclerView) findViewById(R.id.rv_users);
        rvUsers.setHasFixedSize( true );
        rvUsers.setLayoutManager( new LinearLayoutManager(this));
        adapter = new UserRecyclerAdapter(
                User.class,
                android.R.layout.two_line_list_item,
                UserViewHolder.class,
                databaseReference.child("users") );
        rvUsers.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter.cleanup();

        if( authStateListener != null ){
            mAuth.removeAuthStateListener( authStateListener );
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.action_update){
            startActivity(new Intent(this, UpdateActivity.class));
        }
        else if(id == R.id.action_update_login){
            startActivity(new Intent(this, UpdateLoginActivity.class));
        }
        else if(id == R.id.action_update_password){
            startActivity(new Intent(this, UpdatePasswordActivity.class));
        }
        else if(id == R.id.action_link_accounts){
            startActivity(new Intent(this, LinkAccountsActivity.class));
        }
        else if(id == R.id.action_remove_user){
            startActivity(new Intent(this, RemoveUserActivity.class));
        }
        else if(id == R.id.action_logout){
            FirebaseAuth.getInstance().signOut();
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

            FirebaseAuth.getInstance().signOut();
            finish();


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
