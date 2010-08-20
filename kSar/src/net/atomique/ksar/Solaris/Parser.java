/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar.Solaris;


import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import net.atomique.ksar.AllParser;
import net.atomique.ksar.Graph.BaseGraph;
import net.atomique.ksar.Graph.BaseList;
import net.atomique.ksar.GlobalOptions;
import net.atomique.ksar.Graph.GraphList;
import net.atomique.ksar.Graph.OneGraph;
import net.atomique.ksar.XML.GraphConfig;
import net.atomique.ksar.XML.PlotConfig;
import net.atomique.ksar.XML.StackConfig;
import net.atomique.ksar.kSar;
import org.jfree.data.time.Second;

/**
 *
 * @author Max
 */
public class Parser extends AllParser {

    public Parser(kSar hissar, String OS) {
        super(hissar, OS);
    }

    @Override
    public int parse(String line, String[] columns) {
        int heure = 0;
        int minute = 0;
        int seconde = 0;
        

        if ("Average".equals(columns[0])) {
            under_average= true;
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
            if (!"DEVICE".equals(currentStat) ) {
                return -1;
            }
            firstdatacolumn = 0;
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


        /** XML COLUMN PARSER **/
        String checkStat = myosconfig.getStat(columns, firstdatacolumn);

        if (checkStat != null) {
            Object obj = ListofGraph.get(checkStat);
            if (obj == null) {
                GraphConfig mygraphinfo = myosconfig.getGraphConfig(checkStat);
                if (mygraphinfo != null) {
                    if ("unique".equals(mygraphinfo.getType())) {
                        obj = new OneGraph(mysar, mygraphinfo, mygraphinfo.getTitle(),line, firstdatacolumn, mysar.graphtree);
                        ListofGraph.put(checkStat, obj);
                        currentStat = checkStat;
                        return 0;
                    }
                    if ("multiple".equals(mygraphinfo.getType())) {
                        obj = new GraphList(mysar, mygraphinfo, mygraphinfo.getTitle(), line, firstdatacolumn);                        
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

        //System.out.println(currentStat + " " + line);



        if (lastStat != null) {
            if (!lastStat.equals(currentStat) && GlobalOptions.isDodebug()) {
                System.out.println("Stat change from " + lastStat + " to " + currentStat);
                lastStat = currentStat;
                under_average=false;
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

        if ( under_average ) {
            return 0;
        }
        currentStatObj = ListofGraph.get(currentStat);
        if (currentStatObj == null) {
            return -1;
        } else {
            if (currentStatObj instanceof BaseGraph) {
                BaseGraph ag = (BaseGraph) currentStatObj;
                return ag.parse(now, line);
            }
            if (currentStatObj instanceof BaseList) {
                BaseList ag = (BaseList) currentStatObj;
                return ag.parse(now, line);
            }
        }
        return -1;
    }
    Second now = null;
    boolean under_average=false;
}
