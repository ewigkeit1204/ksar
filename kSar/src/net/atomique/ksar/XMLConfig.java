/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import net.atomique.ksar.XML.ColorConfig;
import net.atomique.ksar.XML.GraphConfig;
import net.atomique.ksar.XML.OSConfig;
import net.atomique.ksar.XML.PlotConfig;
import net.atomique.ksar.XML.StatConfig;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author Max
 */
public class XMLConfig extends DefaultHandler {

    public XMLConfig(String filename) {
        load_config(filename);
    }

    public XMLConfig(InputStream is) {
        load_config(is);
    }

    private void load_config(InputStream is) {
        SAXParserFactory fabric = null;
        SAXParser parser = null;
        try {
            fabric = SAXParserFactory.newInstance();
            parser = fabric.newSAXParser();
            parser.parse(is, this);
        } catch (ParserConfigurationException ex) {
            ex.printStackTrace();
        } catch (SAXException ex) {
            ex.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        dump_XML();
        try {
            is.close();
        } catch (IOException ex) {
            Logger.getLogger(XMLConfig.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }

    private void load_config(String xmlfile) {
        SAXParserFactory fabric = null;
        SAXParser parser = null;
        try {
            fabric = SAXParserFactory.newInstance();
            parser = fabric.newSAXParser();
            parser.parse(xmlfile, this);
        } catch (ParserConfigurationException ex) {
            ex.printStackTrace();
        } catch (SAXException ex) {
            ex.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }

    public void dump_XML() {
        SortedSet<String> sortedset = new TreeSet<String>(GlobalOptions.getOSlist().keySet());
        Iterator<String> it = sortedset.iterator();
        while (it.hasNext()) {
            OSConfig tmp = (OSConfig) GlobalOptions.getOSlist().get(it.next());
            System.out.println("-OS-" + tmp.getOSname());
            SortedSet<String> sortedset2 = new TreeSet<String>(tmp.getStatHash().keySet());
            Iterator<String> it2 = sortedset2.iterator();
            while (it2.hasNext()) {
                StatConfig tmp2 = (StatConfig) tmp.getStatHash().get(it2.next());
                System.out.println("--STAT-- "
                        + tmp2.getStatName() + "=> "
                        + tmp2.getGraphName() + " "
                        + tmp2.getHeaderStr());
            }
            SortedSet<String> sortedset3 = new TreeSet<String>(tmp.getGraphHash().keySet());
            Iterator<String> it3 = sortedset3.iterator();
            while (it3.hasNext()) {
                GraphConfig tmp3 = (GraphConfig) tmp.getGraphHash().get(it3.next());
                System.out.println("---GRAPH--- "
                        + tmp3.getName() + "=> "
                        + tmp3.getTitle());
                SortedSet<String> sortedset4 = new TreeSet<String>(tmp3.getPlotlist().keySet());
                Iterator<String> it4 = sortedset4.iterator();
                while (it4.hasNext()) {
                    PlotConfig tmp4 = (PlotConfig) tmp3.getPlotlist().get(it4.next());
                    System.out.println("----PLOT---- "
                            + tmp4.getTitle() + "=> "
                            + tmp4.getHeaderStr());

                }
            }
        }

    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempval = new String(ch, start, length);
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if ("ConfiG".equals(qName)) {
            // config found
        }
        if ("colors".equals(qName)) {
            in_colors = true;
        }
        if ("OS".equals(qName)) {
            in_OS = true;
        }

        // COLORS
        if (in_colors) {
            if ("itemcolor".equals(qName)) {
                cur_color = new ColorConfig(attributes.getValue("name"));
                in_color = true;
            }
        }

        // OS
        if (in_OS) {
            if ("OSType".equals(qName)) {
                currentOS = GlobalOptions.getOSlist().get(attributes.getValue("name"));
                if (currentOS == null) {
                    currentOS = new OSConfig(attributes.getValue("name"));
                    GlobalOptions.getOSlist().put(currentOS.getOSname(), currentOS);
                }
            }
            if (currentOS != null) {
                if ("Stat".equals(qName)) {
                    currentStat = new StatConfig(attributes.getValue("name"));
                    currentOS.addStat(currentStat);
                }
                if ("Graph".equals(qName)) {
                    currentGraph = new GraphConfig(attributes.getValue("name"), attributes.getValue("Title"), attributes.getValue("type"));
                    currentOS.addGraph(currentGraph);
                }
                if (currentGraph != null) {
                    if ("Plot".equals(qName)) {
                        currentPlot = new PlotConfig(attributes.getValue("title"));
                        currentGraph.addPlot(currentPlot);
                    }
                }
            }
        }
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        if ("ConfiG".equals(qName)) {
            beenparse = true;
        }
        if ("colors".equals(qName)) {
            in_colors = false;
        }
        if ("OSType".equals(qName)) {
            currentOS = null;
        }
        if ("Stat".equals(qName)) {
            currentStat = null;
        }
        if ("Graph".equals(qName)) {
            currentGraph = null;
        }
        if (currentStat != null) {
            if ("headerstr".equals(qName)) {
                currentStat.setHeaderStr(tempval);
            }
            if ("graphname".equals(qName)) {
                currentStat.setGraphName(tempval);
            }            
        }

        if (currentPlot != null) {
            if ("Plot".equals(qName)) {
                currentPlot.setHeaderStr(tempval);
                currentPlot = null;
            }
        }
        if ("itemcolor".equals(qName)) {
            if (cur_color.is_valid()) {
                GlobalOptions.getColorlist().put(cur_color.getData_title(), cur_color);
            } else {
                System.err.println("Err: " + cur_color.getError_message());
                cur_color = null;
            }
            in_color = false;
        }

        if (in_color) {
            if ("color".equals(qName) && cur_color != null) {
                cur_color.setData_color(tempval);
            }
        }
    }
    public boolean beenparse = false;
    private ColorConfig cur_color = null;
    private String tempval;
    private boolean in_color = false;
    private boolean in_colors = false;
    private boolean in_OS = false;
    private OSConfig currentOS = null;
    private StatConfig currentStat = null;
    private GraphConfig currentGraph = null;
    private PlotConfig currentPlot = null;
    
}
