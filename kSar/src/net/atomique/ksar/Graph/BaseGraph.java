/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar.Graph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.swing.JCheckBox;
import net.atomique.ksar.GlobalOptions;
import net.atomique.ksar.XML.GraphConfig;
import net.atomique.ksar.XML.PlotConfig;
import net.atomique.ksar.XML.StackConfig;
import net.atomique.ksar.kSar;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StackedXYAreaRenderer2;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeTableXYDataset;
import org.jfree.data.xy.XYDataset;

/**
 *
 * @author Max
 */
public abstract class BaseGraph {

    public BaseGraph(kSar hissar, String s, int i, GraphConfig g) {
        mysar = hissar;
        graphtitle = s;
        graphconfig = g;
        printCheckBox = new JCheckBox(graphtitle, printSelected);
        printCheckBox.addItemListener(new java.awt.event.ItemListener() {

            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                if (evt.getSource() == printCheckBox) {
                    printSelected = printCheckBox.isSelected();
                }
            }
        });
        skipColumn = i;
    }

    protected XYDataset create_collection(ArrayList l) {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        TimeSeries found = null;
        boolean hasdata = false;
        for (int i = 0; i < l.size(); i++) {
            found = null;
            for (int j = 0; j < Stats.size(); j++) {
                found = (TimeSeries) Stats.get(j);
                if (found.getKey().equals(l.get(i))) {
                    break;
                } else {
                    found = null;
                }
            }

            if (found != null) {
                graphcollection.addSeries(found);
                hasdata = true;
            }
        }
        if (!hasdata) {
            return null;
        }
        return graphcollection;
    }

    protected XYPlot create_subplot(XYDataset x, String axistitle) {
        XYItemRenderer renderer = new StandardXYItemRenderer();
        NumberAxis graphaxistitle = new NumberAxis(axistitle);
        XYPlot subplot = new XYPlot(x, null, graphaxistitle, renderer);
        return subplot;
    }

    public void create_newstack(String s1, String s2) {
        TimeTableXYDataset tmp = new TimeTableXYDataset();
        String[] s = s2.split("\\s+");
        for (int i = 0; i < s.length; i++) {
            StackList.put(s[i], tmp);
        }
    }

    protected XYPlot create_plot(String axistitle, ArrayList l) {
        XYDataset t = create_collection(l);
        if (t == null) {
            return null;
        }
        XYPlot tmp = create_subplot(t, axistitle);
        return tmp;
    }

    public void create_newplot(String plotname, String headername) {
        ArrayList<String> t = new ArrayList<String>();
        String[] s = headername.split("\\s+");
        for (int i = 0; i < s.length; i++) {
            t.add(s[i]);
        }
        PlotList.put(plotname, t);
    }

    public JFreeChart getgraph() {
        if (mygraph == null) {
            mygraph = makegraph(null, null);
        }
        return mygraph;
    }

    public void setPlotList(Map PlotList) {
        this.PlotList = PlotList;
    }

    public void setStackList(Map StackList) {
        this.StackList = StackList;
    }

    public String make_csv() {
        StringBuilder tmp = new StringBuilder();
        tmp.append("Date;");
        tmp.append(getCsvHeader());
        TimeSeries datelist = (TimeSeries) Stats.get((1 + skipColumn));
        Iterator ite = datelist.getTimePeriods().iterator();
        while (ite.hasNext()) {
            TimePeriod item = (TimePeriod) ite.next();
            tmp.append(item.toString());
            tmp.append(";");
            tmp.append(getCsvLine((RegularTimePeriod) item));
            tmp.append("\n");
        }

        return tmp.toString();
    }

    public String getCsvHeader() {
        StringBuilder tmp = new StringBuilder();
        for (int i = 1 + skipColumn; i < HeaderStr.length; i++) {
            TimeSeries tmpseries = (TimeSeries) Stats.get(i - skipColumn);
            tmp.append(tmpseries.getKey());
            tmp.append(";");
        }
        tmp.append("\n");
        return tmp.toString();
    }

    public String getCsvLine(RegularTimePeriod t) {
        StringBuilder tmp = new StringBuilder();
        for (int i = 1 + skipColumn; i < HeaderStr.length; i++) {
            TimeSeries tmpseries = (TimeSeries) Stats.get(i - skipColumn);
            tmp.append(tmpseries.getValue(t));

            tmp.append(";");
        }
        return tmp.toString();
    }

    public int savePNG(final Second g_start, final Second g_end, final String filename, final int width, final int height) {
        try {
            ChartUtilities.saveChartAsPNG(new File(filename), this.makegraph(g_start, g_end), width, height);
        } catch (IOException e) {
            System.err.println("Unable to write to : " + filename);
            return -1;
        }
        return 0;
    }

    public int saveJPG(final Second g_start, final Second g_end, final String filename, final int width, final int height) {
        try {
            ChartUtilities.saveChartAsJPEG(new File(filename), this.makegraph(g_start, g_end), width, height);
        } catch (IOException e) {
            System.err.println("Unable to write to : " + filename);
            return -1;
        }
        return 0;
    }

    public JCheckBox getprintform() {
        return printCheckBox;
    }

    public String doPrint() {
        if (printSelected) {
            return "print " + graphtitle;
        } else {
            return "skip " + graphtitle;
        }
    }

    public void addPlot(PlotConfig p) {
    }

    public String getTitle() {
        return graphtitle;
    }

    public boolean isPrintSelected() {
        return printSelected;
    }

    public void setTitle(String s) {
        HeaderStr = s.split("\\s+");
        for (int i = skipColumn; i < HeaderStr.length; i++) {
            Stats.add(new TimeSeries(HeaderStr[i]));
        }
    }

    private XYPlot parse_Graphconfig() {
        long begingenerate = System.currentTimeMillis();
        CombinedDomainXYPlot plot = new CombinedDomainXYPlot(new DateAxis(""));
        if (graphconfig == null) {
            return null;
        }
        SortedSet<String> sortedset = new TreeSet<String>(graphconfig.getPlotlist().keySet());
        Iterator<String> it = sortedset.iterator();
        while (it.hasNext()) {
            PlotConfig tmp = (PlotConfig) graphconfig.getPlotlist().get(it.next());
            XYItemRenderer renderer = new StandardXYItemRenderer();
            
            ArrayList<String> t = new ArrayList<String>();
            String[] s = tmp.getHeaderStr().split("\\s+");
            for (int i = 0; i < s.length; i++) {
                t.add(s[i]);
            }
            XYDataset c = create_collection(t);
            NumberAxis graphaxistitle = new NumberAxis(tmp.getTitle());
            XYPlot tmpplot = new XYPlot(c, null, graphaxistitle, renderer);
            
            if (tmpplot == null) {
                continue;
            }
            for (int i = 0; i < s.length; i++) {
                Color color = GlobalOptions.getDataColor(s[i].toString());
                if (color != null) {
                    renderer.setSeriesPaint(i, color);
                    renderer.setBaseStroke(new BasicStroke(1.0F));
                }
            }
            plot.add(tmpplot, tmp.getSize());
        }
        sortedset = new TreeSet<String>(graphconfig.getStacklist().keySet());
        it = sortedset.iterator();
        while (it.hasNext()) {
            StackConfig tmp = (StackConfig) graphconfig.getStacklist().get(it.next());
            StackedXYAreaRenderer2 renderer = new StackedXYAreaRenderer2();

            ArrayList<String> t = new ArrayList<String>();
            String[] s = tmp.getHeaderStr().split("\\s+");
            for (int i = 0; i < s.length; i++) {
                t.add(s[i]);
            }
            XYDataset c = create_collection(t);
            NumberAxis graphaxistitle = new NumberAxis(tmp.getTitle());
            XYPlot tmpplot = new XYPlot(c, null, graphaxistitle, renderer);

            if (tmpplot == null) {
                continue;
            }
            for (int i = 0; i < s.length; i++) {
                Color color = GlobalOptions.getDataColor(s[i].toString());
                if (color != null) {
                    renderer.setSeriesPaint(i, color);
                    renderer.setBaseStroke(new BasicStroke(1.0F));
                }
            }
            plot.add(tmpplot, tmp.getSize());
        }
        plot.setOrientation(PlotOrientation.VERTICAL);
        long endgenerate = System.currentTimeMillis();
        if ( GlobalOptions.isDodebug()) {
            System.out.println("graph generation: " + (endgenerate-begingenerate) + " ms");
        }
        return plot;
    }

    abstract public JFreeChart makegraph(Second g_start, Second g_end);

    abstract public int parse(Second now, String s);
    protected kSar mysar = null;
    protected JFreeChart mygraph = null;
    protected String graphtitle = null;
    protected ArrayList<TimeSeries> Stats = new ArrayList<TimeSeries>();
    protected String[] HeaderStr = null;
    protected int skipColumn = 0;
    protected Map<String, ArrayList> PlotList = new HashMap<String, ArrayList>();
    protected Map<String, TimeTableXYDataset> StackList = new HashMap<String, TimeTableXYDataset>();
    protected boolean printSelected = true;
    protected JCheckBox printCheckBox = null;
    protected GraphConfig graphconfig = null;
}
