package DPD.Browser;

import DPD.Model.FileNode;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.PackageDeclaration;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Justice on 1/5/2017.
 */
public class FileBrowser {

    private HashMap<String, FileNode> files;
    public FileBrowser(String[] filePaths) {
        files = new HashMap<>();
        int count = 0;
        for(String path: filePaths) {
            files.put(path, new FileNode(count++, path));
        }
    }

    public static String GetType(String filePath) {
        return filePath.substring(filePath.lastIndexOf("\\")+1, filePath.lastIndexOf("."));
    }

    private String getFullyQualifiedType(String filePath) throws IOException, ParseException {
        FileNode fn = files.get(filePath);
        PackageDeclaration pd = fn.getCu().getPackage();
        String x = pd.toString().replace("package ", "");
        x = x.substring(0, x.indexOf(";")) + ".";

        return x + filePath.substring(filePath.lastIndexOf("\\")+1, filePath.lastIndexOf("."));
    }

    public FileNode getByClassId(int classId) {
        for(FileNode f: files.values()){
            if(f.indexOfClass == classId)
                return f;
        }
        return null;
    }
}
