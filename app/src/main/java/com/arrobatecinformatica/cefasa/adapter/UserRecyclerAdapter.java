package com.arrobatecinformatica.cefasa.adapter;

import com.arrobatecinformatica.cefasa.domain.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;

import com.google.firebase.database.Query;

/**
 * Created by Matrix on 01/06/2016.
 */
public class UserRecyclerAdapter extends FirebaseRecyclerAdapter<User, UserViewHolder> {
    public UserRecyclerAdapter(
            Class<User> modelClass,
            int modelLayout,
            Class<UserViewHolder> viewHolderClass,
            Query ref ){
        super( modelClass, modelLayout, viewHolderClass, ref );
    }
    @Override
    protected void populateViewHolder(
            UserViewHolder userViewHolder,
            User user, int i) {
        userViewHolder.text1.setText( user.getName() );
        userViewHolder.text2.setText( user.getEmail() );
    }
}
