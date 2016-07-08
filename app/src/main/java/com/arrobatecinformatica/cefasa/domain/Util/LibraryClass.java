package com.arrobatecinformatica.cefasa.domain.Util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Matrix on 31/05/2016.
 */
public class LibraryClass  {

    public static String PREF = "com.arrobatecinformatica.cefasa.PREF";
    private static DatabaseReference firebase;
    //construtor
    private LibraryClass(){}

    // instanciando o banco de dados
    public static DatabaseReference getFirebase(){
        if( firebase == null ){
            firebase = FirebaseDatabase.getInstance().getReference();
        }

        return( firebase );
    }

    static public void saveSP(Context context, String key, String value ){
        SharedPreferences sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        sp.edit().putString(key, value).apply();
    }

    static public String getSP(Context context, String key ){
        SharedPreferences sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        String token = sp.getString(key, "");
        return( token );
    }
}
