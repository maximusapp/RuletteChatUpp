package com.zender.piramidaupp;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserGroupActivity extends AppCompatActivity {

    private RecyclerView mUsersGroupList;

    private DatabaseReference mUsersGroupDatabase;

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_group);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Добавить в группу юзеров");

        mUsersGroupDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mUsersGroupList = findViewById(R.id.users_group);
        mUsersGroupList.setHasFixedSize(true);
        mUsersGroupList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<UsersGroup, UsersViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<UsersGroup, UsersViewHolder>(

                UsersGroup.class,
                R.layout.user_group_layout,
                UsersViewHolder.class,
                mUsersGroupDatabase
        ) {
            @Override
            protected void populateViewHolder(UsersViewHolder usergroupviewHolder, UsersGroup usersGroup, int position) {

                usergroupviewHolder.setDisplayName(usersGroup.getName());
                usergroupviewHolder.setUserStatus(usersGroup.getStatus());
                usergroupviewHolder.setUserImage(usersGroup.getThumb_image(), getApplicationContext());

            }
        };

        mUsersGroupList.setAdapter(firebaseRecyclerAdapter);

    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public UsersViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setDisplayName(String name) {

            TextView userNameView = (TextView)mView.findViewById(R.id.user_single_name_group);
            userNameView.setText(name);

        }

        public void setUserStatus(String status) {

            TextView userStatusView = (TextView)mView.findViewById(R.id.user_single_status_group);
            userStatusView.setText(status);
        }

        public void setUserImage(String thumb_image, Context ctx){

            CircleImageView userImageView = (CircleImageView)mView.findViewById(R.id.user_single_image_group);

            Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.chat_user_avatar).into(userImageView);

        }


    }
}
