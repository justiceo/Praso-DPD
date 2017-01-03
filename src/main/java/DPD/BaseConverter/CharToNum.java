package DPD.BaseConverter;

import java.util.Dictionary;
import java.util.Hashtable;

/**
 * Created by Justice on 12/20/2016.
 */
public class CharToNum {

    private static final String placeValues = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ!@#$%^&*()-_=;',.?|{}[]<>`~:/";
    private static Dictionary<Character, Integer> mappings;

    public static int Convert(char c) {
        if(mappings == null) {
            mappings =  new Hashtable<>();
            for(int i = 0; i < placeValues.length(); i++) {
                mappings.put(placeValues.charAt(i), i);
            }
            assert mappings.size() == placeValues.length();
        }
        return mappings.get(c);
    }

    public static char Convert(int n) {
        return placeValues.charAt(n);
    }

    public static int MaxBase() {
        return placeValues.length();
    }
}
