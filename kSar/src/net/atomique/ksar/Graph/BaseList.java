/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar.Graph;

import java.awt.FlowLayout;
import net.atomique.ksar.Graph.BaseGraph;
import java.awt.LayoutManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.TitledBorder;
import net.atomique.ksar.UI.ParentNodeInfo;
import net.atomique.ksar.UI.SortedTreeNode;
import net.atomique.ksar.UI.TreeNodeInfo;
import net.atomique.ksar.kSar;
import org.jfree.chart.ChartPanel;
import org.jfree.data.time.Second;

/**
 *
 * @author Max
 */
public abstract class BaseList {

    public BaseList(kSar hissar, String stitle, String sheader, int i) {
        mysar = hissar;
        HeaderStr = sheader;
        Title = stitle;
        skipColumn = i;
        ParentNodeInfo tmp = new ParentNodeInfo(Title, this);
        parentTreeNode = new SortedTreeNode(tmp);
        mysar.add2tree(mysar.graphtree, parentTreeNode);
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

    public int parse(Second now, String s) {
        String cols[] = s.split("\\s+");
        LineGraph tmp = null;
        if (!nodeHashList.containsKey(cols[1])) {
            tmp = new LineGraph(mysar, Title + " " + cols[1], HeaderStr, skipColumn, null);
            tmp.setPlotList(PlotList);
            nodeHashList.put(cols[1], tmp);
            TreeNodeInfo infotmp = new TreeNodeInfo(cols[1], tmp);
            SortedTreeNode nodetmp = new SortedTreeNode(infotmp);
            mysar.add2tree(parentTreeNode, nodetmp);
        } else {
            tmp = (LineGraph) nodeHashList.get(cols[1]);
        }

        return tmp.parse(now, s);
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
    //abstract public int parse(Second now, String s);
    protected SortedTreeNode parentTreeNode = null;
    protected kSar mysar = null;
    protected String HeaderStr = null;
    protected Map<String, BaseGraph> nodeHashList = new HashMap<String, BaseGraph>();
    protected int skipColumn = 0;
    protected String Title = null;
    protected Map<String, ArrayList> PlotList = new HashMap<String, ArrayList>();
}
