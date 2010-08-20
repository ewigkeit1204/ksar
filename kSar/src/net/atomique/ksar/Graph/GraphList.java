/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.atomique.ksar.Graph;

import net.atomique.ksar.UI.SortedTreeNode;
import net.atomique.ksar.UI.TreeNodeInfo;
import net.atomique.ksar.XML.GraphConfig;
import net.atomique.ksar.kSar;
import org.jfree.data.time.Second;

/**
 *
 * @author alex
 */
public class GraphList extends BaseList {

    public GraphList (kSar hissar, GraphConfig g, String s2,String s1, int skipColumn) {
        super(hissar,g,s2,s1,skipColumn);
    }
    
    public int parse(Second now,String s) {
        String cols[] = s.split("\\s+");
        OneGraph tmp = null;
        if ( ! nodeHashList.containsKey(cols[1])) {
            tmp= new OneGraph(mysar, graphconfig,Title + " " + cols[1], HeaderStr, skipColumn+1, null);
            nodeHashList.put(cols[1], tmp);
            tmp.setStackList(StackList);
            tmp.setPlotList(PlotList);
            TreeNodeInfo infotmp= new TreeNodeInfo( cols[1], tmp);
            SortedTreeNode nodetmp = new SortedTreeNode(infotmp);
            mysar.add2tree(parentTreeNode, nodetmp);
        } else {
            tmp = (OneGraph)nodeHashList.get(cols[1]);
        }

        return tmp.parse(now,s);
    }
    
}
