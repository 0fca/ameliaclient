package ameliaclient;
 
import java.awt.AWTException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
 
/**
 *
 * @author Obsidiam
 */
public class ConnectionClient {
     static String USER = System.getProperty("user.name");
     static String OS = System.getProperty("os.name");
    /**
     * @param args the command line arguments
     * @throws java.io.FileNotFoundException
     * @throws java.awt.AWTException
     * @throws java.lang.InterruptedException
     */
    public static void main(String[] args) throws FileNotFoundException, IOException, AWTException, InterruptedException {
       Scanner s = new Scanner(System.in);
       ConnectorThread con = new ConnectorThread();
       con.setName("ConnectorThread");
       con.start();
       String line = s.nextLine();
       
       switch(line){
           case "stop":
               con.stopThread();
               break;
       }
    }
   
    public static String portPathToOS(String path){
        if(OS.contains("Windows")){
            path = "C:"+path;
            path = path.replace("/", "//");
            return path;
        }
        return path;
       
    }
}