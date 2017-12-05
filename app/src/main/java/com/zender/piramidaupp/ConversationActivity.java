package com.zender.piramidaupp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConversationActivity extends AppCompatActivity {

    private CircleImageView mProfileImage;

    private String mChatUser;

    private DatabaseReference mRootRef;

    private FirebaseAuth mAuth;
    private String mCurrentUserId;

    private Button mChatAddBtn;
    private Button mChatSendBtn;
    private EditText mChatMessageView;

    private RecyclerView mMessagesList;
    private final List<Message> messageList = new ArrayList<>();
    private LinearLayoutManager mLinearLayout;
    private MessageAdapter mAdapter;

    private static final int GALLERY_PIC = 101;

    //Storage in Database
    private StorageReference storageRef;

    //определяем нотификацию
    private DatabaseReference mRef;
    private DatabaseReference mUserDatabase;
    private DatabaseReference mNotification;

    private FirebaseUser mCurrent_user;



    @Override
    public boolean onSupportNavigateUp() {
            onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mCurrent_user = FirebaseAuth.getInstance().getCurrentUser();
        //Для нотификации - три сртроки
        final String user_id = getIntent().getStringExtra("user_id");
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mNotification = FirebaseDatabase.getInstance().getReference().child("notifications");
        mRootRef = FirebaseDatabase.getInstance().getReference();

        mMessagesList = (RecyclerView) findViewById(R.id.messages_list);

        mRootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();

        mChatUser = getIntent().getStringExtra("user_id");
        String userName = getIntent().getStringExtra("user_name");
        getSupportActionBar().setTitle(userName);

        storageRef = FirebaseStorage.getInstance().getReference();

        // ----Imge of users in toolbar -------------

        mProfileImage = (CircleImageView) findViewById(R.id.item_image_user);

        mChatAddBtn = (Button) findViewById(R.id.chat_add_btn);
        mChatSendBtn = (Button) findViewById(R.id.chat_send_btn);
        mChatMessageView = (EditText) findViewById(R.id.chat_message_view);

        mAdapter = new MessageAdapter(messageList);

        mLinearLayout = new LinearLayoutManager(this);
        mLinearLayout.setStackFromEnd(true);
        mMessagesList.setHasFixedSize(true);
        mMessagesList.setLayoutManager(mLinearLayout);

        mMessagesList.setAdapter(mAdapter);
        
        loadMessages();


        mRootRef.child("Users").child(mChatUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String image = dataSnapshot.child("image").toString();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mRootRef.child("Chat").child(mCurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!dataSnapshot.hasChild(mChatUser)){

                    Map chatAddMap = new HashMap();
                    chatAddMap.put("seen", false);
                    chatAddMap.put("timestamp", ServerValue.TIMESTAMP);

                    Map chatUserMap = new HashMap();
                    chatUserMap.put("Chat/" + mCurrentUserId + "/" + mChatUser, chatAddMap);
                    chatUserMap.put("Chat/" + mChatUser + "/" + mCurrentUserId, chatAddMap);

                    mRootRef.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if(databaseError != null){

                                Log.d("CHAT_LOG", databaseError.getMessage().toString());
                            }

                        }
                    });

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mChatSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {




                String message = mChatMessageView.getText().toString();
                if (!TextUtils.isEmpty(message)) {

                    DatabaseReference newNotificationref = mRootRef.child("notifications").child(user_id).push();
                    String newNotificationId = newNotificationref.getKey();

                    HashMap<String, String> notificationData = new HashMap<>();
                    notificationData.put("from", mCurrent_user.getUid());
                    notificationData.put("type", "request");

                    Map requestMap = new HashMap();
                    requestMap.put("Friend_req/" + mCurrent_user.getUid() + "/" + user_id + "/request_type", "sent");
                    requestMap.put("Friend_req/" + user_id + "/" + mCurrent_user.getUid() + "/request_type", "received");
                    requestMap.put("notifications/" + user_id + "/" + newNotificationId, notificationData);

                    mRootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if(databaseError != null){

                                Toast.makeText(ConversationActivity.this, "Ошибка в отправлении запроса", Toast.LENGTH_SHORT).show();

                            }


                        }
                    });



                    String current_user_ref = "message/" + mCurrentUserId + "/" + mChatUser;
                    String chat_user_ref = "message/" + mChatUser + "/" + mCurrentUserId;

                    DatabaseReference user_message_push = mRootRef.child("messages")
                            .child(mCurrentUserId).child(mChatUser).push();

                    String push_id = user_message_push.getKey();

                    Map messageMap = new HashMap();
                    messageMap.put("from", mCurrentUserId);
                    messageMap.put("message", message);
                    messageMap.put("seen", false);
                    messageMap.put("type", "text");
                    messageMap.put("time", ServerValue.TIMESTAMP);



                    Map messageUserMap = new HashMap();
                    messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
                    messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);

                    mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if(databaseError != null){

                                Log.d("CHAT_LOG", databaseError.getMessage().toString());

                            }

                        }
                    });


                }

               // sendMessage();
                mChatMessageView.setText("");
                
            }
        });

        // Определяем кнопку для прикрепления медиа
        mChatAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("*/*");
                startActivityForResult(Intent.createChooser(galleryIntent, "Выбрать медиа"), GALLERY_PIC);

            }
        });
        //Конец метода, что определяет прикрепления медиа

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PIC && resultCode == RESULT_OK) {

            final Uri fileUri = data.getData();

            final String current_user_ref = "message/" + mCurrentUserId + "/" + mChatUser;
            final String chat_user_ref = "message/" + mChatUser + "/" + mCurrentUserId;

            DatabaseReference user_message_push = mRootRef.child("messages")
                    .child(mCurrentUserId).child(mChatUser).push();

            final String push_id = user_message_push.getKey();

            StorageReference filepath = storageRef.child("files").child(fileUri.getLastPathSegment());

            filepath.putFile(fileUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    if (task.isSuccessful()) {

                        @SuppressWarnings("VisibleForTests") String download_url = task.getResult().getDownloadUrl().toString();

                        Map messageMap = new HashMap();
                        messageMap.put("message", download_url);
                        messageMap.put("seen", false);
                        messageMap.put("type", "files");
                        messageMap.put("from", mCurrentUserId);

                        Map messageUserMap = new HashMap();
                        messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
                        messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);

                        mChatMessageView.setText("");

                        mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                if (databaseError != null) {
                                    Log.d("CHAT_LOG", databaseError.getMessage().toString());
                                }

                            }
                        });
                    }

                }
            });

        }

    }


    private void loadMessages() {

        mRootRef.child("message").child(mCurrentUserId).child(mChatUser).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Message messages = dataSnapshot.getValue(Message.class);

                messageList.add(messages);
                mAdapter.notifyDataSetChanged();

                mLinearLayout.setStackFromEnd(true);
                mLinearLayout.scrollToPosition(messageList.size() -1);



            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

  //  private void sendMessage() {

//        String message = mChatMessageView.getText().toString();

//        if (!TextUtils.isEmpty(message)) {
//
//            String current_user_ref = "message/" + mCurrentUserId + "/" + mChatUser;
//            String chat_user_ref = "message/" + mChatUser + "/" + mCurrentUserId;
//
//            DatabaseReference user_message_push = mRootRef.child("messages")
//                    .child(mCurrentUserId).child(mChatUser).push();
//
//            String push_id = user_message_push.getKey();
//
//            Map messageMap = new HashMap();
//            messageMap.put("from", mCurrentUserId);
//            messageMap.put("message", message);
//            messageMap.put("seen", false);
//            messageMap.put("type", "text");
//            messageMap.put("time", ServerValue.TIMESTAMP);
//
//
//
//            Map messageUserMap = new HashMap();
//            messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
//            messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);
//
//            mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
//                @Override
//                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//
//                    if(databaseError != null){
//
//                        Log.d("CHAT_LOG", databaseError.getMessage().toString());
//
//                    }
//
//                }
//            });
//
//
//        }

  //  }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_conversation, menu);
        return true;
    }

}
