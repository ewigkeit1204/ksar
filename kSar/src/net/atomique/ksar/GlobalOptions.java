/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.atomique.ksar;

import java.awt.Color;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;
import net.atomique.ksar.UI.Desktop;
import net.atomique.ksar.XML.ColorConfig;
import net.atomique.ksar.XML.OSConfig;

/**
 *
 * @author Max
 */
public class GlobalOptions {

    private static GlobalOptions instance = new GlobalOptions();

    public static GlobalOptions getInstance() {
        return instance;
    }

    public static Object hasUI() {
        if ( UI != null ) {
            return true;
        }
        return false;
    }

    GlobalOptions () {
        colorlist  = new HashMap<String,ColorConfig>();
        OSlist = new HashMap<String, OSConfig>();
        if ( Config.getLocal_config() == -1 ||  Config.getLocal_config() == 0 ) {
            InputStream is = this.getClass().getResourceAsStream("/Config.xml");
            XMLConfig tmp = new XMLConfig(is);            
        }
        if ( Config.getLocal_config() == 1 ) {
            // load file config

        }
    }

    public static Desktop UI = null;

    public static Desktop getUI() {
        return UI;
    }

    public static void setUI(Desktop UI) {
        GlobalOptions.UI = UI;
    }

    public static String getUserhome() {
        return userhome;
    }

    public static String getUsername() {
        return username;
    }

    public static HashMap<String,ColorConfig> getColorlist() {
        return colorlist;
    }

    public static HashMap<String, OSConfig> getOSlist() {
        return OSlist;
    }

    
    public static Color getDataColor(String s ) {
        ColorConfig tmp = colorlist.get(s);
        if ( tmp != null ) {
            return tmp.getData_color();
        } else {
            System.err.println("WARN: color not found for tag " + s);
        }
        return null;
    }

    public static OSConfig getOSinfo( String s ) {
        return OSlist.get(s);
    }
    public static boolean isDodebug() {
        return dodebug;
    }

    public static void setDodebug(boolean do_debug) {
        GlobalOptions.dodebug = do_debug;
    }

    public static String getCLfilename() {
        return CLfilename;
    }

    public static void setCLfilename(String CL_filename) {
        GlobalOptions.CLfilename = CL_filename;
    }



    private static Properties systemprops = System.getProperties();
    private static String userhome = (String) systemprops.get("user.home") + systemprops.get("file.separator");
    private static String username = (String) systemprops.get("user.name");
    private static HashMap<String,ColorConfig> colorlist;
    private static HashMap<String, OSConfig> OSlist;
    private static boolean dodebug= false;
    private static String CLfilename = null;
    
    
}
