/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package com.ociweb;


import static com.ociweb.iot.maker.FogCommandChannel.I2C_WRITER;

import com.ociweb.iot.grove.lcd_rgb.Grove_LCD_RGB;
import com.ociweb.iot.maker.AnalogListener;
import com.ociweb.iot.maker.DigitalListener;
import com.ociweb.iot.maker.FogCommandChannel;
import com.ociweb.iot.maker.FogRuntime;
import com.ociweb.iot.maker.Port;
import com.ociweb.pronghorn.util.Appendables;

public class IoTBehavior implements AnalogListener, DigitalListener{
    
    private final FogCommandChannel channel;
    private boolean setSize;
    
    private int fullTank;
    
    public IoTBehavior(FogRuntime runtime) {
        
        channel = runtime.newCommandChannel(I2C_WRITER);
        
    }
    @Override
	public void digitalEvent(Port port, long time, long durationMillis, int value) {
		setSize = (1 == value);
	}
    
    @Override
    public void analogEvent(Port port, long time, long durationMillis, int average, int value) {
    	if (setSize) {
    		fullTank = value;
    	}
    	else if (value>fullTank) {
            System.out.println("Check equipment, tank is deeper than expected");
        } else {
            int remainingDepth = 100*((fullTank-value)/fullTank);;
            
            StringBuilder builder = new StringBuilder();
            Appendables.appendFixedDecimalDigits(builder, remainingDepth, 100);
            
            builder.append("percent \n");
            builder.append("full");
            
            Grove_LCD_RGB.commandForColor(channel, 200, 200, 180);
            Grove_LCD_RGB.commandForText(channel, builder);
            
            
        }
        
        
        
    }
    
}
