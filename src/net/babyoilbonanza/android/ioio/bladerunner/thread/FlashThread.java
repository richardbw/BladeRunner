/**
 * 
 */
package net.babyoilbonanza.android.ioio.bladerunner.thread;

import static net.babyoilbonanza.android.ioio.bladerunner.BladerunnerIoioActivity.tag;
import net.babyoilbonanza.android.ioio.bladerunner.BladerunnerIoioActivity;
import net.babyoilbonanza.android.ioio.bladerunner.BladerunnerIoioActivityHandler;
import net.babyoilbonanza.android.ioio.bladerunner.util.Alert;
import android.os.Message;
import android.util.Log;

/**
 * This thread tells the {@link BeepHandler} to set the background to white for 
 * DEFAULT_FLASH_MSEC seconds, when the button is pressed.
 * 
 * @author rbw
 *
 */
public class FlashThread extends Thread
{
    private static final long   DEFAULT_FLASH_MSEC = 100;
    private static FlashThread  _inst;
    public BladerunnerIoioActivity  _main;
    private boolean             isRunning          = false;

    FlashThread(BladerunnerIoioActivity main) {
        super();
        this._main = main;
    }

    
    @Override
    public void run()
    {
        isRunning = true;
        
        Log.d(tag, "*Start flash*");
        _main.beepHandler.sendMessage(Message.obtain(_main.beepHandler, BladerunnerIoioActivityHandler.FLASH_START));

        try
        {
            Thread.sleep(DEFAULT_FLASH_MSEC);
        }
        catch (InterruptedException e)
        {
            Alert.i().bummer(e);
        }

        Log.d(tag, "*/End flash*");
        _main.beepHandler.sendMessage(Message.obtain(_main.beepHandler, BladerunnerIoioActivityHandler.FLASH_END));

        Log.d(tag, "Thread Done.");
        _inst.isRunning = false;
    }
    
    public static void restart() {
        if (_inst.isRunning) return;
        
        try
        {
            do
            { //do we need all this?  it's copied from an old proj.... //rbw20111225, xmas day
                Log.d(tag, "waiting to stop.., isAlive: "+_inst.isAlive());
                FlashThread.sleep(10);;//spin
            } while (_inst.isAlive());
            
            BladerunnerIoioActivity m = _inst._main;           // save previous 'main' and..
            _inst = new FlashThread(m);   //   ..create new thread.
            _inst.start();
        } catch (Exception e)
        {
            Alert.i().bummer(e);
        }
    }
    
    public static void end() {
        _inst.isRunning = false;
    }


    public static FlashThread i(BladerunnerIoioActivity m)
    {
        if (_inst == null) _inst = new FlashThread(m);
        return _inst;
    }

}
