package ameliaclient;
 
import static ameliaclient.ConnectionClient.USER;
import java.awt.AWTException;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
 
/**
 *
 * @author lukas
 */
public class ConnectorThread extends Thread implements Runnable {
    private Thread INSIDE = null;
    private String IP = "localhost";
    private int PORT = 7999;
    //public boolean IS_CONNECTED = false;
    Socket soc = null;
    
    {
        try {
            loadSettings();
        } catch (IOException ex) {
            Logger.getLogger(ConnectorThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void start(){
        if(INSIDE == null){
            INSIDE = new Thread(this,"ConnectorThread");
            INSIDE.start();
        }
    }
   
    @Override
    public void run(){
        System.out.println("Preparing socket...");
        try {
            System.out.println("Is "+IP+":"+PORT+" pingable: "+new InetSocketAddress(IP,PORT).getAddress().isReachable(500));
            
            //System.out.println(IP);
        } catch (IOException ex) {
            Logger.getLogger(ConnectorThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        while(!INSIDE.isInterrupted()){
            try{
              
               soc = new Socket(IP,PORT);
               
               System.out.println("Client is listening on port "+PORT+" and waiting for an idle...");
             
               OutputStream out;
 
               char[] user_chars = USER.toCharArray();
               int len = user_chars.length;
 
               byte[] buffer = new byte[8192];
 
                   System.out.println("Started...");
                   //System.out.println(">");
                   while(true){
 
                       buffer[0] = (byte)len;
                       for(int it = 1; it<=len; it++){
                           buffer[it] = (byte)((int)user_chars[it-1]);
                           //System.out.print(String.valueOf((char)((int)user_chars[it-1])));
                       }
 
                       BufferedImage image = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
                       Image img = image.getScaledInstance(250, 150, BufferedImage.SCALE_SMOOTH);
 
                       ImageIO.write(toBufferedImage(img), "jpg", new File(USER+".jpg"));
                       File fileToSend = new File(USER+".jpg");
 
                       InputStream in = new BufferedInputStream(new FileInputStream(fileToSend));
                       in.read(buffer,len+1,8192-(len+1));
                       out = soc.getOutputStream();
                       out.write(buffer);
                       Thread.sleep(1000);
                   }
               }catch(AWTException | HeadlessException | IOException | InterruptedException e){
                    //Logger.getLogger(ConnectorThread.class.getName()).log(Level.WARNING,null,e);
                    try {
                        Logger.getLogger(ConnectionClient.class.getName()).log(Level.ALL,null,e);
                        if(soc != null){
                            System.err.println("Server disconnected. Resetting connection...");
                            soc.close();
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(ConnectorThread.class.getName()).log(Level.SEVERE, null, ex);
                    }
                   soc = null;
               }
            
        }
        
       System.out.println("Connection stopped.");
       INSIDE = null;
       //System.out.print(">");
    }
   
    protected void stopThread() throws IOException{
        if(INSIDE != null){
            INSIDE.interrupt();
            saveSettings();
        }
    }
        private static BufferedImage toBufferedImage(Image img){
            if (img instanceof BufferedImage){
                return (BufferedImage) img;
            }
 
            // Create a buffered image with transparency
            BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.SCALE_SMOOTH);
 
            // Draw the image on to the buffered image
            Graphics2D bGr = bimage.createGraphics();
            bGr.drawImage(img, 0, 0, null);
            bGr.dispose();
 
            // Return the buffered image
            return bimage;
        }
    protected void setAddress(String ip){
        this.IP = ip;
    }   
    
    protected void setPort(int port){
        this.PORT = port;
    }
    
    void saveSettings() throws IOException{
        File settings = new File("settings");
        settings.createNewFile();
        
        FileWriter fw = new FileWriter(settings);
        fw.write(IP+":"+PORT);
        fw.close();
    }
    
    @Override
    public boolean isInterrupted(){
        if(INSIDE != null){
            return INSIDE.isInterrupted();
        }
        return true;
    }

    protected boolean loadSettings() throws FileNotFoundException, IOException {
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

    private void parseSettings(String s) {
        String[] tmp = s.split(":");
        IP = tmp[0];
        PORT = Integer.parseInt(tmp[1].trim());
    }
}