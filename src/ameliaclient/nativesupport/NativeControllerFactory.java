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
public class NativeControllerFactory {
    
    static{
        System.load(Platform.isLinux() ? "/home/lukas/Desktop/libJXhooker.so" : "C:\\Users\\lukas\\Desktop\\jwmhooker.dll");
        //System.setProperty("jna.debug_load", "true");
        
        Native.loadLibrary(Platform.isLinux() ? "X11" : "user32.dll" , AbstractNativeController.ExtLib.class);
    }

    public static AbstractNativeController getControllerInstance(DeviceType type){
        switch(type){
            case MOUSE:
                return NativeMouseController.getInstance();

            case KEYBOARD:
                return NativeKeyboardController.getInstance();
               
        }
        return null;
    }
}
