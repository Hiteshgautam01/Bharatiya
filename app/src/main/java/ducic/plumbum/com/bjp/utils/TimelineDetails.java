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
    private String message, url, image_link, source_name;
    private int id;
//    private int votes;

    // UI = 0 for itl, 1 for tl, 2 for il, 3 for ytl
    private int UI;
    // source_id = 0 for facebook, 1 for youtube, 2 for others
    private int source_id;

    public TimelineDetails(int id, String message, String url, String image_link, String source_name, int source_id){
        this.id = id;
        this.message = message;
        this.url = url;
        if (source_id == 1) {
//            this.url = "https://www.youtube.com/watch?v=" + url;
            this.UI = 3;
        }
        else {
//            this.url = url;
            if (image_link.length()==0)
                this.UI = 2;
            else if (message.length() == 0)
                this.UI = 1;
            else
                this.UI = 0;
        }
        this.image_link = image_link;
        this.source_name = source_name;
        this.source_id = source_id;

    }

    public TimelineDetails(String message, String url, String image_link, String source_name, int UI){
        this.message = message;
        if (UI == 1)
            this.url = "https://www.youtube.com/watch?v=" + url;
        else
            this.url = url;
        this.image_link = image_link;
        this.source_name = source_name;
        this.UI = UI;
    }

    public TimelineDetails(String message, String url, String image_link){
        this.image_link = image_link;
        this.message = message;
        this.url = url;
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

    public String getMessage() {
        return message;
    }

    public String getUrl() {
        return url;
    }

    public void setImage_link(String image_link) {
        this.image_link = image_link;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
