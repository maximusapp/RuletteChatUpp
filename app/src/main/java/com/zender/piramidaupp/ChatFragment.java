package com.zender.piramidaupp;


import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import static android.support.v4.widget.SearchViewCompat.getQuery;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {

    private RecyclerView mChatList;

    private DatabaseReference mMessageDatabase;
    private DatabaseReference usersDatabase;
    private FirebaseAuth mAuth;

    private String mCurrentUserId;

    private View mMainChatsViewe;

    AlertDialog.Builder ad;

    private FirebaseRecyclerAdapter<Chats, ChatsActivity.ChatsViewHolder> recyclerAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        mMainChatsViewe = inflater.inflate(R.layout.fragment_chat, container, false);
        mChatList = mMainChatsViewe.findViewById(R.id.list_of_chats);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();

        //Доступ ко всем детям родителя Users
       usersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

       mMessageDatabase = FirebaseDatabase.getInstance().getReference().child("message").child(mCurrentUserId);

        mChatList.setLayoutManager(new LinearLayoutManager(getContext()));

        return mMainChatsViewe;



    }

    Query query = FirebaseDatabase.getInstance().getReference().child("message").child(mCurrentUserId);

    FirebaseRecyclerOptions<Chats> options =
            new FirebaseRecyclerOptions.Builder<Chats>()
                    .setQuery(query, Chats.class)
                    .build();




    @Override
    public void onStart() {
        super.onStart();

            final FirebaseRecyclerAdapter<Chats, ChatsActivity.ChatsViewHolder> chatsRecyclerAdapter =
                    new FirebaseRecyclerAdapter<Chats, ChatsActivity.ChatsViewHolder>(

                    Chats.class,
                    R.layout.user_single_layout_chats,
                    ChatsActivity.ChatsViewHolder.class,
                    mMessageDatabase

            ) {



                        protected void populateViewHolder(final ChatsActivity.ChatsViewHolder chatsViewHolder,
                                                          final Chats chats, final int position) {

                    final String message_id = getRef(position).getKey();


                    usersDatabase.child(message_id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(final DataSnapshot dataSnapshot) {

                            final String userNames = dataSnapshot.child("name").getValue().toString();

                            if (mMessageDatabase != null) {

                                    chatsViewHolder.setChatName(userNames);

                            }

                    chatsViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            CharSequence options[] = new CharSequence[]{"Перейти в чат c " + userNames, "Удалить чат с " + userNames };
                            final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle("Выбрать опцию");
                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (i == 0) {

                                        Intent chatsIntent = new Intent(getContext(), ConversationActivity.class);
                                        chatsIntent.putExtra("user_id",message_id);
                                        startActivity(chatsIntent);

                                    }
                                    if (i == 1) {
                                        if (mMessageDatabase.child(userNames) != null) {
                                                deleteChat(message_id);
                                        }
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

            mChatList.setAdapter(chatsRecyclerAdapter);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(mChatList.getContext(),
                LinearLayoutManager.VERTICAL);

        itemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.divider_white));

        mChatList.addItemDecoration(itemDecoration);

    }

    private void deleteChat(final String message_id) {

        String title = "Уверен что хочешь удалить чат?";
        String message = "Все сообщения будут удалены без возможности восстановления...";
        String button1 = "Хочу удалить";
        String button2 = "Нет, передумал";

        ad = new AlertDialog.Builder(getContext());
        ad.setTitle(title);  // заголовок
        ad.setMessage(message); // сообщение
        ad.setPositiveButton(button1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {

                DatabaseReference delRef = FirebaseDatabase.getInstance().getReference("message").child(mCurrentUserId).child(message_id);
                delRef.removeValue();

                Toast.makeText(getContext(), "Удалено без возврата",
                        Toast.LENGTH_LONG).show();

            }
        });
        ad.setNegativeButton(button2, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                Toast.makeText(getContext(), "Сообщения остались", Toast.LENGTH_LONG)
                        .show();
            }
        });
        ad.setCancelable(false);
        ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                Toast.makeText(getContext(), "Удалено без возврата",
                        Toast.LENGTH_LONG).show();
            }
        });

        ad.show();

    }

}