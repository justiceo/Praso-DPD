package DPD.SourceParser;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Justice on 1/5/2017.
 */
public class FileBrowser {

    private HashMap<String, FileNode> files;
    public FileBrowser(String[] filePaths) throws IOException, ParseException {
        files = new HashMap<>();
        for(String path: filePaths) {
            files.put(path, new FileNode(path));
        }
    }

    public static String GetType(String filePath) {
        return filePath.substring(filePath.lastIndexOf("\\")+1, filePath.lastIndexOf("."));
    }

    private String getFullyQualifiedType(String filePath) {
        FileNode fn = files.get(filePath);
        PackageDeclaration pd = fn.cu.getPackage();
        String x = pd.toString().replace("package ", "");
        x = x.substring(0, x.indexOf(";")) + ".";

        return x + filePath.substring(filePath.lastIndexOf("\\")+1, filePath.lastIndexOf("."));
    }

    class FileNode {
        int indexOfClass;
        String filePath;
        CompilationUnit cu;

        public FileNode(String path) throws IOException, ParseException {
            this.filePath = path;
            cu = JavaParser.parse(new File(path));
        }
    }
}
