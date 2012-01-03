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
 * @author rbw
 *
 */
public class BeepThread extends Thread
{
    
    
    private static BeepThread _inst;
    private BladerunnerIoioActivity _main;

    public BeepThread(BladerunnerIoioActivity m)
    {
        super();
        this._main = m;
    }

    public static void restart()
    {
        if (_inst.isAlive() ) return;
        
        
        try
        {
            do
            { //do we need all this?  it's copied from an old proj.... //rbw20111225, xmas day
                Log.d(tag, "waiting to stop.., isAlive: "+_inst.isAlive());
                FlashThread.sleep(10);;//spin
            } while (_inst.isAlive());
            
            BladerunnerIoioActivity m = _inst._main;           // save previous 'main' and..
            _inst = new BeepThread(m);                      //   ..create new thread.
            _inst.start();
        } catch (Exception e)
        {
            Alert.i().bummer(e);
        }
        
        
    }

    @Override
    public void run()
    {
        Log.d(tag, "*Start beep*");
        _main.beepHandler.sendMessage(Message.obtain(_main.beepHandler, BladerunnerIoioActivityHandler.BEEP_START));
        
        try
        {
            int sleepTime = Integer.parseInt( _main.beepLengthTextView.getText()+"" ) ;
            Log.d(tag, "Sleeping for ["+sleepTime+"] secs.");
            Thread.sleep( sleepTime * 1000 );
        }
        catch (Exception e)
        {
            Alert.i().bummer(e);
        }
        
        _main.beepHandler.sendMessage(Message.obtain(_main.beepHandler, BladerunnerIoioActivityHandler.BEEP_END));
        
        Log.d(tag, "*/End beep*");
        
    }

    public static BeepThread i(BladerunnerIoioActivity main)
    {
        if (_inst == null) _inst = new BeepThread(main);
        return _inst;
    }
    
    
    

}
