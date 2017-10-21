package fm.radiokids.models;


public class Message {
    public String avatar;
    public String author;
    public String message;
    public String date;

    public void setAvatar(String avatar){
        this.avatar = avatar;
    }
    public void setAuthor(String author){
        this.author = author;
    }
    public void setMessage(String message){
        this.message = message;
    }
    public void setDate(String date){
        this.date = date;
    }


    public String getAvatar(){
        return avatar;
    }
    public String getAuthor(){
        return author;
    }
    public String getMessage(){
        return message;
    }
    public String getdate(){
        return date;
    }
}
