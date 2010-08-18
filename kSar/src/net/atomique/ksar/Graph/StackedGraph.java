/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar.Graph;

import java.awt.BasicStroke;
import java.awt.Color;
import net.atomique.ksar.Graph.BaseGraph;
import net.atomique.ksar.Config;
import net.atomique.ksar.Config;
import net.atomique.ksar.GlobalOptions;
import net.atomique.ksar.GlobalOptions;
import net.atomique.ksar.kSar;
import net.atomique.ksar.kSar;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StackedXYAreaRenderer2;
import org.jfree.data.general.SeriesException;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeTableXYDataset;

/**
 *
 * @author Max
 */
public class StackedGraph extends BaseGraph {

    public StackedGraph(kSar hissar, String str, int skipColumn) {
        super(hissar, str, skipColumn);
    }

    public void setTitle(String s) {
        HeaderStr = s.split("\\s+");
        for (int i = skipColumn+1; i < HeaderStr.length; i++) {
            Stats.put(HeaderStr[i], new TimeSeries(HeaderStr[i]));
        }
    }

    public int parse(Second now, String s) {
        String[] cols = s.split("\\s+");
        Float colvalue = null;
        for (int i = skipColumn+1; i < HeaderStr.length; i++) {
            try {
            colvalue = new Float(cols[i]);
            } catch ( NumberFormatException ne ) {
                System.out.println(graphtitle + " " + cols[i] + "is NaN");
                return 0;
            }
            try {
            ((TimeSeries) (Stats.get(HeaderStr[i]))).add(now, colvalue);
            } catch ( SeriesException se) {
                System.out.println(graphtitle + "oups" + s);
                System.exit(1);
            }
            stacked.add(now,colvalue,HeaderStr[i]);
        }

        return 0;
    }
    public void setAxisTitle(String s) {
        this.axisTitle= s ;
    }

    public JFreeChart makegraph(Second g_start, Second g_end) {
        StackedXYAreaRenderer2 renderer = new StackedXYAreaRenderer2();
        for (int i=0; i < stacked.getSeriesCount(); i++ ) {
            Color color= GlobalOptions.getDataColor(stacked.getSeriesKey(i).toString());
            if ( color != null ) {
                renderer.setSeriesPaint(i, color);
                renderer.setBaseStroke(new BasicStroke(1.0F));
            }

            
        }
        graphaxis= new NumberAxis(axisTitle);
        graphaxis.setRange(0.0D, 100D);
        XYPlot all = new XYPlot(stacked, new DateAxis(null), graphaxis, renderer);
        
        JFreeChart mychart = new JFreeChart(graphtitle, Config.getDEFAULT_FONT(), all, true);
        mychart.setBackgroundPaint(Color.white);
        return mychart;
    }

    private NumberAxis graphaxis = null;
    private String axisTitle = "";
    private TimeTableXYDataset stacked = new TimeTableXYDataset();
   
}
