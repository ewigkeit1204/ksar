/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar.Graph;

import java.awt.LayoutManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import net.atomique.ksar.UI.ParentNodeInfo;
import net.atomique.ksar.UI.SortedTreeNode;
import net.atomique.ksar.XML.GraphConfig;
import net.atomique.ksar.kSar;
import org.jfree.chart.ChartPanel;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeTableXYDataset;

/**
 *
 * @author Max
 */
public abstract class BaseList {

    public BaseList(kSar hissar,  GraphConfig g,String stitle, String sheader, int i) {
        mysar = hissar;
        HeaderStr = sheader;
        graphconfig =g ;
        Title = stitle;
        skipColumn = i;
        ParentNodeInfo tmp = new ParentNodeInfo(Title, this);
        parentTreeNode = new SortedTreeNode(tmp);
        mysar.add2tree(mysar.graphtree, parentTreeNode);
    }

    public void create_newstack(String s1, String s2) {
        TimeTableXYDataset tmp = new TimeTableXYDataset();
        String [] s = s2.split("\\s+");
        for (int i=0 ; i< s.length; i++) {
            StackList.put(s[i], tmp);
        }
    }
    
    public void create_newplot(String plotname, String headername) {
        ArrayList<String> t = new ArrayList<String>();
        String[] s = headername.split("\\s+");
        for (int i = 0; i < s.length; i++) {
            t.add(s[i]);
        }
        PlotList.put(plotname, t);
    }

    public JPanel run() {
        JPanel tmppanel = new JPanel();
        LayoutManager tmplayout = null;
        int graphnumber = nodeHashList.size();
        int linenum = (int) Math.floor(graphnumber / 2);
        if (graphnumber % 2 != 0) {
            linenum++;
        }
        tmplayout = new java.awt.GridLayout(linenum, 2);
        tmppanel.setLayout(tmplayout);


        SortedSet<String> sortedset = new TreeSet<String>(nodeHashList.keySet());

        Iterator<String> it = sortedset.iterator();

        while (it.hasNext()) {
            BaseGraph tmpgraph = (BaseGraph) nodeHashList.get(it.next());
            tmppanel.add(new ChartPanel(tmpgraph.getgraph()));
        }

        return tmppanel;
    }

    

    public JPanel getprintform() {
        JPanel panel = new JPanel();
        panel.setBorder(new TitledBorder(Title));
        panel.setLayout(new javax.swing.BoxLayout(panel, javax.swing.BoxLayout.PAGE_AXIS));
        return panel;
    }

    public String doPrint() {
        boolean leaftoprint = false;
        SortedSet<String> sortedset = new TreeSet<String>(nodeHashList.keySet());

        Iterator<String> it = sortedset.iterator();

        while (it.hasNext()) {
            BaseGraph tmpgraph = (BaseGraph) nodeHashList.get(it.next());
            if (tmpgraph.printSelected) {
                leaftoprint = true;
                break;
            }
        }
        if (leaftoprint) {
            return "print " + Title;
        } else {
            return "skip " + Title;
        }
    }

    public String getTitle() {
        return Title;
    }

    public boolean isPrintSelected() {
        return false;
    }

    abstract public int parse(Second now, String s);
    protected GraphConfig graphconfig = null;
    protected SortedTreeNode parentTreeNode = null;
    protected kSar mysar = null;
    protected String HeaderStr = null;
    protected Map<String, BaseGraph> nodeHashList = new HashMap<String, BaseGraph>();
    protected int skipColumn = 0;
    protected String Title = null;
    protected Map<String, ArrayList> PlotList = new HashMap<String, ArrayList>();
    protected Map<String, TimeTableXYDataset> StackList  = new HashMap<String, TimeTableXYDataset>();
}
