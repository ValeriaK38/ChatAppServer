package chatApp.utilities;

import net.bytebuddy.utility.RandomString;
public class Utilities {

    public static String createRandomString64(){
        return RandomString.make(5);
    }

}
