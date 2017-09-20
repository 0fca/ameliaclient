/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ameliaclient.data;

import java.net.Socket;

/**
 *
 * @author obsidiam
 */
public class ConnectionData {
    private Thread INSIDE = null;
    private String IP = "localhost";
    private int PORT = 7999;
    private String ext = "JPG";
    private Socket s;
    
    public void setThread(Thread t){
        INSIDE = t;
    }
    
    public Thread getThread(){
        return INSIDE;
    }
    
    public void setIp(String ip){
        IP = ip;
    }
    
    public String getIp(){
        return IP;
    }
    
    public void setPort(int port){
        PORT = port;
    }
    
    public int getPort(){
        return PORT;
    }
    
    
    public String getExt(){
        return ext;
    }
    
    public void setSocket(Socket s){
        this.s = s;
    }
    
    public String getLocalIp(){
        return s != null ? s.getLocalAddress().getHostName() : null;
    }
}
