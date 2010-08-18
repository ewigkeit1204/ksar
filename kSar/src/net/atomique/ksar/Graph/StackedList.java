/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.atomique.ksar.Graph;

import net.atomique.ksar.UI.SortedTreeNode;
import net.atomique.ksar.UI.TreeNodeInfo;
import net.atomique.ksar.kSar;
import org.jfree.data.time.Second;

/**
 *
 * @author alex
 */
public class StackedList extends BaseList {

    public StackedList (kSar hissar, String s2,String s1, int skipColumn) {
        super(hissar,s2,s1,skipColumn);
    }

    public int parse(Second now,String s) {
        String cpu_cols[] = s.split("\\s+");
        StackedGraph cputmp = null;
        if ( ! nodeHashList.containsKey(cpu_cols[1])) {
            cputmp= new StackedGraph(mysar, Title+" "+cpu_cols[1], skipColumn);
            cputmp.setTitle(HeaderStr);
            cputmp.setAxisTitle("%");
            nodeHashList.put(cpu_cols[1], cputmp);
            TreeNodeInfo infotmp= new TreeNodeInfo( cpu_cols[1], cputmp);
            SortedTreeNode nodetmp = new SortedTreeNode(infotmp);
            mysar.add2tree(parentTreeNode, nodetmp);
        } else {
            cputmp = (StackedGraph)nodeHashList.get(cpu_cols[1]);
        }

        return cputmp.parse(now,s);
    }
    
}
