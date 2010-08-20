/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar.Parser;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import net.atomique.ksar.AllParser;
import net.atomique.ksar.GlobalOptions;
import net.atomique.ksar.Graph.Graph;
import net.atomique.ksar.Graph.List;
import net.atomique.ksar.XML.GraphConfig;
import net.atomique.ksar.XML.PlotConfig;
import net.atomique.ksar.XML.StackConfig;
import net.atomique.ksar.kSar;
import org.jfree.data.time.Second;

/**
 *
 * @author Max
 */
public class Linux extends AllParser {

    public Linux(kSar hissar, String OS) {
        super(hissar, OS);        
    }

    @Override
    public int parse(String line, String[] columns) {
        int heure = 0;
        int minute = 0;
        int seconde = 0;
        Second now = null;

        if ("Average:".equals(columns[0])) {
            currentStat = "NONE";
            return 0;
        }

        if (line.indexOf("unix restarts") >= 0 || line.indexOf(" unix restarted") >= 0) {
            return 0;
        }

        // match the System [C|c]onfiguration line on AIX
        if (line.indexOf("System Configuration") >= 0 || line.indexOf("System configuration") >= 0) {
            return 0;
        }

        if (line.indexOf("State change") >= 0) {
            return 0;
        }


        String[] sarTime = columns[0].split(":");
        if (sarTime.length != 3) {
            return -1;
        } else {
            heure = Integer.parseInt(sarTime[0]);
            minute = Integer.parseInt(sarTime[1]);
            seconde = Integer.parseInt(sarTime[2]);
            now = new Second(seconde, minute, heure, mysar.day, mysar.month, mysar.year);
            if (startofstat == null) {
                startofstat = now;
            }
            if (now.compareTo(endofstat) > 0) {
                endofstat = now;
            }
            firstdatacolumn = 1;
        }


        //00:20:01     CPU  i000/s  i001/s  i002/s  i008/s  i009/s  i010/s  i011/s  i012/s  i014/s
        if ("CPU".equals(columns[firstdatacolumn]) && line.matches(".*i([0-9]+)/s.*")) {
            currentStat = "IGNORE";
            return 1;
        }
        /** XML COLUMN PARSER **/
        String checkStat = myosconfig.getStat(columns, firstdatacolumn);

        if (checkStat != null) {
            Object obj = ListofGraph.get(checkStat);
            if (obj == null) {
                GraphConfig mygraphinfo = myosconfig.getGraphConfig(checkStat);
                if (mygraphinfo != null) {
                    if ("unique".equals(mygraphinfo.getType())) {
                        obj = new Graph(mysar, mygraphinfo,mygraphinfo.getTitle(), line, firstdatacolumn, mysar.graphtree);
                        
                        ListofGraph.put(checkStat, obj);
                        currentStat = checkStat;
                        return 0;
                    }
                    if ("multiple".equals(mygraphinfo.getType())) {
                        obj = new List(mysar, mygraphinfo,mygraphinfo.getTitle(), line, firstdatacolumn);
                        
                        ListofGraph.put(checkStat, obj);
                        currentStat = checkStat;
                        return 0;
                    }                    
                } else {
                    // no graph associate
                    currentStat = checkStat;
                    return 0;
                }
            } else {
                currentStat = checkStat;
                return 0;
            }
        }

        //System.out.println( currentStat +" " + line);
        
        
        
        if (lastStat
                != null) {
            if (!lastStat.equals(currentStat) && GlobalOptions.isDodebug()) {
                System.out.println("Stat change from " + lastStat + " to " + currentStat);
                lastStat = currentStat;
            }
        } else {
            lastStat = currentStat;
        }
        if ("IGNORE".equals(currentStat)) {
            return 1;
        }
        if ("NONE".equals(currentStat)) {
            return -1;
        }
        
        currentStatObj = ListofGraph.get(currentStat);
        if (currentStatObj == null) {
            return -1;
        } else {
            if (currentStatObj instanceof Graph) {
                Graph ag = (Graph) currentStatObj;
                return ag.parse_line(now, line);
            }
            if (currentStatObj instanceof  List) {
                 List ag = ( List) currentStatObj;
                return ag.parse_line(now, line);
            }
        }
        return -1;
    }

    
    
}
