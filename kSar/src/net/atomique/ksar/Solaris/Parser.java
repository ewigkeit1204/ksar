/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar.Solaris;

import net.atomique.ksar.Linux.*;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import net.atomique.ksar.Graph.LineGraph;
import net.atomique.ksar.AllParser;
import net.atomique.ksar.Graph.BaseGraph;
import net.atomique.ksar.Graph.BaseList;
import net.atomique.ksar.GlobalOptions;
import net.atomique.ksar.Graph.LineList;
import net.atomique.ksar.Graph.StackedGraph;
import net.atomique.ksar.Graph.StackedList;
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
            if (!"DEVICE".equals(currentStat)) {
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
            Object obj = GraphList.get(checkStat);
            if (obj == null) {
                GraphConfig mygraphinfo = myosconfig.getGraphConfig(checkStat);
                if (mygraphinfo != null) {
                    if ("line".equals(mygraphinfo.getType())) {
                        obj = new LineGraph(mysar, mygraphinfo.getTitle(), line, firstdatacolumn, mysar.graphtree);
                        SortedSet<String> sortedset = new TreeSet<String>(mygraphinfo.getPlotlist().keySet());
                        Iterator<String> it = sortedset.iterator();
                        while (it.hasNext()) {
                            PlotConfig tmp = (PlotConfig) mygraphinfo.getPlotlist().get(it.next());
                            ((LineGraph)obj).create_newplot(tmp.getTitle(), tmp.getHeaderStr());
                        }
                        GraphList.put(checkStat, obj);
                        currentStat = checkStat;
                        return 0;
                    }
                    if ("linelist".equals(mygraphinfo.getType())) {
                        obj = new LineList(mysar, mygraphinfo.getTitle(), line, firstdatacolumn);
                        SortedSet<String> sortedset = new TreeSet<String>(mygraphinfo.getPlotlist().keySet());
                        Iterator<String> it = sortedset.iterator();
                        while (it.hasNext()) {
                            PlotConfig tmp = (PlotConfig) mygraphinfo.getPlotlist().get(it.next());
                            ((LineList)obj).create_newplot(tmp.getTitle(), tmp.getHeaderStr());
                        }
                        GraphList.put(checkStat, obj);
                        currentStat = checkStat;
                        return 0;

                    }
                    if ("stacked".equals(mygraphinfo.getType())) {
                        obj = new StackedGraph(mysar, mygraphinfo.getTitle(), line, firstdatacolumn, mysar.graphtree);
                        SortedSet<String> sortedset = new TreeSet<String>(mygraphinfo.getStacklist().keySet());
                        Iterator<String> it = sortedset.iterator();
                        while (it.hasNext()) {
                            StackConfig tmp = (StackConfig) mygraphinfo.getStacklist().get(it.next());
                            ((StackedGraph)obj).create_newstack(tmp.getTitle(), tmp.getHeaderStr());
                        }
                        GraphList.put(checkStat, obj);
                        currentStat = checkStat;
                        return 0;
                    }
                    if ("stackedlist".equals(mygraphinfo.getType())) {
                        obj = new StackedList(mysar, mygraphinfo.getTitle(), line, firstdatacolumn);
                        SortedSet<String> sortedset = new TreeSet<String>(mygraphinfo.getStacklist().keySet());
                        Iterator<String> it = sortedset.iterator();
                        while (it.hasNext()) {
                            StackConfig tmp = (StackConfig) mygraphinfo.getStacklist().get(it.next());
                            ((StackedList)obj).create_newstack(tmp.getTitle(), tmp.getHeaderStr());
                        }
                        GraphList.put(checkStat, obj);
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

        currentStatObj = GraphList.get(currentStat);
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
}
