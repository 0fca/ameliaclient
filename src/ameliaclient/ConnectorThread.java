package ameliaclient;
 
import static ameliaclient.ConnectionClient.USER;
import ameliaclient.data.ConnectionData;
import java.awt.AWTException;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
 
/**
 *
 * @author obsidiam
 */
public class ConnectorThread extends Thread implements Runnable {
    private Thread INSIDE = null;
    private String IP = "localhost";
    private int PORT = 7999;
    private String ext = "JPG";
    Socket soc = null;
    static ConnectionData cd;
    
    public ConnectorThread(ConnectionData c){
        IP = c.getIp();
        PORT = c.getPort();
        cd = c;
    }

    @Override
    public void start(){
        if(INSIDE == null){
            INSIDE = new Thread(this,"ConnectorThread");
            INSIDE.start();
            cd.setThread(INSIDE);
        }
    }
   
    @Override
    public void run(){
        print("Preparing socket...\n", System.out);
        try {
            print("Is "+IP+":"+PORT+" pingable: "+new InetSocketAddress(IP,PORT).getAddress().isReachable(500),System.out);
        } catch (IOException ex) {
            Logger.getLogger(ConnectorThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        while(INSIDE != null){
            if(!INSIDE.isInterrupted()){
            try{
              
               soc = new Socket(IP,PORT);
               cd.setSocket(soc);
               print("Client is listening on port "+PORT+" and waiting for an idle...\n", System.out);
             
               OutputStream out;
 
               char[] user_chars = USER.toCharArray();
               int len = user_chars.length;
 
               byte[] buffer = new byte[8192];
 
                   print("Started...", System.out);
                   //System.out.println(">");
                   while(soc != null){
                         
                       buffer[0] = (byte)len;
                       for(int it = 1; it<=len; it++){
                           buffer[it] = (byte)((int)user_chars[it-1]);
                           //System.out.print(String.valueOf((char)((int)user_chars[it-1])));
                       }
 
                       BufferedImage image = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
                       Image img = image.getScaledInstance(250, 150, BufferedImage.SCALE_SMOOTH);
 
                       ImageIO.write(toBufferedImage(img), ext, new File(USER+"."+ext));
                       File fileToSend = new File(USER+"."+ext);
                       //System.out.println(fileToSend.length());
                       boolean isRemoteRequested = false;
                       InputStream fromServer = soc.getInputStream();
                       if(fromServer.available() > 0){
                           byte[] tmp = new byte[256];
                           fromServer.read(tmp);
                           isRemoteRequested = (tmp[0] == 1);
                       }
                       
                       cd.setRemoteDesktopOn(isRemoteRequested);
                       InputStream in = new BufferedInputStream(new FileInputStream(fileToSend));
                       in.read(buffer,len+1,8192-(len+1));
                       out = soc.getOutputStream();
                       out.write(buffer);
                       
                       Thread.sleep(1000);
                   }
               }catch(AWTException | HeadlessException | IOException | InterruptedException e){

                    try {
                        Logger.getLogger(ConnectionClient.class.getName()).log(Level.ALL,null,e);
                        if(soc != null){
                            System.err.println("Server disconnected. Resetting connection...\n");
                            soc.close();
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(ConnectorThread.class.getName()).log(Level.SEVERE, null, ex);
                    }
                   soc = null;
               }
            }
        }
        
       print("Connection stopped.", System.out);
    }
   
    protected void stopThread() throws IOException{
        if(INSIDE != null){
            if(soc != null){
                soc.close();
                soc = null;
            }
            INSIDE.interrupt();
            saveSettings();
            INSIDE = null;
        }
    }
    
    private static BufferedImage toBufferedImage(Image img){
        if (img instanceof BufferedImage){
            return (BufferedImage) img;
        }

        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.SCALE_SMOOTH);

        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        return bimage;
    }
    
    protected boolean saveSettings(){
        File settings = new File("settings");

        FileWriter fw;
        try {
            settings.createNewFile();
            fw = new FileWriter(settings);
            fw.write(IP+":"+PORT);
            fw.close();
            return true;
        } catch (IOException ex) {
           return false;
        }       
    }
    
    @Override
    public boolean isInterrupted(){
        if(INSIDE != null){
            return INSIDE.isInterrupted();
        }
        return true;
    }

    static void print(String msg, PrintStream ps){
       ps.print(msg);
       if(!msg.endsWith("\n")){
           try {
               if(System.getProperty("os.name").contains("Windows")){
                  Runtime.getRuntime().exec(new String[]{"cmd.exe","/c","cls"});
               }else{
                   Runtime.getRuntime().exec(new String[]{"bash","-c","clear"});
               }
           } catch (IOException ex) {
               Logger.getLogger(ConnectorThread.class.getName()).log(Level.SEVERE, null, ex);
           }
           ps.print("\n>");
       }
    }
}