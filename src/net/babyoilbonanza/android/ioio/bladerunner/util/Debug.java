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

import static net.babyoilbonanza.android.ioio.bladerunner.BladerunnerIoioActivity.tag;

import java.lang.reflect.Method;

import android.util.Log;
/**
 * @author rbw
 *
 */
public class Debug
{
    

    public static void logGetters(Object obj) {
        dumpGetters(obj, true);
    }
    
    
    public static void dumpGetters(Object obj, boolean toLog)
    {
        if ( obj == null ) {
            out( tag +": NULL parameter; dumpGetters("+obj+")", toLog );
            return;
        }
        out( tag + ": Getters for: "+ obj.getClass(), toLog);
        
        for (Method method: obj.getClass().getMethods() ) 
            if ( method.getName().startsWith("get") && method.getParameterTypes().length == 0 ) 
                try { System.out.println(String.format("  - %-30s:%s\t(%s)", 
                            method.getName()+"()", method.invoke(obj, (new Object[]{})), method.getReturnType() ) ); } 
                catch (Exception e) { e.printStackTrace(); }
    }
    
    
    public static void out(String mesg, boolean toLog) {
        if ( toLog )
        {
            Log.d(mesg, mesg);
        }
        else
        {
            System.out.println( mesg );
        }
        
    }
    



}
