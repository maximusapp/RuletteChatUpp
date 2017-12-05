package com.zender.piramidaupp;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;



public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<Message> mMessageList;

    private FirebaseAuth mAuth;

    public MessageAdapter(List<Message> mMessageList) {

        this.mMessageList = mMessageList;

    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_single_layout ,parent, false);


        return new MessageViewHolder(v);

    }




    public class MessageViewHolder extends RecyclerView.ViewHolder {

       final public TextView messageText;
        public CircleImageView profileImage;

        final public TextView messageTextFrom;
        public CircleImageView getProfileImageFrom;

        public ImageView messageImage;
        public ImageView messageImageFrom;

        public VideoView messageVideo;





        public MessageViewHolder(View view) {
            super(view);

            messageText = (TextView) view.findViewById(R.id.message_text_layout);
            profileImage = (CircleImageView) view.findViewById(R.id.message_profile_layout);

            messageTextFrom = (TextView) view.findViewById(R.id.message_text_layout_from);
            getProfileImageFrom = (CircleImageView) view.findViewById(R.id.message_profile_layout_from);

            messageImage = (ImageView) view.findViewById(R.id.message_image_layout);
            messageImageFrom = (ImageView) view.findViewById(R.id.message_image_from);

            messageVideo = (VideoView) view.findViewById(R.id.message_video_layout);

        }

    }

    @Override
    public void onBindViewHolder(final MessageViewHolder viewHolder, int i) {

        mAuth = FirebaseAuth.getInstance();
        String current_user_id = mAuth.getCurrentUser().getUid();

        Message c = mMessageList.get(i);

        //set background of sms
        String from = c.getFrom();
        final String message_type = c.getType();

        if (current_user_id.equals(from) && message_type.equals("text")) {

            viewHolder.messageImage.setVisibility(View.INVISIBLE);
            viewHolder.messageImage.getLayoutParams().height = 0;
            viewHolder.messageImage.getLayoutParams().width = 0;
            viewHolder.messageImageFrom.setVisibility(View.INVISIBLE);

            viewHolder.messageTextFrom.setBackgroundResource(R.drawable.message_text_bgnd_second);
            viewHolder.messageTextFrom.setTextColor(Color.DKGRAY);

            viewHolder.messageText.setVisibility(View.INVISIBLE);
            viewHolder.profileImage.setVisibility(View.INVISIBLE);

            viewHolder.messageTextFrom.setVisibility(View.VISIBLE);
            viewHolder.getProfileImageFrom.setVisibility(View.VISIBLE);

            viewHolder.messageVideo.setVisibility(View.INVISIBLE);

            viewHolder.messageTextFrom.setText(c.getMessage());


            viewHolder.messageTextFrom.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Toast.makeText(viewHolder.messageTextFrom.getContext(), "Long Pressed", Toast.LENGTH_SHORT).show();
                    return false;
                }
            });


        } else if (!current_user_id.equals(from) && message_type.equals("text")) {

            viewHolder.messageImage.setVisibility(View.INVISIBLE);
            viewHolder.messageImage.getLayoutParams().height = 0;
            viewHolder.messageImage.getLayoutParams().width = 0;
            viewHolder.messageImageFrom.setVisibility(View.INVISIBLE);
            viewHolder.messageImageFrom.getLayoutParams().height = 0;
            viewHolder.messageImageFrom.getLayoutParams().width = 0;

            viewHolder.messageText.setBackgroundResource(R.drawable.message_text_background);
            viewHolder.messageText.setTextColor(Color.WHITE);

            viewHolder.profileImage.setVisibility(View.VISIBLE);
            viewHolder.messageText.setVisibility(View.VISIBLE);

            viewHolder.messageTextFrom.setVisibility(View.INVISIBLE);
            viewHolder.getProfileImageFrom.setVisibility(View.INVISIBLE);

            viewHolder.messageVideo.setVisibility(View.INVISIBLE);

            viewHolder.messageText.setText(c.getMessage());


        } else if (!message_type.equals("text") && current_user_id.equals(from)) {

            //Вывод картинки которую я отправлю

            viewHolder.messageImage.setVisibility(View.INVISIBLE);

            viewHolder.messageImageFrom.setVisibility(View.VISIBLE);
            viewHolder.messageImageFrom.getLayoutParams().height = 600;
            viewHolder.messageImageFrom.getLayoutParams().width = 500;
            viewHolder.messageImageFrom.requestLayout();

            viewHolder.messageImageFrom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(viewHolder.messageImageFrom.getContext(), LargeImageActivity.class);
                    viewHolder.messageImageFrom.getContext().startActivity(intent);

                }
            });

            viewHolder.messageText.setVisibility(View.INVISIBLE);
            viewHolder.profileImage.setVisibility(View.INVISIBLE);

            viewHolder.messageTextFrom.setVisibility(View.INVISIBLE);

            viewHolder.getProfileImageFrom.setVisibility(View.VISIBLE);

            viewHolder.messageVideo.setVisibility(View.INVISIBLE);


            Picasso.with(viewHolder.getProfileImageFrom.getContext()).load(c.getMessage())
                    .placeholder(R.drawable.profile_image).into(viewHolder.messageImageFrom);


        } else if (!message_type.equals("text") && !current_user_id.equals(from)) {

            //Вывод фото сообщения от юзера с которым общаюсь
            viewHolder.messageImage.setVisibility(View.VISIBLE);
            viewHolder.messageImage.getLayoutParams().height = 600;
            viewHolder.messageImage.getLayoutParams().width = 500;
            viewHolder.messageImage.requestLayout();

            viewHolder.messageImageFrom.setVisibility(View.INVISIBLE);
           // viewHolder.messageImage.setBackgroundResource(R.drawable.message_text_background);

            viewHolder.messageText.setVisibility(View.INVISIBLE);
            viewHolder.profileImage.setVisibility(View.VISIBLE);

            viewHolder.messageTextFrom.setVisibility(View.INVISIBLE);
            viewHolder.getProfileImageFrom.setVisibility(View.INVISIBLE);

            viewHolder.messageVideo.setVisibility(View.INVISIBLE);


            Picasso.with(viewHolder.profileImage.getContext()).load(c.getMessage())
                    .placeholder(R.drawable.profile_image).into(viewHolder.messageImage);

        }
        //Добавляю работу с видео
        else if (message_type.equals("video") && current_user_id.equals(from)) {
            viewHolder.messageVideo.setVisibility(View.VISIBLE);
        } else if (message_type.equals("video") && !current_user_id.equals(from)) {
            viewHolder.messageVideo.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }


}