package gamal.myappnew.chatapp.Moduel;

public class User {
    String id,username,imageURL,status;

    public User() {
    }

    public User(String id, String username, String imageURL,String status) {
        this.id = id;
        this.username = username;
        this.status=status;
        this.imageURL = imageURL;
   }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }


}
