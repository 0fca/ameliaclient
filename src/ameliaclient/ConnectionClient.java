package ameliaclient;
 
import ameliaclient.data.ConnectionData;
import ameliaclient.nativesupport.DeviceType;
import ameliaclient.nativesupport.NativeControllerFactory;
import ameliaclient.nativesupport.NativeMouseController;
import java.awt.AWTException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
 
/**
 *
 * @author Obsidiam
 */
public class ConnectionClient {
     static String USER = System.getProperty("user.name");
     static String OS = System.getProperty("os.name");
     static ConnectionData cd = new ConnectionData();
     private static RemoteDesktopThread rdt;
    /**
     * @param args the command line arguments
     * @throws java.io.FileNotFoundException
     * @throws java.awt.AWTException
     * @throws java.lang.InterruptedException
     */
     
     
    static{
        try {
            loadSettings();
        } catch (IOException ex) {
            Logger.getLogger(ConnectorThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
     
    public static void main(String[] args) throws FileNotFoundException, IOException, AWTException, InterruptedException {
        ConnectorThread con = new ConnectorThread(cd);
        //con.setName("ConnectorThread");
        con.start();

        Scanner s = new Scanner(System.in);
        
        while(true){
           
           String[] line = s.nextLine().split(" ");
           
           switch(line[0]){
               case "stop":
                   ConnectorThread.print("Stopping connection...\n", System.out);
                   con.stopThread();
                   break;
               case "exit":
                   con.saveSettings();
                   System.exit(0);
                   break;
               case "set-ip":
                   if(con.isInterrupted()){
                       String ip = line[1];
                       con.setAddress(ip);
                       ConnectorThread.print("The program will connect to "+ip, System.out);
                   }else{
                       System.err.println("Cannot change address while connection is up.\n");
                   }
                   break;
               case "connect":
                   con.start();
                   break;
                   
               case "set-port":
                   if(con.isInterrupted()){
                       String port = line[1];
                       con.setPort(Integer.parseInt(port));
                       ConnectorThread.print("The program will connect to the server via "+port, System.out);
                   }else{
                       ConnectorThread.print("Cannot change port while connection is up.", System.err);
                   }
                   break;
               case "load":
                   if(!loadSettings()){
                       ConnectorThread.print("Cannot find the settings file.", System.out);
                   }else{
                       ConnectorThread.print("Restart to connect the server!", System.err);
                   }
                   break;
               case "save":
                   if(con.saveSettings()){
                       ConnectorThread.print("Settings saved.", System.out);
                   }else{
                       ConnectorThread.print("Error saving settings.", System.err);
                   }
                   break;
               case "help":
                   printHelp();
                   break;
               case "?":
                   printHelp();
                   break;
               case "view-settings":
                   ConnectorThread.print("Server's IP: "+cd.getIp(), System.out);
                   ConnectorThread.print("Local IP: "+cd.getLocalIp(), System.out);
                   ConnectorThread.print("Port: "+cd.getPort(), System.out);
                   ConnectorThread.print("Is ready to connect: "+(cd.getThread() != null), System.out);
                   break;
                   
               case "check-native-acc":
                   ConnectorThread.print("Warning: Testing native access can cause fatal error, do you want to proceed?",System.err);
                   String choice = s.nextLine();
                   if(choice.trim().toLowerCase().contains("y")){
                       NativeControllerFactory mouseController = new NativeControllerFactory(DeviceType.MOUSE);
                       NativeMouseController nativeMouseController = (NativeMouseController)mouseController.getControllerInstance();
                       nativeMouseController.sendCoords(10, 10);
                       nativeMouseController.nativeMouseClick(1);
                   }else{
                       ConnectorThread.print("",System.out);
                   }
                   break;
                   
               case "remote-start":
                   if(cd.isRemoteOn()){
                        System.out.println("Starting remote desktop...");
                        rdt = new RemoteDesktopThread(cd.getIp());
                        rdt.start();
                   }
                   break;
               default:
                   ConnectorThread.print("There is no command as "+line[0], System.err);
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

    private static void printHelp() {
        System.out.println("Amelia Client v.1.5 by Obsidiam");
        System.out.print("Commands: \n"
                + "stop - stops the connection\n"
                + "connect - reconnects to the server\n"
                + "set-port - sets the port\n"
                + "set-ip - sets IP address of the server\n"
                + "help - prints this message\n"
                + "load - loads settings from default file\n"
                + "save - saves settings\n"
                + "view-settings - views settings\n>");
    }
    
    protected static boolean loadSettings() throws FileNotFoundException, IOException {
        File f = new File("settings");
        if(f.exists()){
            BufferedReader fr = new BufferedReader(new FileReader(f));
            
            char[] c = new char[32];
            if(fr.ready()){
                fr.read(c);
                fr.close();
            }
            parseSettings(new String(c));
            return true;
        }
        return false;
    }

    private static void parseSettings(String s) {
        String[] tmp = s.split(":");
        cd.setIp(tmp[0]);
        cd.setPort(Integer.parseInt(tmp[1].trim()));
    }
}