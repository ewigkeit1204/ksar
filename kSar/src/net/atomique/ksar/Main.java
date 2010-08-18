/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import net.atomique.ksar.UI.Desktop;
import net.atomique.ksar.UI.SplashScreen;

/**
 *
 * @author Max
 */
public class Main {

    Config config = Config.getInstance();
    GlobalOptions globaloptions = GlobalOptions.getInstance();
    
    
    public static void usage() {
        show_version();
    }

    public static void show_version() {
        System.err.println("ksar Version : " + VersionNumber.getVersionNumber());
    }

    private static void set_lookandfeel() {
        for (UIManager.LookAndFeelInfo laf : UIManager.getInstalledLookAndFeels()) {
            if (Config.getLandf().equals(laf.getName())) {
                try {
                    UIManager.setLookAndFeel(laf.getClassName());
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(Desktop.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InstantiationException ex) {
                    Logger.getLogger(Desktop.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(Desktop.class.getName()).log(Level.SEVERE, null, ex);
                } catch (UnsupportedLookAndFeelException ex) {
                    Logger.getLogger(Desktop.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    public static void make_ui() {
        SplashScreen mysplash = new SplashScreen(null, 3000);
        while (mysplash.isVisible()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }
        set_lookandfeel();
        System.setProperty("com.apple.mrj.application.growbox.intrudes", "false");
        
        GlobalOptions.setUI(new Desktop());
        SwingUtilities.updateComponentTreeUI(GlobalOptions.getUI());
        GlobalOptions.getUI().add_window();
        GlobalOptions.getUI().maxall();
    }
    
    public static void main(String[] args) {
        int i = 0;
        String arg;
        
        if (args.length > 0) {
            while (i < args.length && args[i].startsWith("-")) {
                arg = args[i++];
                if ("-version".equals(arg)) {
                    show_version();
                    System.exit(0);
                }
                if ("-help".equals(arg)) {
                    usage();
                    continue;
                }
                if ( "-test".equals(arg)) {
                    GlobalOptions.setDodebug(true);
                }
                if ("-input".equals(arg)) {
                    if (i < args.length) {
                        GlobalOptions.setCLfilename(args[i++]);
                    } else {
                        exit_error("-input requires an option");
                    }
                    continue;
                }
            }
        }

        make_ui();
    }

    public static void exit_error(final String message) {
        System.err.println(message);
        System.exit(1);
    }


}
