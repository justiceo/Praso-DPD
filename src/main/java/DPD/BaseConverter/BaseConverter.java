package DPD.BaseConverter;

/**
 * Created by Justice on 12/20/2016.
 */
public class BaseConverter {
    public String convert(String number, int currentBase, int targetBase) throws Exception {
        return fromBaseTen(toBaseTen(number, currentBase), targetBase);
    }

    public int toBaseTen(String number, int base) throws Exception {
        int result = 0;
        int size = number.length()-1;
        for(int i = 0; i < number.length(); i++) {
            result += (long) CharToNum.Convert(number.charAt(size - i)) * Math.pow(base, i);
        }
        if(result == Integer.MAX_VALUE || result < 0)
            throw new Exception("The input is beyond allowed range. base10: " + result);
        return result;
    }

    public String fromBaseTen(int number, int base) {
        String result = "";
        while(number > 0 ) {
            int original = number;
            number = number / base;
            int quotient = original - (number * base);
            result = CharToNum.Convert(quotient) + result;
        }
        return result;
    }


    public static void main(String[] args) throws Exception {
        int result = new BaseConverter().toBaseTen("30071", 9);
        System.out.println("result: " + result);
        String back = new BaseConverter().fromBaseTen(result, 9);
        System.out.println("back: " + back);

        BaseConverter b = new BaseConverter();
        System.out.println("convert: " + b.convert("6b18ftw2", 20, 3));
    }
}
