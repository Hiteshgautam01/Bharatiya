package ducic.plumbum.com.bjp.utils;

/**
 * Project Name: 	<bjp>
 * Author List: 		Pankaj Baranwal
 * Filename: 		<>
 * Functions: 		<>
 * Global Variables:	<>
 * Date of Creation:    <07/02/2018>
 */
public class TimelineDetails {
    String time;
    private String message, url, image_link, source_name;
//    private int votes;
    private int id;
    // UI = 0 for itl, 1 for tl, 2 for il, 3 for ytl
    private int UI;
    // source_id = 0 for facebook, 1 for youtube, 2 for others
    private int source_id;
    private int votes;

    public TimelineDetails(int id, String message, String url, String image_link, String source_name, int source_id, int votes, String time) {
        this.id = id;
        this.message = message;
        this.url = url;
        if (source_id == 1) {
//            this.url = "https://www.youtube.com/watch?v=" + url;
            this.UI = 3;
        } else {
//            this.url = url;
            if (image_link.length() == 0)
                this.UI = 1;
            else if (message.length() == 0)
                this.UI = 2;
            else
                this.UI = 0;
        }
        this.image_link = image_link;
        this.source_name = source_name;
        this.source_id = source_id;
        this.votes = votes;
        this.time = time.substring(0, 10) + " " + time.substring(11, 16);
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSource_name() {
        return source_name;
    }

    public void setSource_name(String source_name) {
        this.source_name = source_name;
    }

    public int getSource_id() {
        return source_id;
    }

    public void setSource_id(int source_id) {
        this.source_id = source_id;
    }

    public int getUI() {
        return UI;
    }

    public void setUI(int UI) {
        this.UI = UI;
    }

    public String getImage_link() {
        return image_link;
    }

    public void setImage_link(String image_link) {
        this.image_link = image_link;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }
}
