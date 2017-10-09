/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ameliaclient.nativesupport;

import com.sun.jna.Native;
import com.sun.jna.Platform;

/**
 *
 * @author obsidiam
 */
public class NativeKeyboardController extends AbstractNativeController{

    private static volatile NativeKeyboardController instance = new NativeKeyboardController();
    
    static{
        //System.out.println(System.getProperties());
        System.load("/home/lukas/Desktop/libnativeioaccess.so");
        //System.load("/home/lukas/Desktop/libjnidispatch.so");
        
        System.setProperty("jna.debug_load", "true");
        Native.loadLibrary(Platform.isLinux() ? "X11" : "user32.dll" , ExtLib.class);
    }
    
    private NativeKeyboardController(){}
    
    public synchronized static NativeKeyboardController getInstance(){
        return instance;
    }
    
    public native void nativeKeyPress(char c);
    public native String getKeyboardLayout();
    
    @Override
    public DeviceType getControllerType() {
       return DeviceType.KEYBOARD;
    }
    
}
