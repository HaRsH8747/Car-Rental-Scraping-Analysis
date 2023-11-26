package webcrawling;

import org.openqa.selenium.WebElement;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class WebCrawler {
    public static List<String> findHyperLinks(List<WebElement> lnks) {
        List<String> url_List = new ArrayList<String>();
        for (WebElement elements : lnks) {

            // check for null
            if (elements.equals(null))
                continue;
            else {
                // If href present add to the list
                url_List.add(elements.getAttribute("href"));
            }
        }
        // remove if any null link
        url_List.remove(null);
        return (url_List);
    }

    public static void writeContent(String folderName, String content, String fileName, String extension) {
        try {
            File folderCheck = new File(folderName);
            File f = new File(folderName + fileName + extension);
            if (!folderCheck.exists()) {
                // Try to create the directory
                boolean created = folderCheck.mkdirs(); // This will create the folder and any necessary but nonexistent parent directories.

                if (created) {
//					System.out.println("Folder created successfully.");
                    FileWriter writer = new FileWriter(f, false);
                    writer.write(content);
                    //			System.out.println("Report Created is in Location : " + f.getAbsolutePath());
                    writer.close();
                } else {
                    System.out.println("Failed to create the folder.");
                }
            } else {
                FileWriter writer = new FileWriter(f, false);
                writer.write(content);
                //			System.out.println("Report Created is in Location : " + f.getAbsolutePath());
                writer.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("OOPS!!!Error in Writing file");
        }
    }

    public static Hashtable<String, String> createFile(String url, String content, String fileName,
                                                       String folder) {
        Hashtable<String, String> url_Map = new Hashtable<String, String>();
        url_Map.put(fileName + ".html", url);
        writeContent(folder, content, fileName, ".html");
        return url_Map;
    }

}
