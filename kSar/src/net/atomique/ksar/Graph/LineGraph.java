/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar.Graph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.ArrayList;
import net.atomique.ksar.Graph.BaseGraph;
import net.atomique.ksar.Config;
import net.atomique.ksar.Config;
import net.atomique.ksar.GlobalOptions;
import net.atomique.ksar.GlobalOptions;
import net.atomique.ksar.UI.SortedTreeNode;
import net.atomique.ksar.UI.TreeNodeInfo;
import net.atomique.ksar.kSar;
import net.atomique.ksar.kSar;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.general.SeriesException;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;

/**
 *
 * @author Max
 */
public class LineGraph extends BaseGraph {

    public LineGraph(kSar hissar, String Title, String str, int i, SortedTreeNode pp) {
        super(hissar, Title, i);
        this.setTitle(str);
        if (pp != null) {
            TreeNodeInfo infotmp = new TreeNodeInfo(Title, this);
            SortedTreeNode nodetmp = new SortedTreeNode(infotmp);
            mysar.add2tree(pp, nodetmp);
        }
    }

    
    public void setTitle(String s) {
        HeaderStr = s.split("\\s+");
        for (int i = skipColumn; i < HeaderStr.length; i++) {
            Stats.put(HeaderStr[i], new TimeSeries(HeaderStr[i]));
        }
    }

    public int parse(Second now, String s) {
        String[] cols = s.split("\\s+");
        Float colvalue = null;
        for (int i = skipColumn; i < HeaderStr.length; i++) {
            try {
                colvalue = new Float(cols[i]);
            } catch (NumberFormatException ne) {
                continue;
            }
            try {
            ((TimeSeries) (Stats.get(HeaderStr[i]))).add(now, colvalue);
            } catch ( SeriesException se) {
                System.out.println(graphtitle + "oups" + s);
                System.exit(1);
            }
        }

        return 0;
    }

    public JFreeChart makegraph(Second g_start, Second g_end) {
        long begingenerate = System.currentTimeMillis();
        CombinedDomainXYPlot plot = new CombinedDomainXYPlot(new DateAxis(""));
        for (Object key : PlotList.keySet()) {
            ArrayList tmp = (ArrayList) PlotList.get(key);
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
}
