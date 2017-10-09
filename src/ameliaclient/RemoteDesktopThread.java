/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ameliaclient;

import ameliaclient.nativesupport.DeviceType;
import ameliaclient.nativesupport.NativeControllerFactory;
import ameliaclient.nativesupport.NativeMouseController;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author obsidiam
 */
public class RemoteDesktopThread extends Thread implements Runnable {
    private Thread TH;
    private static double x = 0.0, y = 0.0;
    private static Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
    private static String ip = "localhost";
    private static boolean hasMouseStateChanged = false, hasKeyboardStateChanged = false;
    
    RemoteDesktopThread(String ip){
        this.ip = ip;
    }
    
    @Override
    public void start(){
        if(TH == null){
            TH = new Thread(this,"RemoteDesktopThread");
            TH.start();
        }
    }
    
    @Override
    public void run(){
        while(TH != null){
            if(!TH.isInterrupted()){       
                System.out.println("Attempting to download data...");
                try {
                    downloadData();
                } catch (UnknownHostException ex) {
                    Logger.getLogger(RemoteDesktopThread.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException | InterruptedException ex) {
                    Logger.getLogger(RemoteDesktopThread.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private void downloadData() throws SocketException, UnknownHostException, IOException, InterruptedException{
      DatagramSocket clientSocket = new DatagramSocket();
      System.out.println("Starting remote desktop session with "+ip);
      InetAddress IPAddress = InetAddress.getByName(ip);
          byte[] sendData;
          byte[] receiveData = new byte[16384];
          
          String sentence = "hndshk";
          
          sendData = sentence.getBytes();
          
          DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 7998);
          clientSocket.send(sendPacket);
          DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
          
          clientSocket.receive(receivePacket);
          System.out.println("Received.");
          String modifiedSentence = "error";
          
          modifiedSentence = processData(receivePacket.getData());
          
          String first = modifiedSentence.split("_")[0];
          
          if(modifiedSentence.contains("_")){
              String second = modifiedSentence.split("_")[1];
              if(!second.isEmpty()){
                if(!second.contains("\n")){
                   System.out.println("TEXT FROM SERVER: "+second.trim());
                }else{
                    System.out.println("TEXT FROM SERVER: ENTER");
                }
                   
              }else{
                  System.out.println("EMPTY");
              }
          }
          
          double xloc = Double.parseDouble(first.split(":")[0]);
          double yloc = Double.parseDouble(first.split(":")[1]);
          if(x != xloc && y != yloc){
            System.out.println("FROM SERVER:" +x+" "+y * screenDim.height);
            x = xloc * screenDim.width;
            y = yloc * screenDim.height;
          }
          executeOperations();
          System.out.println("Data downloaded.");
          Thread.sleep(1000);
      
    }
    
    private static String processData(byte[] data) {
       
        String out = "";
      
        for(byte b : data){
            if(b != -2){
                System.out.println("No mouse changes.");
                continue;
            }
                if(((int)b) == -1){
                    hasMouseStateChanged = true;
                    System.out.println("Mouse changes.");
                    out += ":";
                    continue;
                }
            
            if(b == -128){
                out += "_";
                hasKeyboardStateChanged = true;
                continue;
            }else if(b == -127){
                System.out.println("No keyboard changes.");
                break;
            }
            out += (char)b;
            //System.out.println(b);
        }
        return out;
    }
    
    private void executeOperations() {
           NativeControllerFactory mouseController = new NativeControllerFactory(DeviceType.MOUSE);
           NativeMouseController nativeMouseController = (NativeMouseController)mouseController.getControllerInstance();
           if(hasMouseStateChanged){
               Double tmpx = x;
               Double tmpy = y;
               nativeMouseController.sendCoords(tmpx.intValue(), tmpy.intValue());
               nativeMouseController.nativeMouseClick(1);
               System.out.println(x+","+y);
           }
    }
}
