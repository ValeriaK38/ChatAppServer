package chatApp.utilities;

import net.bytebuddy.utility.RandomString;
public class Utilities {

    public static String createRandomString(int length){
        return RandomString.make(length);
    }

}
