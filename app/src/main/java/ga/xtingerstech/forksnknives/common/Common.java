package ga.xtingerstech.forksnknives.common;

import ga.xtingerstech.forksnknives.models.User;

/**
 * Created by thebullwhocodes on 5/3/18.
 */

public class Common {
    private static User currentUser;
    private  static String phoneNumber;

    public static String getPhoneNumber() {
        return phoneNumber;
    }

    public static void setPhoneNumber(String phoneNumber) {
        Common.phoneNumber = phoneNumber;
    }

    public Common() {

    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User currentUser) {
        Common.currentUser = currentUser;
    }
}
