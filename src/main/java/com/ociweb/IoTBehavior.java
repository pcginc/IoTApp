/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package com.ociweb;


import static com.ociweb.iot.maker.FogCommandChannel.I2C_WRITER;

import com.ociweb.gl.api.StartupListener;
import com.ociweb.iot.grove.lcd_rgb.Grove_LCD_RGB;
import com.ociweb.iot.grove.simple_analog.SimpleAnalogTwig;
import com.ociweb.iot.maker.AnalogListener;
import com.ociweb.iot.maker.DigitalListener;
import com.ociweb.iot.maker.FogCommandChannel;
import com.ociweb.iot.maker.FogRuntime;
import com.ociweb.iot.maker.Port;
import com.ociweb.pronghorn.util.Appendables;

public class IoTBehavior implements AnalogListener, DigitalListener, StartupListener {
   
	private final FogCommandChannel channel;
    private boolean startCalibration;
    private StringBuilder builder = new StringBuilder();
    private int percentFull;
    
    private int fullTank = SimpleAnalogTwig.UltrasonicRanger.range();
    
    public IoTBehavior(FogRuntime runtime) {
        
        channel = runtime.newCommandChannel(I2C_WRITER);
        
    }
    @Override
    public void startup() {
    	Grove_LCD_RGB.commandForColor(channel, 200, 200, 180);
    }
    
    @Override
	public void digitalEvent(Port port, long time, long durationMillis, int value) {
		if (value != 0) {
			startCalibration = true;
			percentFull = 0;
		}
	}
    
    @Override
    public void analogEvent(Port port, long time, long durationMillis, int average, int value) {
    	if (startCalibration) {
    		fullTank = value;
    		startCalibration = false;
    	}
         else {
            double full = 1.0 - ((double) value / (double) fullTank);
            percentFull = (int) (full * 100.0);
            // type conversion so you don't divide an integer by integer and get a decimal value
            if (value >= fullTank) {
            	percentFull = 0;
            }
            if (value == 0) {
            	percentFull = 100;
            }
            builder.setLength(0);
            Appendables.appendFixedDecimalDigits(builder, percentFull, 100);
            
            builder.append("percent\nfull");
           
            Grove_LCD_RGB.commandForText(channel, builder);
            
            
        }
        
        
        
    }
    
}
