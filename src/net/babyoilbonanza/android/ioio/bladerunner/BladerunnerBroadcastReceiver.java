/**
 * 
 */
package net.babyoilbonanza.android.ioio.bladerunner;

import net.babyoilbonanza.android.ioio.bladerunner.util.Debug;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import static net.babyoilbonanza.android.ioio.bladerunner.BladerunnerIoioActivity.tag;

/**
 * @author rbw
 *
 */
public class BladerunnerBroadcastReceiver extends BroadcastReceiver
{


    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.d(tag, "* start onReceive()");
        Debug.logGetters(context);
        Debug.logGetters(intent);

    }
    
    
    
    

}
