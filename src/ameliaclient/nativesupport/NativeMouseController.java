/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ameliaclient.nativesupport;

import com.sun.jna.Native;
import com.sun.jna.Platform;
import java.io.File;

/**
 *
 * @author obsidiam
 */
public class NativeMouseController extends AbstractNativeController{
    private static volatile NativeMouseController instance = new NativeMouseController();
    
    static{
        //System.out.println(System.getProperties());
       
        System.load(System.getProperty("user.dir")+File.separator+"libnativeioaccess.so");
        
        //System.load("/home/lukas/Desktop/libjnidispatch.so");
        
        System.setProperty("jna.debug_load", "true");
        Native.loadLibrary(Platform.isLinux() ? "X11" : "user32.dll" , ExtLib.class);
    }
    
    private NativeMouseController(){}
    
    public synchronized static NativeMouseController getInstance(){
        return instance;
    }
    
    public native boolean nativeMouseClick(int button);
    public native void sendCoords(int x, int y);

    @Override
    public DeviceType getControllerType() {
       return DeviceType.MOUSE;
    }
}
