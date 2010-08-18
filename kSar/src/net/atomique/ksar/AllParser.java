/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar;

import java.util.HashMap;
import java.util.Map;
import org.jfree.data.time.Second;

/**
 *
 * @author Max
 */
public abstract class AllParser {

    public AllParser(kSar hissar) {
        mysar = hissar;
    }

    public int parse(String line, String[] columns) {
        System.err.println("not implemented");
        return -1;
    }

    abstract public Object getStatGraph(String statname, String headerline);


    
    protected kSar mysar = null;
    protected Map<String,Object> GraphList = new HashMap<String, Object>();
    protected String currentStat = "NONE";
    protected String lastStat = null;
    protected Object currentStatObj = null;
    //List graphlist = new ArrayList();
    protected Second startofstat = null;
    protected Second endofstat = null;
    protected int firstdatacolumn =0;
}
