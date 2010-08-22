/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar;

import java.awt.Color;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
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
        if (UI != null) {
            return true;
        }
        return false;
    }

    GlobalOptions() {
        colorlist = new HashMap<String, ColorConfig>();
        OSlist = new HashMap<String, OSConfig>();
        ParserMap = new HashMap<String, Class>();
        if (Config.getLocal_config() == -1 || Config.getLocal_config() == 0) {
            InputStream is = this.getClass().getResourceAsStream("/Config.xml");
            XMLConfig tmp = new XMLConfig(is);
        }
        try {
            Class[] parserlist = getClasses("net.atomique.ksar.Parser");
            for (int i =0; i< parserlist.length; i++) {
                System.out.println("parser found " + parserlist[i].getName());
                String simplename = parserlist[i].getName().replaceFirst("net.atomique.ksar.Parser.","");
                System.out.println("simplename " + simplename);
                ParserMap.put(simplename,parserlist[i]);
            }
        } catch (ClassNotFoundException ex) {
            System.out.println("oups no parser");
        }

        
        if (Config.getLocal_config() == 1) {
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

    public static HashMap<String, ColorConfig> getColorlist() {
        return colorlist;
    }

    public static HashMap<String, OSConfig> getOSlist() {
        return OSlist;
    }

    public static Color getDataColor(String s) {
        ColorConfig tmp = colorlist.get(s);
        if (tmp != null) {
            return tmp.getData_color();
        } else {
            System.err.println("WARN: color not found for tag " + s);
        }
        return null;
    }

    public static OSConfig getOSinfo(String s) {
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

    public static Class getParser(String s) {
        if ( ParserMap.isEmpty()) {
            return null;
        }
        return ParserMap.get(s);
    }
    
    /*
    http://forums.sun.com/thread.jspa?threadID=341935&tstart=0kage
    @throws ClassNotFoundException if the Package is invalid
     */
    public static Class[] getClasses(String pckgname) throws ClassNotFoundException {
        ArrayList<Class> classes = new ArrayList<Class>();
        // Get a File object for the package
        File directory = null;
        try {
            ClassLoader cld = Thread.currentThread().getContextClassLoader();
            if (cld == null) {
                throw new ClassNotFoundException("Can't get class loader.");
            }
            String path = pckgname.replace('.', '/');
            URL resource = cld.getResource(path);
            if (resource == null) {
                throw new ClassNotFoundException("No resource for " + path);
            }
            directory = new File(resource.getFile());
        } catch (NullPointerException x) {
            throw new ClassNotFoundException(pckgname + " (" + directory + ") does not appear to be a valid package");
        }
        if (directory.exists()) {
            // Get the list of the files contained in the package
            String[] files = directory.list();
            for (int i = 0; i < files.length; i++) {
                // we are only interested in .class files
                if (files[i].endsWith(".class")) {
                    // removes the .class extension
                    classes.add(Class.forName(pckgname + '.' + files[i].substring(0, files[i].length() - 6)));
                }
            }
        } else {
            throw new ClassNotFoundException(pckgname + " does not appear to be a valid package");
        }
        Class[] classesA = new Class[classes.size()];
        classes.toArray(classesA);
        return classesA;
    }

    
    private static Properties systemprops = System.getProperties();
    private static String userhome = (String) systemprops.get("user.home") + systemprops.get("file.separator");
    private static String username = (String) systemprops.get("user.name");
    private static HashMap<String, ColorConfig> colorlist;
    private static HashMap<String, OSConfig> OSlist;
    private static boolean dodebug = false;
    private static String CLfilename = null;
    private static HashMap<String, Class> ParserMap;
}
