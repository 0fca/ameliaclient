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
           
           String[] line = s.nextLine().split(" ");
           
           switch(line[0]){
               case "stop":
                   System.out.println("Stopping connection...");
                   con.stopThread();
                   break;
               case "exit":
                   System.exit(0);
                   break;
               case "set-ip":
                   if(con.isInterrupted()){
                       String ip = line[1];
                       con.setAddress(ip);
                       System.out.println("The program will connect to "+ip);
                   }else{
                       System.err.println("Cannot change address while connection is up.");
                   }
                   break;
               case "reconnect":
                   con.start();
                   break;
                   
               case "set-port":
                   if(con.isInterrupted()){
                       String port = line[1];
                       con.setPort(Integer.parseInt(port));
                       System.out.println("The program will connect to the server via "+port);
                   }else{
                       System.err.println("Cannot change port while connection is up.");
                   }
                   break;
               default:
                   System.err.println("There is no command as "+line[0]);
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