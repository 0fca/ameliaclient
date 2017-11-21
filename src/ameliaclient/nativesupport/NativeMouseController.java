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
public class NativeMouseController extends AbstractNativeController{
    private static volatile NativeMouseController instance = new NativeMouseController();
    

    private NativeMouseController(){}
    
    public synchronized static NativeMouseController getInstance(){
        return instance;
    }
    
    private native boolean nativeMouseClick(int button);
    public native void sendCoords(int x, int y);

    @Override
    public DeviceType getControllerType() {
       return DeviceType.MOUSE;
    }
    
    public boolean doMouseClick(int button){
        return button <= 3 && button >= 1 ? nativeMouseClick(button) : false;
    }
}
