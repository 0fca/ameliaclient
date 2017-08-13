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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
    //public boolean IS_CONNECTED = false;
    Socket soc = null;
    @Override
    public void start(){
        if(INSIDE == null){
            INSIDE = new Thread(this);
            INSIDE.start();
        }
    }
   
    @Override
    public void run(){
        System.out.println("Preparing socket...");
       
        while(!this.isInterrupted()){
            try{
               //System.out.println(new InetSocketAddress("192.168.0.103",7999).getAddress().isReachable(500));
               soc = new Socket("192.168.0.103",7999);
 
               System.out.println("Slave is listening on port 7999 and waiting for an idle...");
             
               OutputStream out;
 
               char[] user_chars = USER.toCharArray();
               int len = user_chars.length;
 
               byte[] buffer = new byte[8192];
 
               
                   System.out.println("Started...");
                   while(true){
 
                       buffer[0] = (byte)len;
                       for(int it = 1; it<=len; it++){
                           buffer[it] = (byte)((int)user_chars[it-1]);
                           //System.out.print(String.valueOf((char)((int)user_chars[it-1])));
                       }
 
                       BufferedImage image = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
                       Image img = image.getScaledInstance(150, 100, BufferedImage.SCALE_SMOOTH);
 
                       ImageIO.write(toBufferedImage(img), "jpg", new File(USER+".jpg"));
                       File fileToSend = new File(USER+".jpg");
 
                       InputStream in = new BufferedInputStream(new FileInputStream(fileToSend));
                       in.read(buffer,len+1,8192-(len+1));
                       out = soc.getOutputStream();
                       out.write(buffer);
                       Thread.sleep(2000);
                   }
               }catch(AWTException | HeadlessException | IOException | InterruptedException e){
                   //Logger.getLogger(ConnectorThread.class.getName()).log(Level.SEVERE,null,e);
                    try {
                        Logger.getLogger(ConnectionClient.class.getName()).log(Level.ALL,null,e);
                        if(soc != null){
                            System.err.println("Master disconnected. Resetting connection...");
                            soc.close();
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(ConnectorThread.class.getName()).log(Level.SEVERE, null, ex);
                    }
                   //soc = null;
               }
        }
       
    }
   
    public void stopThread() throws IOException{
        this.interrupt();
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
       
}