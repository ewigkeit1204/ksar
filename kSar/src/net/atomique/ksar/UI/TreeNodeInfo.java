/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.atomique.ksar.UI;

import net.atomique.ksar.Graph.BaseGraph;

/**
 *
 * @author Max
 */
public class TreeNodeInfo {

    public TreeNodeInfo(String t, BaseGraph graph) {
        node_title = t;
        node_object = graph;
    }

    public BaseGraph getNode_object() {
        return node_object;
    }

    public String getNode_title() {
        return node_title;
    }

    public String toString() {
        return node_title;
    }
    
    private String node_title = null;
    private BaseGraph node_object = null;
    
}
