package ducic.plumbum.com.bjp.utils;

/**
 * Created by pankaj on 18/3/18.
 */

public class CommentDetails {
    String user_name;
    String message;
    int id;

    public CommentDetails(int id, String user_name, String message) {
        this.user_name = user_name;
        this.message = message;
        this.id = id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
