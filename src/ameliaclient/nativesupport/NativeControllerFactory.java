/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ameliaclient.nativesupport;

/**
 *
 * @author obsidiam
 */
public class NativeControllerFactory {
    DeviceType type = DeviceType.MOUSE;
    
    public NativeControllerFactory(DeviceType d){
        this.type = d;
    }
    
    public AbstractNativeController getControllerInstance(){
        switch(type){
            case MOUSE:
                return NativeMouseController.getInstance();

            case KEYBOARD:
                return NativeMouseController.getInstance();
               
        }
        return null;
    }
}
