/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar;

import java.util.HashMap;
import java.util.Map;
import net.atomique.ksar.XML.OSConfig;
import org.jfree.data.time.Second;

/**
 *
 * @author Max
 */
public abstract class AllParser {

    public AllParser(kSar hissar, String OS) {
        mysar = hissar;
        myosconfig = GlobalOptions.getOSinfo(OS);
    }

    public int parse(String line, String[] columns) {
        System.err.println("not implemented");
        return -1;
    }

    

    protected OSConfig myosconfig = null;
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
