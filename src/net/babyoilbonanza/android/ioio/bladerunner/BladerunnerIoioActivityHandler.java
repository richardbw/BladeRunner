/**
 * 
 */
package net.babyoilbonanza.android.ioio.bladerunner;

import net.babyoilbonanza.android.ioio.bladerunner.util.Alert;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * @author rbw
 *
 */
public class BladerunnerIoioActivityHandler extends Handler
{
    private static final String tag = BladerunnerIoioActivityHandler.class.getPackage().getName() + "]" + BladerunnerIoioActivityHandler.class.getSimpleName();
    
    public static final int FLASH_START = 0;
    public static final int FLASH_END   = 1;
    public static final int BEEP_START  = 2;
    public static final int BEEP_END    = 4;
    
    private BladerunnerIoioActivity _main;

    public BladerunnerIoioActivityHandler(BladerunnerIoioActivity main)
    {
        this._main = main;
    }
    
    

    @Override 
    public void handleMessage(Message msg) 
    {
        switch (msg.what) 
        {
        case FLASH_START:
            _main.beepColourTextView.setBackgroundResource(R.color.flash_yellow);
            break;
        case FLASH_END:
            Log.d(tag, "Setting Background Resource to : "+ _main.currentBgColour);
            _main.beepColourTextView.setBackgroundResource(_main.currentBgColour);
            break;
        case BEEP_START:           
            //_main.startBeep();
            _main.setDescription("Beeping");
            break;            
        case BEEP_END:
            _main.endBeep();
            _main.setDescription("");
            
            break;            
            
            
        default:
            Alert.i().error("Unknown 'what': "+msg.what);
        }
    }
    

}
