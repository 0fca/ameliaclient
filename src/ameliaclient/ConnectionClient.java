package ameliaclient;
 
import ameliaclient.data.ConnectionData;
import ameliaclient.data.InputDataType;
import ameliaclient.nativesupport.DeviceType;
import ameliaclient.nativesupport.NativeControllerFactory;
import ameliaclient.nativesupport.NativeKeyboardController;
import ameliaclient.nativesupport.NativeMouseController;
import java.awt.AWTException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
     static final ConnectionData cd = new ConnectionData();
     private static RemoteDesktopThread rdt;
     private static NativeMouseController nativeMouseController;
     private static NativeKeyboardController keyboardController;
     private static int mode = Mode.LOCAL;
     private static final float VERSION = 2f; 
     private static boolean firstRun = true;
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
     
    static ConnectorThread con = new ConnectorThread(cd);
    
    public static void main(String[] args) throws FileNotFoundException, IOException, AWTException, InterruptedException {
        Scanner s = new Scanner(System.in);
        while(true){
            if(firstRun){
                firstRun = false;
                ConnectorThread.print("Alice "+VERSION+"\nMode: 0x"+Integer.toHexString(mode)+"("+((mode == Mode.LOCAL) ? "Local" : "Remote")+")"+"\nFor help type 'help' or '?'.", System.out);
            }
            if(args.length == 0){
                String[] line = s.nextLine().split(" ");
                recognizeCommands(line);
            }else{
                String[] line = s.nextLine().split(" ");
                if(args[0].equals("-mode")){
                    switch(Integer.parseInt(args[1])){
                        case Mode.LOCAL:
                            mode = Mode.LOCAL;
                            recognizeCommands(line);
                            break;
                        case Mode.REMOTE:
                            mode = Mode.REMOTE;
                            recognizeCommands(line);
                            break;
                    }
                }
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
        System.out.println("Alice v."+VERSION+" by Obsidiam");
        System.out.print("Commands: \n"
                + "stop - stops the connection\n"
                + "connect - reconnects to the server\n"
                + "set-port - sets the port; just press enter to set the appropiate port number using mode.\n"
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

    private static void recognizeCommands(String[] line) throws IOException, InterruptedException{
               switch(line[0]){
                   case "stop":
                       stop();
                       break;
                   case "exit":
                       con.saveSettings();
                       System.exit(0);
                       break;
                   case "set-ip":
                       setIp(line);
                       break;
                   case "connect":
                       con.start();
                       break;

                   case "set-port":
                        setPort(line);
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
                       ConnectorThread.print("Mode: 0x"+Integer.toHexString(mode)+"("+((mode == Mode.LOCAL) ? "Local" : "Remote")+")",System.out);
                       break;

                   case "check-native-acc":
                       nativeAccCheck();
                       break;

                   case "remote-start":
                       remoteStart();
                       break;
                       
                   case "set-mode":
                       setMode(line);
                       break;
                   default:
                       ConnectorThread.print("There is no command as "+line[0], System.err);
               }    
    }
    
    
    
    private static void stop() throws IOException{
        ConnectorThread.print("Stopping connection...\n", System.out);
        con.stopThread();
    }
    
    private static void setIp(String[] line){
        if(con.isInterrupted()){
           if(line.length > 1){
               String ip = line[1];
               if((!Mode.isPrivateIp(ip) && mode == Mode.REMOTE )|| mode == Mode.LOCAL){
                   cd.setIp(ip);
                   ConnectorThread.print("The program will connect to "+ip, System.out);
               }else{
                   ConnectorThread.print("IP and connection Mode are different: isIpPublic = "+!Mode.isPrivateIp(ip)+"; mode = "+((mode == Mode.LOCAL) ? Mode.LOCAL : Mode.REMOTE), System.err);
               }
           }
       }else{
           System.err.println("Cannot change address while connection is up.\n");
       }
    }

    
    private static void setPort(String[] line){
        if(con.isInterrupted()){
           int port;
           if(line.length > 1){
               port = Integer.parseInt(line[1]);
               cd.setPort(port);
           }else{
               port = Mode.getPort(mode);
               cd.setPort(port);
           }
           ConnectorThread.print("The program will connect to the server via "+port, System.out);
        }else{
               ConnectorThread.print("Cannot change port while connection is up.", System.err);
        }
    }
    
    private static void setMode(String[] line){
        if(line.length > 1){
           int modeLoc = Integer.parseInt(line[1]);
           if(modeLoc == Mode.LOCAL || modeLoc == Mode.REMOTE){
                mode = modeLoc;
           }else{
               ConnectorThread.print("Cannot set mode as "+modeLoc, System.out);
           }
        }
    }
    
    private static void remoteStart(){
        if(cd.isRemoteOn()){
            System.out.println("Starting remote desktop...");
            rdt = new RemoteDesktopThread(cd.getIp());
            rdt.start();
        }
    }
    
    @SuppressWarnings("uncheck")
    private static void nativeAccCheck() throws InterruptedException{
           ConnectorThread.print("Warning: Testing native access can cause fatal error, do you want to proceed?",System.err);
           
           String choice = new Scanner(System.in).nextLine();
           if(choice.trim().toLowerCase().contains("y")){

               nativeMouseController = (NativeMouseController)NativeControllerFactory.getControllerInstance(DeviceType.MOUSE);
               keyboardController = (NativeKeyboardController)NativeControllerFactory.getControllerInstance(DeviceType.KEYBOARD);
               nativeMouseController.sendCoords(350, 10);
               nativeMouseController.doMouseClick(1);
               Thread.sleep(1000);
               keyboardController.inputKeyboardData("va",0,InputDataType.SEQUENCE);
               System.exit(0);
           }else{
               ConnectorThread.print("",System.out);
           }
    }
}