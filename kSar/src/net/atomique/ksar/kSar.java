/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar;

import java.beans.PropertyVetoException;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDesktopPane;
import net.atomique.ksar.Export.FilePDF;
import net.atomique.ksar.Graph.BaseGraph;
import net.atomique.ksar.Graph.BaseList;
import net.atomique.ksar.UI.DataView;
import net.atomique.ksar.UI.GraphSelection;
import net.atomique.ksar.UI.ParentNodeInfo;
import net.atomique.ksar.UI.SortedTreeNode;
import net.atomique.ksar.UI.TreeNodeInfo;

/**
 *
 * @author Max
 */
public class kSar {

    public kSar(JDesktopPane DesktopPane) {
        dataview = new DataView(this);
        dataview.toFront();
        dataview.setVisible(true);
        dataview.setTitle("Empty");
        DesktopPane.add(dataview);
        try {
            int num = DesktopPane.getAllFrames().length;
            if (num != 1) {
                dataview.reshape(5 * num, 5 * num, 800, 600);
            } else {
                dataview.reshape(0, 0, 800, 600);
            }
            dataview.setSelected(true);
        } catch (PropertyVetoException vetoe) {
        }
        if (GlobalOptions.getCLfilename() != null) {
            do_fileread(GlobalOptions.getCLfilename());
        }
    }

    public kSar() {
    }

    public void do_fileread(String filename) {
        if (filename == null) {
            launched_action = new FileRead(this);
        } else {
            launched_action = new FileRead(this, filename);
        }
        reload_action = ((FileRead) launched_action).get_action();
        launched_action.start();
    }

    public void do_localcommand(String cmd) {
        if (cmd == null) {
            launched_action = new LocalCommand(this);
        } else {
            launched_action = new LocalCommand(this, cmd);
        }
        reload_action = ((LocalCommand) launched_action).get_action();
        launched_action.start();
    }

    public int parse(BufferedReader br) {
        String current_line = null;
        long parsing_start = 0L;
        long parsing_end = 0L;
        String[] columns;
        int parser_return = 0;

        parsing_start = System.currentTimeMillis();

        try {
            while ((current_line = br.readLine()) != null && !action_interrupted) {
                lines_parsed++;
                if (current_line.length() == 0) {
                    continue;
                }
                columns = current_line.split("\\s+");

                if (columns.length == 0) {
                    continue;
                }
                // SCO_SV sco 3.2v5.0.7 i80386    09/24/2009
                if ("SCO_SV".equals(columns[0]) && columns.length == 5) {
                    if (myOS == null) {
                        myOS = new OSInfo("SCO", "automatically", current_line, this, null);
                    }
                    myOS.setHostname(columns[1]);
                    myOS.setOSversion(columns[2]);
                    myOS.setKernel(columns[3]);
                    myOS.setCpuType(columns[4]);
                    myOS.setDate(columns[4]);
                    String[] dateSplit = myOS.getDate().split("/");
                    if (dateSplit.length == 3) {
                        day = Integer.parseInt(dateSplit[1]);
                        month = Integer.parseInt(dateSplit[0]);
                        year = Integer.parseInt(dateSplit[2]);
                        if (year < 100) { // solaris 8 show date on two digit
                            year += 2000;
                        }
                    }
                    continue;
                }
                // SunOS host 5.9 Generic_118558-28 sun4u    09/01/2006
                if ("SunOS".equals(columns[0])) {
                    if (myOS == null) {
                        myOS = new OSInfo("SunOS", "automatically", current_line, this, new net.atomique.ksar.Solaris.Parser(this,"Solaris"));
                    }
                    myOS.setHostname(columns[1]);
                    myOS.setOSversion(columns[2]);
                    myOS.setKernel(columns[3]);
                    myOS.setCpuType(columns[4]);
                    myOS.setDate(columns[5]);
                    String[] dateSplit = myOS.getDate().split("/");
                    if (dateSplit.length == 3) {
                        day = Integer.parseInt(dateSplit[1]);
                        month = Integer.parseInt(dateSplit[0]);
                        year = Integer.parseInt(dateSplit[2]);
                        if (year < 100) { // solaris 8 show date on two digit
                            year += 2000;
                        }
                    }
                    continue;
                }
                // Linux 2.4.21-32.ELsmp (host)       09/09/06
                if ("Linux".equals(columns[0])) {
                    String tmpstr;
                    if (myOS == null) {
                        myOS = new OSInfo("Linux", "automatically", current_line, this, new net.atomique.ksar.Linux.Parser(this,"Linux"));
                    }
                    myOS.setKernel(columns[1]);
                    tmpstr = columns[2];
                    myOS.setHostname(tmpstr.substring(1, tmpstr.length() - 1));
                    myOS.setDate(columns[3]);
                    String[] dateSplit = columns[3].split("/");
                    if (dateSplit.length == 3) {
                        day = Integer.parseInt(dateSplit[1]);
                        month = Integer.parseInt(dateSplit[0]);
                        year = Integer.parseInt(dateSplit[2]);
                        if (year < 100) { // solaris 8 show date on two digit
                            year += 2000;
                        }
                    }
                    dateSplit = columns[3].split("-");
                    if (dateSplit.length == 3) {
                        day = Integer.parseInt(dateSplit[2]);
                        month = Integer.parseInt(dateSplit[1]);
                        year = Integer.parseInt(dateSplit[0]);
                    }
                    continue;
                }
                // AIX rsora1 3 4 0006488F4C00    12/18/06
                if ("AIX".equals(columns[0])) {
                    String tmpstr;
                    if (myOS == null) {
                        myOS = new OSInfo("AIX", "automatically", current_line, this, null);
                    }
                    myOS.setHostname(columns[1]);
                    myOS.setOSversion(columns[2] + "." + columns[3]);
                    myOS.setMacAddress(columns[4]);
                    myOS.setDate(columns[5]);
                    String[] dateSplit = myOS.getDate().split("/");
                    if (dateSplit.length == 3) {
                        day = Integer.parseInt(dateSplit[1]);
                        month = Integer.parseInt(dateSplit[0]);
                        year = Integer.parseInt(dateSplit[2]);
                        if (year < 100) { // solaris 8 show date on two digit
                            year += 2000;
                        }
                    }
                    continue;
                }
                //
                //
                if ("HP-UX".equals(columns[0])) {
                    if (myOS == null) {
                        myOS = new OSInfo("HP-UX", "automatically", current_line, this, null);
                    }
                    myOS.setHostname(columns[1]);
                    myOS.setOSversion(columns[2]);
                    myOS.setKernel(columns[3]);
                    myOS.setCpuType(columns[4]);
                    myOS.setDate(columns[5]);
                    String[] dateSplit = myOS.getDate().split("/");
                    if (dateSplit.length == 3) {
                        day = Integer.parseInt(dateSplit[1]);
                        month = Integer.parseInt(dateSplit[0]);
                        year = Integer.parseInt(dateSplit[2]);
                        if (year < 100) { // solaris 8 show date on two digit
                            year += 2000;
                        }
                    }
                    continue;
                }
                //
                //
                if ("Darwin".equals(columns[0])) {
                    if (myOS == null) {
                        myOS = new OSInfo("Mac", "automatically", current_line, this, null);
                    }
                    myOS.setHostname(columns[1]);
                    myOS.setOSversion(columns[2]);
                    myOS.setCpuType(columns[3]);
                    myOS.setDate(columns[4]);
                    String[] dateSplit = myOS.getDate().split("/");
                    if (dateSplit.length == 3) {
                        day = Integer.parseInt(dateSplit[1]);
                        month = Integer.parseInt(dateSplit[0]);
                        year = Integer.parseInt(dateSplit[2]);
                        if (year < 100) { // solaris 8 show date on two digit
                            year += 2000;
                        }
                    }
                    continue;
                }
                // Esar SunOS host 5.9 Generic_118558-28 sun4u    09/01/2006
                if ("Esar".equals(columns[0])) {
                    if (myOS == null) {
                        myOS = new OSInfo("Esar SunOS", "automatically", current_line, this, null);
                    }
                    // skip sunos

                    myOS.setHostname(columns[2]);
                    myOS.setOSversion(columns[3]);
                    myOS.setKernel(columns[4]);
                    myOS.setCpuType(columns[5]);
                    myOS.setDate(columns[6]);
                    String[] dateSplit = myOS.getDate().split("/");
                    if (dateSplit.length == 3) {
                        day = Integer.parseInt(dateSplit[1]);
                        month = Integer.parseInt(dateSplit[0]);
                        year = Integer.parseInt(dateSplit[2]);
                        if (year < 100) { // solaris 8 show date on two digit
                            year += 2000;
                        }
                    }
                    continue;
                }

                if (myOS.getParser() == null) {
                    System.out.println("unknown system");
                }

                parser_return = myOS.getParser().parse(current_line, columns);
                if (parser_return == 1 && GlobalOptions.isDodebug()) {
                    System.out.println("### " + current_line);
                }
                if (parser_return < 0 && GlobalOptions.isDodebug()) {
                    System.out.println("ERR " + current_line);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(kSar.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (dataview != null) {
            dataview.treehome();
            dataview.setHasData(true);
        }
        parsing_end = System.currentTimeMillis();
        if (GlobalOptions.isDodebug()) {
            System.out.println("time to parse: " + (parsing_end - parsing_start) + "ms ");
        }
        return -1;
    }

    public boolean isAction_interrupted() {
        return action_interrupted;
    }

    public void add2tree(SortedTreeNode parent, SortedTreeNode newNode) {
        if (dataview != null) {
            dataview.add2tree(parent, newNode);
        }
    }

   
    public int get_page_to_print() {
        page_to_print=0;
        count_printSelected(graphtree);
        return page_to_print;
    }
    
    public void count_printSelected(SortedTreeNode node) {
        int num = node.getChildCount();

        if (num > 0) {
            Object obj1 = node.getUserObject();
            for (int i = 0; i < num; i++) {
                SortedTreeNode l = (SortedTreeNode) node.getChildAt(i);
                count_printSelected(l);
            }
        } else {
            Object obj1 = node.getUserObject();
            if (obj1 instanceof TreeNodeInfo) {
                TreeNodeInfo tmpnode = (TreeNodeInfo) obj1;
                BaseGraph nodeobj = tmpnode.getNode_object();
                if ( nodeobj.isPrintSelected()) {
                    page_to_print++;
                }
            }
        }
    }
    /*
    public void export_treenode(SortedTreeNode node,GraphSelection graphselection) {
        int num = node.getChildCount();

        if (num > 0) {
            Object obj1 = node.getUserObject();
            if (obj1 instanceof ParentNodeInfo) {
                ParentNodeInfo tmpnode = (ParentNodeInfo) obj1;
                BaseList nodeobj = tmpnode.getNode_object();
                System.out.println(nodeobj.doPrint());
            }
            for (int i = 0; i < num; i++) {
                SortedTreeNode l = (SortedTreeNode) node.getChildAt(i);
                export_treenode(l, graphselection);
            }
        } else {
            Object obj1 = node.getUserObject();
            if (obj1 instanceof TreeNodeInfo) {
                TreeNodeInfo tmpnode = (TreeNodeInfo) obj1;
                BaseGraph nodeobj = tmpnode.getNode_object();
                System.out.println(nodeobj.doPrint());
            }
        }
    }
     * 
     */
    
    


    DataView dataview = null;
    private long lines_parsed = 0L;
    private String reload_action = "Empty";
    private Thread launched_action = null;
    private boolean action_interrupted = false;
    public OSInfo myOS = null;
    public int day = 0;
    public int month = 0;
    public int year = 0;
    public int total_graph=0;
    public SortedTreeNode graphtree = new SortedTreeNode("kSar");
    public int page_to_print=0;
}
