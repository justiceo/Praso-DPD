package DPD.Model;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;

import java.io.File;
import java.io.IOException;

/**
 * Created by Justice on 1/19/2017.
 */
public class FileNode {
    public int indexOfClass;
    private String filePath;
    private CompilationUnit cu;

    public FileNode(int index, String path)  {
        this.indexOfClass = index;
        this.filePath = path;
    }

    public CompilationUnit getCu() throws IOException, ParseException {
        if(cu == null){
            cu = JavaParser.parse(new File(filePath));
        }
        return cu;
    }
}