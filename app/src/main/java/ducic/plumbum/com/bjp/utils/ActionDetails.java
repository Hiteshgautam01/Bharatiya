package ducic.plumbum.com.bjp.utils;

/**
 * Created by pankaj on 18/3/18.
 */

public class ActionDetails {
    private int action;
    private String message;
    private int post_id;

    public ActionDetails(int action, String message, int post_id) {
        this.action = action;
        this.message = message;
        this.post_id = post_id;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public int getPost_id() {
        return post_id;
    }

    public void setPost_id(int post_id) {
        this.post_id = post_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
