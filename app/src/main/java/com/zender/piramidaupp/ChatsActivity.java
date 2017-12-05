package com.zender.piramidaupp;


import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public abstract class ChatsActivity extends AppCompatActivity {

    private RecyclerView mChatsList;

    private DatabaseReference mChatDatabase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mChatDatabase = FirebaseDatabase.getInstance().getReference().child("Chat");

        mChatsList = findViewById(R.id.list_of_chats);
        mChatsList.setHasFixedSize(true);
        mChatsList.setLayoutManager(new LinearLayoutManager(this));


    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Chats, ChatsViewHolder> firebaseRecyclerAdapter =
                                                new FirebaseRecyclerAdapter<Chats, ChatsViewHolder>(

                Chats.class,
                R.layout.user_single_layout_chats,
                ChatsViewHolder.class,
                mChatDatabase

        ) {
            protected void populateViewHolder(ChatsViewHolder chatsViewHolder, Chats chats, int position) {

                chatsViewHolder.setChatName(chats.getChatName());


            }
        };

        mChatsList.setAdapter(firebaseRecyclerAdapter);

    }

    public static class ChatsViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public ChatsViewHolder(final View itemView) {
            super(itemView);

            mView = itemView;

        }

        public void setChatName(String name) {

            TextView chatName = mView.findViewById(R.id.user_name);
            chatName.setText(name);

        }


    }
}

