/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ameliaclient.nativesupport;

import ameliaclient.data.InputDataType;




/**
 *
 * @author obsidiam
 */
public class NativeKeyboardController extends AbstractNativeController{

    
    
    private static volatile NativeKeyboardController instance = new NativeKeyboardController();
    
    private NativeKeyboardController(){}
    
    public synchronized static NativeKeyboardController getInstance(){
        return instance;
    }
    
    private native char nativeKeyPress(String c, int modifier);
    private native char nativeTypeSequence(String sequence, int modifier);
    public native String getKeyboardLayout();
    
    @Override
    public DeviceType getControllerType() {
       return DeviceType.KEYBOARD;
    }
    
    public boolean inputKeyboardData(String c, int modifier,InputDataType e){
        if(modifier <= 7 && modifier >= 0){
            switch(e){
                case PRESS:
                    return nativeKeyPress(c,modifier) != 0;

                case SEQUENCE:
                    return nativeTypeSequence(c,modifier) != 0;

                default: 
                    return false;
            }
        }else{
            return false;
        }
    }
    
}
