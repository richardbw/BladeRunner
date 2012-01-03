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
package net.babyoilbonanza.android.ioio.bladerunner.util;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;

/**
 * @author rbw
 *
 */
public class Alert {
	
	private static boolean ALERT_LOG = true;
	private static Alert _inst;
	private static Context CONTEXT;
	
	private Alert() {;}
	public Alert(Context c) {CONTEXT=c; _inst = new Alert();}
	public static Alert i() {return _inst;}
	
	
	public void warning(String s) {
		dialog("Warning", s);
	}
	public void error(String s) {
		dialog("Error", s);
	}
	
	public void dialog(String t, String s) {
		(new AlertDialog.Builder(CONTEXT))
			.setTitle(t)
			.setMessage(s)
			.setNeutralButton("Okay", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.dismiss();
		           }
		       })
			.create()
		.show();
	}
	
	public void spinner(String s) {
	    (new ProgressDialog(CONTEXT)).show();
	}

	public void alert(String s) {
		Toast.makeText(CONTEXT, 
				s, Toast.LENGTH_LONG
		).show();
	}
	
	public static void setLogDisplay(boolean b) {ALERT_LOG=b;}

	
	public void bummer(Exception e) {
		dialog("Exception", e.getMessage());
		Log.e("Alert", "Exception!?:\n"+e.getMessage(), e);
		e.printStackTrace();
	}
	
	public void lalert(String tag, String string) {		
		Log.d(tag, string);
		if (ALERT_LOG) 
			Toast.makeText(CONTEXT, 
				tag+":\n"+string, 
				Toast.LENGTH_SHORT
			).show();  
	}
	
	public boolean confirm(String s) 
	{
		boolean retval = true;
		(new AlertDialog.Builder(CONTEXT))
			.setTitle("Confirm")
			.setMessage(s)
			.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   //((Main)CONTEXT).saveTargetDeviceName();
		        	   dialog.dismiss();
		           }
		       })
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		               dialog.dismiss();
		           }
		       })
			.create()
			.show();
		return retval;
	}

}
