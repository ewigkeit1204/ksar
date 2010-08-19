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
        String cols[] = s.split("\\s+");
        StackedGraph tmp = null;
        if ( ! nodeHashList.containsKey(cols[1])) {
            tmp= new StackedGraph(mysar, Title + " " + cols[1], HeaderStr, skipColumn, null);
            nodeHashList.put(cols[1], tmp);
            tmp.setStackList(StackList);
            TreeNodeInfo infotmp= new TreeNodeInfo( cols[1], tmp);
            SortedTreeNode nodetmp = new SortedTreeNode(infotmp);
            mysar.add2tree(parentTreeNode, nodetmp);
        } else {
            tmp = (StackedGraph)nodeHashList.get(cols[1]);
        }

        return tmp.parse(now,s);
    }
    
}
