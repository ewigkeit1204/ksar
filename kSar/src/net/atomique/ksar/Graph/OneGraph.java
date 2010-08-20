/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar.Graph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.ArrayList;
import net.atomique.ksar.Config;
import net.atomique.ksar.GlobalOptions;
import net.atomique.ksar.UI.SortedTreeNode;
import net.atomique.ksar.UI.TreeNodeInfo;
import net.atomique.ksar.XML.GraphConfig;
import net.atomique.ksar.kSar;
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
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeTableXYDataset;

/**
 *
 * @author Max
 */
public class OneGraph extends BaseGraph {

    public OneGraph(kSar hissar, GraphConfig g,String Title, String str, int i, SortedTreeNode pp) {
        super(hissar, Title, i,g);
        this.setTitle(str);
        if (pp != null) {
            TreeNodeInfo infotmp = new TreeNodeInfo(Title, this);
            SortedTreeNode nodetmp = new SortedTreeNode(infotmp);
            mysar.add2tree(pp, nodetmp);
        }
    }
    
    public int parse(Second now, String s) {
        String[] cols = s.split("\\s+");
        Float colvalue = null;
        for (int i = skipColumn; i < HeaderStr.length; i++) {
            try {
                colvalue = new Float(cols[i]);
            } catch (NumberFormatException ne) {
                System.out.println(graphtitle + " " + cols[i] + "is NaN");
                return 0;
            } catch (Exception ae) {
                System.out.println(graphtitle + " " + cols[i] + "is undef " + s );
            }

            try {
                ((TimeSeries) (Stats.get(i - skipColumn))).add(now, colvalue);
            } catch (SeriesException se) {
                System.out.println(graphtitle + "oups" + s);
                System.exit(1);
            }
            
            TimeTableXYDataset tmp = StackList.get(HeaderStr[i]);
            if (tmp != null ) {
                tmp.add(now, colvalue, HeaderStr[i]);
            }
        }

        return 0;
    }


    public JFreeChart makegraph(Second g_start, Second g_end) {
        long begingenerate = System.currentTimeMillis();
        CombinedDomainXYPlot plot = new CombinedDomainXYPlot(new DateAxis(""));
        ArrayList tmplist = new ArrayList();
        for (Object key : StackList.values()) {
            if ( ! tmplist.contains(key) ) {
                tmplist.add(key);
            }
        }
        for (Object key : tmplist) {
            TimeTableXYDataset tmp = (TimeTableXYDataset)key;
            StackedXYAreaRenderer2 renderer = new StackedXYAreaRenderer2();
            XYPlot all_plot = new XYPlot(tmp, new DateAxis(null), graphaxis, renderer);
            for (int i = 0; i < tmp.getSeriesCount(); i++) {
                Color color = GlobalOptions.getDataColor(tmp.getSeriesKey(i).toString());
                if (color != null) {
                    renderer.setSeriesPaint(i, color);
                    renderer.setBaseStroke(new BasicStroke(1.0F));
                }
            }
            plot.add(all_plot, 1);
        }
        for (Object key : PlotList.keySet()) {
            ArrayList tmp = (ArrayList) PlotList.get((String)key);
            XYItemRenderer renderer = new StandardXYItemRenderer();
            XYPlot all_plot = create_plot((String) key, tmp);
            if (all_plot == null) {
                continue;
            }
            for (int i = 0; i < tmp.size(); i++) {
                Color color = GlobalOptions.getDataColor(tmp.get(i).toString());
                if (color != null) {
                    renderer.setSeriesPaint(i, color);
                    renderer.setBaseStroke(new BasicStroke(1.0F));
                }
            }
            
            plot.add(all_plot, 1);
        }
        plot.setOrientation(PlotOrientation.VERTICAL);

        JFreeChart mychart = new JFreeChart(graphtitle, Config.getDEFAULT_FONT(), plot, true);
        long endgenerate = System.currentTimeMillis();
        mychart.setBackgroundPaint(Color.white);
        if ( GlobalOptions.isDodebug()) {
            System.out.println("graph generation: " + (endgenerate-begingenerate) + " ms");
        }
        return mychart;
    }
    
    private NumberAxis graphaxis = new NumberAxis();
    private String axisTitle = "";    
    
}
