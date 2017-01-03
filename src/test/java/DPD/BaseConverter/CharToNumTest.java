package DPD.BaseConverter;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Justice on 12/20/2016.
 */
public class CharToNumTest {

    @Test
    public void TestConvertChar() {
        System.out.println("max-base: " + CharToNum.MaxBase());
        for(int i = 0; i < CharToNum.MaxBase(); i++) {
            System.out.print(CharToNum.Convert(i));
            char c = CharToNum.Convert(i);
            Assert.assertEquals(i, CharToNum.Convert(c));
        }
    }
}
