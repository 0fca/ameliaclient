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
        ConnectorThread con = new ConnectorThread();
        con.setName("ConnectorThread");
        con.start();
        Scanner s = new Scanner(System.in);
        
        while(true){
           
           String line = s.nextLine();

           switch(line){
               case "stop":
                   System.out.println("Stopping connection...");
                   con.stopThread();
                   break;
               case "exit":
                   System.exit(0);
                   break;
               case "set-addr":
                   if(con.isInterrupted()){
                       System.out.print("Enter the IP address: ");
                       String ip = s.nextLine();
                       con.setAddress(ip);
                       System.out.println("The program will connect to "+ip);
                   }else{
                       System.err.println("Cannot change address while connection is up.");
                   }
                   break;
               case "reconnect":
                   con.start();
                   break;
               default:
                   System.err.println("There is no command as "+line);
           }
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