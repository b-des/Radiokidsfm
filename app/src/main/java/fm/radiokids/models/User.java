package fm.radiokids.models;


import java.io.Serializable;

public class User implements Serializable{
    int id;
    String name;
    String nickname;
    String email;
    String avatar;

    public User(int id,String name, String nickname, String email,String avatar){
        this.id = id;
        this.name = name;
        this.nickname = nickname;
        this.email = email;
        this.avatar = avatar;
    }

    public void setId(int id){
        this.id = id;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setNickname(String nickname){
        this.nickname = nickname;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public void setAvatar(String avatar){
        this.avatar = avatar;
    }

    public int getId(){
        return id;
    }
    public String getName(){
        return name;
    }
    public String getNickname(){
        return nickname;
    }
    public String getEmail(){
        return email;
    }
    public String getAvatar(){return avatar;}


}
