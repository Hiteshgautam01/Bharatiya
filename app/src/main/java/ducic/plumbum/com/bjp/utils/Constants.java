package ducic.plumbum.com.bjp.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Project Name: 	<bjp>
 * Author List: 		Pankaj Baranwal
 * Filename: 		<>
 * Functions: 		<>
 * Global Variables:	<>
 * Date of Creation:    <14/02/2018>
 */
public class Constants {
    public static String user_email = null;
    public static String user_id = null;
    public static String quote = "";

    public static String url_signup = "https://www.lithics.in/apis/bjp/signup.php";
    public static String url_event_details = "https://www.lithics.in/apis/bjp/getAllPosts.php";
    public static String url_verify_user = "https://www.lithics.in/apis/bjp/verify.php";
    public static String url_action = "https://www.lithics.in/apis/bjp/recordActivity.php";

    public static int paused_post_id = 0;
    public static List<Integer> actions = new ArrayList<>();
    public static List<String> messages = new ArrayList<>();
    public static List<Integer> post_id = new ArrayList<>();
    public static boolean updating = false;
}
