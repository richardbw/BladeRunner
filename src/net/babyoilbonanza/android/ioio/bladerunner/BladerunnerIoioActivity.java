/*
    Blade runner IOIO Foil Fencing app.
    Copyright (C) 2012  Richard Barnes-Webb

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>. 

 */
package net.babyoilbonanza.android.ioio.bladerunner;

import ioio.lib.api.DigitalInput;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.AbstractIOIOActivity;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

import net.babyoilbonanza.android.ioio.bladerunner.thread.BeepThread;
import net.babyoilbonanza.android.ioio.bladerunner.thread.FlashThread;
import net.babyoilbonanza.android.ioio.bladerunner.util.Alert;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.AnalogClock;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 * @author rbw
 *
 */
public class BladerunnerIoioActivity extends AbstractIOIOActivity
{
    public static final String tag                 = "pint_test01";

    // set main instantances in singletons:
    protected Alert            a                   = new Alert(this);
    protected BeepThread       b                   = BeepThread.i(this);
    protected FlashThread      f                   = FlashThread.i(this);

    public BladerunnerIoioActivityHandler      beepHandler         = new BladerunnerIoioActivityHandler(this);

    
    public TextView                       beepColourTextView;
    public TextView                       beepLengthTextView;
    public MediaPlayer                    mMediaPlayer;
    public CheckBox                       armed_checkBox;
    private TextView                      main_desc;
    private ToggleButton                  toggleBeepButton;
    private TextView                      weapon_pin;
    private TextView                      lame_pin;
    private ImageView                     isOn_weapon;
    private ImageView                     isOn_lame;
    
    
    public String _hardware_ver     = null;
    public String _app_firmware_ver = null;
    public String _bootloader_ver   = null;
    public String _ioiolib_ver      = null;




    private static final boolean      NOT_TESTING_FOR_REAL = false;
    private static final boolean      TESTING_BEEP         = true;



    /** Called with the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_layout);

        beepColourTextView  = (TextView)        findViewById(R.id.beepColourTextView);
        beepLengthTextView  = (TextView)        findViewById(R.id.beepLengthTextView);
        armed_checkBox      = (CheckBox)        findViewById(R.id.armed_checkBox);
        main_desc           = (TextView)        findViewById(R.id.main_desc);
        toggleBeepButton    = (ToggleButton)    findViewById(R.id.toggleBeepButton);
        weapon_pin          = (TextView)        findViewById(R.id.weapon_pin);
        lame_pin            = (TextView)        findViewById(R.id.lame_pin);
        isOn_weapon         = (ImageView)       findViewById(R.id.isOn_weapon);
        isOn_lame           = (ImageView)       findViewById(R.id.isOn_lame);

        toggleBeepButton.setOnClickListener(buttonListener);
        ((Button) findViewById(R.id.testBeepButton)).setOnClickListener(buttonListener);
        ((Button) findViewById(R.id.testIoioButton)).setOnClickListener(buttonListener);
        ((SeekBar)findViewById(R.id.beepLengthSeekBar)).setOnSeekBarChangeListener(beepLengthSeekBarListener);
        
        beepLengthSeekBarListener.onProgressChanged(((SeekBar)findViewById(R.id.beepLengthSeekBar)), Integer.parseInt( getString(R.string.default_beeplen) ), false);
        
        mMediaPlayer = MediaPlayer.create(this, R.raw.beep_14);
        mMediaPlayer.setLooping(true); 
        
        getWindow().addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON); //block screensaver (http://stackoverflow.com/questions/6290094/android-disable-screen-saver)
        
    }
   

    
    

    @Override
    protected IOIOThread createIOIOThread()
    {
        Log.d(tag, getTS()+"---> createIOIOThread() <---");
        return new IOIOThread();
    }
    
    
    

    private String getTS()
    {
        return (new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" )).format(new Date());
    }




    OnClickListener buttonListener = new OnClickListener() {
        public void onClick(View v) {
            switch ( v.getId()  )
            {
                case R.id.testBeepButton:
                    startBeep(TESTING_BEEP);                    
                    break;

                case R.id.testIoioButton:
                    showVersionStringDialog();
                    break;

                case R.id.toggleBeepButton:
                    if ( ! toggleBeepButton.isChecked() ) endBeep();
                    break;

                default:
                    Log.e(tag, "Unsupported view in buttonListener: "+v.getId());
                    break;
            }
        }
    };


    
    protected void showVersionStringDialog()
    {
        Log.d(tag, getTS()+"--> start showVersionStringDialog() <--");
        
        String versionStr = "Identified IOIO versions:\n"+
            "\nHardware       : "+_hardware_ver     +
            "\nApp firmware   : "+_app_firmware_ver +
            "\nBootloader     : "+_bootloader_ver   +
            "\nIoiolib        : "+_ioiolib_ver      +
            "\n";
        
        try
        {
            versionStr+="\nNetwork:";
            for ( Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); )
            {
                NetworkInterface intf = en.nextElement();
                versionStr+="\n- "+intf.getName()+": ";
                for ( Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); )
                {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    versionStr+="\n    "+inetAddress.getHostName()+" (" +inetAddress.getHostAddress()+") ";
                }
            }
        }
        catch (Exception e)
        {
            Alert.i().bummer(e);
        }
        
        (new AlertDialog.Builder(this))
            .setTitle("Last versions retrieved")
            .setMessage(versionStr)
            .setNeutralButton("Okay", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                   }
               })
            .create()
        .show();
    }//

    
    
    
    
    SeekBar.OnSeekBarChangeListener beepLengthSeekBarListener = new SeekBar.OnSeekBarChangeListener()
    {   @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
        {
            beepLengthTextView.setText(progress+"");
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar){}
        @Override
        public void onStopTrackingTouch(SeekBar seekBar){}
    };

    

    public void startBeep(boolean testing)
    {
        startBeep(testing, false);
    }


    
    
    public void startBeep(boolean testing, final boolean onTarget)
    {
        Log.d(tag, "Starting startBeep. (testing: "+testing+")");
        
        if ( mMediaPlayer.isPlaying() ) 
        { Log.i(tag, getTS()+": is currently beeping; won't restart beep."); return; }
        
        
        if ( ! armed_checkBox.isChecked() && ! testing )   //only bother if isChecked || testing
        { Log.i(tag, getTS()+": Box isn't armed; won't beep."); return; }
        
        
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
        
                if ( ! toggleBeepButton.isChecked() )
                { Log.i(tag, getTS()+": beepbutton is off; won't beep."); return; }
                else {
                    BeepThread.restart();
                    mMediaPlayer.start();
                }
                
                if ( onTarget  )
                {
                    FlashThread.restart();
                    beepColourTextView.setBackgroundResource(R.color.beep_green);
                    beepColourTextView.setText(R.string.hit_on_target);  
                }
                else
                {
                    beepColourTextView.setBackgroundResource(R.color.beep_white);
                    beepColourTextView.setText(R.string.hit_off_target);
                    beepColourTextView.setTextColor(R.color.beep_darkgrey);
                }
            }
        });
    }


    public void endBeep()
    {
        if ( ! mMediaPlayer.isPlaying() ) 
        { Log.i(tag, getTS()+": Media isn't playing."); return; }
        
        try
        {
            mMediaPlayer.stop();
            mMediaPlayer.prepare();
            mMediaPlayer.seekTo(0);
        }
        catch (Exception e)
        {
            Alert.i().bummer(e);
        }
        
        beepColourTextView.setBackgroundResource(R.color.beep_darkgrey);
        beepColourTextView.setText("");
    }
    

    
    private void setCircuitClosedLights(final boolean weapon, final boolean lame) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                isOn_weapon.setImageResource( weapon? android.R.drawable.button_onoff_indicator_on:android.R.drawable.button_onoff_indicator_off);
                isOn_lame.setImageResource(     lame? android.R.drawable.button_onoff_indicator_on:android.R.drawable.button_onoff_indicator_off);
            }
        });
    }
    
    public void setDescription(final String desc) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                main_desc.setText(desc);
            }
        });
    }
    

    
    void disconnected() throws InterruptedException {
        Alert.i().alert(getTS()+"*IOIO Disconnected.*");
    }
    
    
    //-----------------------------------------------------------------
    
    
    
    
    class IOIOThread extends AbstractIOIOActivity.IOIOThread 
    {
        
        private DigitalOutput _led;
        private DigitalInput  _weaponPinOutput;
        private DigitalInput  _lamePinOutput;
        
        
        @Override
        protected void setup() throws ConnectionLostException {
            Log.d(tag, getTS()+"---> IOIOThread setup() <---");
            _hardware_ver     = ioio_.getImplVersion(IOIO.VersionType.HARDWARE_VER);      
            _app_firmware_ver = ioio_.getImplVersion(IOIO.VersionType.APP_FIRMWARE_VER);
            _bootloader_ver   = ioio_.getImplVersion(IOIO.VersionType.BOOTLOADER_VER);
            _ioiolib_ver      = ioio_.getImplVersion(IOIO.VersionType.IOIOLIB_VER);
            
            int wp = Integer.parseInt(weapon_pin.getText()+"");
            int lp = Integer.parseInt(lame_pin.getText()+"");
            
            try
            {
                Log.d(tag, "Opening Pins> led: "+IOIO.LED_PIN+", _weaponPinOutput: "+wp+", _lamePinOutput: "+lp);
                _led                = ioio_.openDigitalOutput(IOIO.LED_PIN, true);
                _weaponPinOutput    = ioio_.openDigitalInput(wp, DigitalInput.Spec.Mode.PULL_UP);
                _lamePinOutput      = ioio_.openDigitalInput(lp, DigitalInput.Spec.Mode.PULL_UP);
            }
            catch (Exception e)
            {
                Log.e(tag, getTS()+"---> IOIOThread setup() Exception: "+e.getMessage(), e);
                e.printStackTrace();
                Alert.i().bummer(e);
                throw new ConnectionLostException(e);
            }
        }
        
        
        
        @Override
        protected void loop() throws ConnectionLostException 
        {
            
            try 
            {
                boolean weapon = !_weaponPinOutput.read();
                if ( ! weapon ) { sleep(1500);
                    setDescription("sleeping..     ");
                }; //allow some time for the lame to register..
                
                
                boolean lame   = !_lamePinOutput.read();
                String msg = getTS()+"->weapon: "+weapon+", lame: "+ lame;
                setDescription(msg);
                Log.d(msg, msg);
                
                
                setCircuitClosedLights(weapon, lame);
                
                _led.write(!armed_checkBox.isChecked());
                if ( ! armed_checkBox.isChecked() ) return;    //only bother if isChecked
                
                if ( ! weapon ) { //tip has been depressed
                    startBeep(NOT_TESTING_FOR_REAL, lame);
                }
                
//                setDescription("Waiting for a switch break..");
//                _weaponPinOutput.waitForValue(true);
//                setDescription("Got a result: circuit broken.");
                
                
                
                
                

                sleep(100);
            } catch (Exception e) {
                Log.e(tag, getTS()+"---> IOIOThread loop() Exception: "+e.getMessage(), e);
                e.printStackTrace();
                setDescription(" IOIOThread loop() Exception: "+e.getMessage());
                throw new ConnectionLostException(e);
            }
        }
        
        
    }

    
    
    
    

}


/* ******** SCRAP
 * 
 *  
 *                  boolean pin48read = _pin48.read();
                
                if ( !firstRun  && ( pin48read != lastPin48Value ) )
                    setDescription("Pin value has changed, from ["+lastPin48Value+"] to ["+pin48read+"]");
                
                setCheckedOnUi( pin48read );
                //test_checkBox.setChecked( pin48read );  ---> android.view.ViewRoot$CalledFromWrongThreadException
                
                Log.d(tag, getTS()+"---> IOIOThread loop() <---pin48: "+ pin48read);
                
                lastPin48Value = pin48read;
                firstRun = false;
                
 */
