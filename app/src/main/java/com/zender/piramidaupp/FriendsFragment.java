package com.zender.piramidaupp;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {

    private RecyclerView mFriendsList;

    private DatabaseReference mUsersDatabase;
    private FirebaseAuth mAuth;

    private View mMainView;


    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMainView = inflater.inflate(R.layout.fragment_friends, container, false);

        mFriendsList = mMainView.findViewById(R.id.friends_list);
        mAuth = FirebaseAuth.getInstance();

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatabase.keepSynced(true);

        mFriendsList.setHasFixedSize(true);
        mFriendsList.setLayoutManager(new LinearLayoutManager(getContext()));


        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();


        final FirebaseRecyclerAdapter<Users, UsersActivity.UsersViewHolder> friendsRecyclerAdapter =
                new FirebaseRecyclerAdapter<Users, UsersActivity.UsersViewHolder>(

                Users.class,
                R.layout.users_single_layout,
                UsersActivity.UsersViewHolder.class,
                mUsersDatabase

        ) {
            @Override
            protected void populateViewHolder(final UsersActivity.UsersViewHolder usersViewHolder, Users users, int position) {

                final String list_user_id = getRef(position).getKey();


                mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final String userName = dataSnapshot.child("name").getValue().toString();
                        final String userStatus = dataSnapshot.child("status").getValue().toString();
                        String userThumb = dataSnapshot.child("thumb_image").getValue().toString();

                        usersViewHolder.setDisplayName(userName);
                        usersViewHolder.setUserStatus(userStatus);
                        usersViewHolder.setUserImage(userThumb, getContext());


                        // Слушатель на нажатие на юзера в списке контактов
                        usersViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                CharSequence options[] = new CharSequence[]{"Открыть профайл", "Отправить сообщение"};

                                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                                builder.setTitle("Выбрать опцию");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        if (i == 0) {

                                            Intent profileIntent = new Intent(getContext(), ProfileActivity.class);
                                            profileIntent.putExtra("user_id", list_user_id);
                                            startActivity(profileIntent);

                                        }

                                        if (i == 1) {

                                            Intent chatIntent = new Intent(getContext(), ConversationActivity.class);
                                            chatIntent.putExtra("user_id", list_user_id);
                                            chatIntent.putExtra("user_name", userName);
                                            startActivity(chatIntent);
                                        }

                                    }
                                });

                                builder.show();

                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });




            }
        };

        mFriendsList.setAdapter(friendsRecyclerAdapter);


    }

//    public static class UsersViewHolder extends RecyclerView.ViewHolder {
//
//        View mView;
//
//        public UsersViewHolder(View itemView) {
//            super(itemView);
//
//            mView = itemView;
//        }
//
//        public void setDisplayName(String name) {
//
//            TextView userNameView = (TextView)mView.findViewById(R.id.user_single_name);
//            userNameView.setText(name);
//
//        }
//
//        public void setUserStatus(String status) {
//
//            TextView userStatusView = (TextView)mView.findViewById(R.id.user_single_status);
//            userStatusView.setText(status);
//        }
//
//        public void setUserImage(String thumb_image, Context ctx){
//
//            CircleImageView userImageView = (CircleImageView)mView.findViewById(R.id.user_single_image);
//
//            Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.chat_user_avatar).into(userImageView);
//
//        }
//
//    }

}
