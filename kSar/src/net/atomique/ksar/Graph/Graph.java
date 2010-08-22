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
import net.atomique.ksar.Config;
import net.atomique.ksar.GlobalOptions;
import net.atomique.ksar.UI.SortedTreeNode;
import net.atomique.ksar.UI.TreeNodeInfo;
import net.atomique.ksar.XML.GraphConfig;
import net.atomique.ksar.XML.PlotConfig;
import net.atomique.ksar.XML.StackConfig;
import net.atomique.ksar.kSar;
import org.jfree.chart.ChartPanel;
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
import org.jfree.data.general.SeriesException;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeTableXYDataset;
import org.jfree.data.xy.XYDataset;

/**
 *
 * @author alex
 */
public class Graph {

    public Graph(kSar hissar, GraphConfig g, String Title, String hdrs, int skipcol, SortedTreeNode pp) {
        mysar = hissar;
        graphtitle = Title;
        graphconfig = g;
        printCheckBox = new JCheckBox(graphtitle, printSelected);
        printCheckBox.addItemListener(new java.awt.event.ItemListener() {

            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                if (evt.getSource() == printCheckBox) {
                    printSelected = printCheckBox.isSelected();
                }
            }
        });
        skipColumn = skipcol;
        if (pp != null) {
            TreeNodeInfo infotmp = new TreeNodeInfo(Title, this);
            SortedTreeNode nodetmp = new SortedTreeNode(infotmp);
            mysar.add2tree(pp, nodetmp);
        }
        HeaderStr = hdrs.split("\\s+");
        create_DataStore();
    }

    private void create_DataStore() {
        // create timeseries
        for (int i = skipColumn; i < HeaderStr.length; i++) {
            Stats.add(new TimeSeries(HeaderStr[i]));
        }
        // create stack
        SortedSet<String> sortedset = new TreeSet<String>(graphconfig.getStacklist().keySet());
        Iterator<String> it = sortedset.iterator();
        while (it.hasNext()) {
            StackConfig tmp = (StackConfig) graphconfig.getStacklist().get(it.next());
            TimeTableXYDataset tmp2 = new TimeTableXYDataset();
            String[] s = tmp.getHeaderStr().split("\\s+");
            for (int i = 0; i < s.length; i++) {
                StackListbyCol.put(s[i], tmp2);
            }
            StackListbyName.put(tmp.getTitle(), tmp2);
        }
    }

    public int parse_line(Second now, String s) {
        String[] cols = s.split("\\s+");
        Float colvalue = null;
        for (int i = skipColumn; i < HeaderStr.length; i++) {
            try {
                colvalue = new Float(cols[i]);
            } catch (NumberFormatException ne) {
                System.out.println(graphtitle + " " + cols[i] + "is NaN");
                return 0;
            } catch (Exception ae) {
                System.out.println(graphtitle + " " + cols[i] + "is undef " + s);
            }

            try {
                ((TimeSeries) (Stats.get(i - skipColumn))).add(now, colvalue);
            } catch (SeriesException se) {
                System.out.println(graphtitle + "oups" + s);
                System.exit(1);
            }

            TimeTableXYDataset tmp = StackListbyCol.get(HeaderStr[i]);
            if (tmp != null) {
                tmp.add(now, colvalue, HeaderStr[i]);
            }
        }

        return 0;
    }

    public String make_csv() {
        StringBuilder tmp = new StringBuilder();
        tmp.append("Date;");
        tmp.append(getCsvHeader());
        tmp.append("\n");
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
            tmp.append(graphtitle).append(" ").append(tmpseries.getKey());
            tmp.append(";");
        }
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
            ChartUtilities.saveChartAsPNG(new File(filename), this.getgraph(mysar.myparser.get_startofgraph(),mysar.myparser.get_endofgraph()), width, height);
        } catch (IOException e) {
            System.err.println("Unable to write to : " + filename);
            return -1;
        }
        return 0;
    }

    public int saveJPG(final Second g_start, final Second g_end, final String filename, final int width, final int height) {
        try {
            ChartUtilities.saveChartAsJPEG(new File(filename), this.getgraph(mysar.myparser.get_startofgraph(),mysar.myparser.get_endofgraph()), width, height);
        } catch (IOException e) {
            System.err.println("Unable to write to : " + filename);
            return -1;
        }
        return 0;
    }

    public JCheckBox getprintform() {
        return printCheckBox;
    }

    public boolean doPrint() {
        return printSelected;
    }

    public JFreeChart getgraph(Second g_start, Second g_end) {
        if (mygraph == null) {
            mygraph = makegraph(g_start, g_end);
        }
        return mygraph;
    }


    public String getTitle() {
        return graphtitle;
    }

    public boolean isPrintSelected() {
        return printSelected;
    }
    
     private XYDataset create_collection(ArrayList l) {
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

    public ChartPanel get_ChartPanel() {
        if ( chartpanel == null) {
            chartpanel = new ChartPanel(getgraph(mysar.myparser.get_startofgraph(),mysar.myparser.get_endofgraph()));
        }
        return chartpanel;
    }
    
    private JFreeChart makegraph(Second g_start, Second g_end) {
        long begingenerate = System.currentTimeMillis();
        DateAxis axisofdate = new DateAxis("");
        CombinedDomainXYPlot plot = new CombinedDomainXYPlot(axisofdate);
        // do the stacked stuff
        SortedSet<String> sortedset = new TreeSet<String>(graphconfig.getStacklist().keySet());
        Iterator<String> it = sortedset.iterator();
        while (it.hasNext()) {
            StackConfig tmp = (StackConfig) graphconfig.getStacklist().get(it.next());
            if (tmp == null) {
                continue;
            }
            TimeTableXYDataset tmp2 = StackListbyName.get(tmp.getTitle());
            if (tmp2 != null) {
                StackedXYAreaRenderer2 renderer = new StackedXYAreaRenderer2();
                XYPlot temp_plot = new XYPlot(tmp2, axisofdate, graphaxis, renderer);
                for (int i = 0; i < tmp2.getSeriesCount(); i++) {
                    Color color = GlobalOptions.getDataColor(tmp2.getSeriesKey(i).toString());
                    if (color != null) {
                        renderer.setSeriesPaint(i, color);
                        renderer.setBaseStroke(new BasicStroke(1.0F));
                    }
                }
                System.out.println("stack "+ tmp.getTitle() + " size " + tmp.getSize());
                plot.add(temp_plot, tmp.getSize());
            }
        }
        // do the line stuff
        sortedset = new TreeSet<String>(graphconfig.getPlotlist().keySet());
        it = sortedset.iterator();
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
            XYPlot tmpplot = new XYPlot(c, axisofdate, graphaxistitle, renderer);

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
            System.out.println("plot "+ tmp.getTitle() + " size " + tmp.getSize());

        }
        if ( plot.getSubplots().isEmpty() ) {
            return null;
        }
        if (g_start !=null && g_end !=null) {
            System.out.println("range seted");
            axisofdate.setRange(g_start.getStart(), g_end.getEnd());
        }
        plot.setOrientation(PlotOrientation.VERTICAL);
        JFreeChart mychart = new JFreeChart(graphtitle, Config.getDEFAULT_FONT(), plot, true);
        long endgenerate = System.currentTimeMillis();
        mychart.setBackgroundPaint(Color.white);
        if (GlobalOptions.isDodebug()) {
            System.out.println("graph generation: " + (endgenerate - begingenerate) + " ms");
        }
        return mychart;
    }
    
    private kSar mysar = null;
    private JFreeChart mygraph = null;
    private ChartPanel chartpanel =null;
    private NumberAxis graphaxis = new NumberAxis();
    private String graphtitle = null;
    public boolean printSelected = true;
    private JCheckBox printCheckBox = null;
    private GraphConfig graphconfig = null;
    private int skipColumn = 0;
    private String[] HeaderStr = null;
    private ArrayList<TimeSeries> Stats = new ArrayList<TimeSeries>();
    private Map<String, TimeTableXYDataset> StackListbyName = new HashMap<String, TimeTableXYDataset>();
    private Map<String, TimeTableXYDataset> StackListbyCol = new HashMap<String, TimeTableXYDataset>();
}
