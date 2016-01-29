package DPD.SourceParser;

/**
 * Created by Justice on 1/27/2016.
 *
 * Passes a java file and provides valuable information about its inner working.
 * - including, method names, return values, fields and their types
 *
 * Sample use case, I know that classA.java uses classB.java,
 * I want to determine if this is a typed use, is the instance of classB is in a list or just a var
 * And if any method in classA returns classB
 *
 * I can get all fields in classA and check if any of them is of type classB
 * I can get all methods in classA and check if it returns classB, or has it as parameter
 *
 */
public interface JParser {
}
