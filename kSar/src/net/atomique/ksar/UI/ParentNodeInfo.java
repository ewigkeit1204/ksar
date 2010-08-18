/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.atomique.ksar.UI;

import net.atomique.ksar.Graph.BaseGraph;
import net.atomique.ksar.Graph.BaseList;

/**
 *
 * @author Max
 */
public class ParentNodeInfo {

    public ParentNodeInfo(String t, BaseList list) {
        node_title = t;
        node_object = list;
    }

    public BaseList getNode_object() {
        return node_object;
    }

    public String getNode_title() {
        return node_title;
    }

    public String toString() {
        return node_title;
    }
    
    private String node_title = null;
    private BaseList node_object = null;
    
}
