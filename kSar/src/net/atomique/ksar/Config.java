/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar;

import java.awt.Font;
import java.io.File;
import java.util.ArrayList;
import java.util.prefs.Preferences;
import javax.swing.UIManager;

/**
 *
 * @author Max
 */
public class Config {

    private static Preferences myPref ;
    private static Config instance = new Config();

    public static Config getInstance() {
        return instance;
    }

    Config() {        
        myPref = Preferences.userNodeForPackage(Config.class);
        /*
         * load default value or stored value
         */
        setLandf(myPref.get("landf", UIManager.getLookAndFeel().getName()));
        setLastReadDirectory(myPref.get("lastReadDirectory", null));
        setLastExportDirectory(myPref.get("lastExportDirectory",null));
        setNumber_host_history(myPref.getInt("HostHistory", 0));
        setLocal_config(myPref.getInt("hasconfig", -1));
        for (int i=0; i<getNumber_host_history(); i++) {
            host_history.add(myPref.get("HostHistory_"+i, null));
        }

        
    }

    public static void save() {
        if ( myPref == null) {
            return;
        }
        myPref.put("landf", landf);
        if (lastReadDirectory != null) {
            myPref.put("lastReadDirectory", lastReadDirectory.toString());
        }
        if ( lastExportDirectory != null ) {
            myPref.put("lastExportDirectory", lastExportDirectory.toString());
        }
        for (int i=0; i< host_history.size(); i++) {
            myPref.put("HostHistory_"+i, host_history.get(i));
        }
        myPref.putInt("HostHistory", host_history.size());
        myPref.putInt("hasconfig", local_config);

    }

    public static String getLandf() {
        return landf;
    }

    public static void setLandf(String landf) {
        Config.landf = landf;
    }

    public static File getLastReadDirectory() {
        return lastReadDirectory;
    }

    public static void setLastReadDirectory(String lastReadDirectory) {
        if ( lastReadDirectory != null) {
            Config.lastReadDirectory = new File(lastReadDirectory);
        }
    }
    public static void setLastReadDirectory(File lastReadDirectory ) {
        Config.lastReadDirectory = lastReadDirectory;
    }

    public static File getLastExportDirectory() {
        return lastReadDirectory;
    }
    
    public static void setLastExportDirectory(String lastExportDirectory) {
        if ( lastExportDirectory != null) {
            Config.lastExportDirectory = new File(lastExportDirectory);
        }
    }
    public static void setLastExportDirectory(File lastExportDirectory ) {
        Config.lastExportDirectory = lastExportDirectory;
    }

    public static String getLastCommand() {
        return lastCommand;
    }

    public static void setLastCommand(String lastCommand) {
        Config.lastCommand = lastCommand;
    }

    public static ArrayList<String> getHost_history() {
        return host_history;
    }

    public static void addHost_history(String e) {
        host_history.add(e);
    }
    
    public static int getNumber_host_history() {
        return number_host_history;
    }

    public static void setNumber_host_history(int number_host_history) {
        Config.number_host_history = number_host_history;
    }

    public static Font getDEFAULT_FONT() {
        return DEFAULT_FONT;
    }

    public static int getLocal_config() {
        return local_config;
    }

    public static void setLocal_config(int local_config) {
        Config.local_config = local_config;
    }

    



    private static String landf;
    private static File lastReadDirectory;
    private static File lastExportDirectory;
    private static String lastCommand;
    private static int number_host_history;
    private static int local_config;
    private static ArrayList<String> host_history = new ArrayList<String>();
    public static final Font DEFAULT_FONT = new Font("SansSerif", Font.BOLD, 18);
}