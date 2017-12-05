package com.zender.piramidaupp;



//для закладки, где будет отображаться чаты с юзерами



public abstract class Chats {

    public Chats(){}

    private String chatName;

    public Chats(String chatName) {
        this.chatName = chatName;
    }

    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }


}
