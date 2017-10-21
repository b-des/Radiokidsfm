package fm.radiokids.models;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;


public class ChatModel {

    String user;
    String text;
    String time;
    String avatar_url;


    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setTime(String time) {
        this.time = time;
    }



    public String getUser() {
        return user;
    }

    public String getText() {
        return text;
    }

    public String geTime() {
        return time;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

}