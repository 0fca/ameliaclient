/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ameliaclient.nativesupport;

import com.sun.jna.Library;

/**
 *
 * @author obsidiam
 */
public abstract class AbstractNativeController {
    interface ExtLib extends Library {//only for loading X11.
        
    }
    
    public abstract DeviceType getControllerType();
}
