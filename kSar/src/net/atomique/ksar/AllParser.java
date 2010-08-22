/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import net.atomique.ksar.GlobalOptions;
import net.atomique.ksar.XML.OSConfig;
import net.atomique.ksar.kSar;
import org.jfree.data.time.Second;

/**
 *
 * @author Max
 */
public abstract class AllParser {

    public AllParser () {
        
    }

    public void init (kSar hissar,String OS, String header) {
        mysar = hissar;
        myosconfig = GlobalOptions.getOSinfo(OS);
        parse_header(header);
    }
    
    public AllParser(kSar hissar, String OS,String header) {
        init(hissar,OS, header);
    }

    public int parse(String line, String[] columns) {
        System.err.println("not implemented");
        return -1;
    }

    public OSConfig get_OSConfig() {
        return myosconfig;
    }


    public void setHostname(String s) {
        Hostname = s;
    }

    public void setOSversion(String s) {
        OSversion = s;
    }

    public void setKernel(String s) {
        Kernel = s;
    }

    public void setCpuType(String s) {
        CpuType = s;
    }

    public void setDate(String s) {
        Date dateSimple1;
        Date dateSimple2;
        Date dateSimple3;
        if (sarStartDate == null) {
            sarStartDate = s;
        }
        if (sarEndDate == null) {
            sarEndDate = s;
        }
        try {
            dateSimple1 = new SimpleDateFormat("MM/dd/yy").parse(s);
            dateSimple2 = new SimpleDateFormat("MM/dd/yy").parse(sarStartDate);
            dateSimple3 = new SimpleDateFormat("MM/dd/yy").parse(sarEndDate);
        } catch (ParseException e) {
            return;
        }
        if (dateSimple1.compareTo(dateSimple2) < 0) {
            sarStartDate = s;
        }
        if (dateSimple1.compareTo(dateSimple3) > 0) {
            sarEndDate = s;
        }
    }

    public void setMacAddress(String s) {
        MacAddress = s;
    }

    public void setMemory(String s) {
        Memory = s;
    }

    public void setNBDisk(String s) {
        NBDisk = s;
    }

    public void setNBCpu(String s) {
        NBCpu = s;
    }

    public void setENT(String s) {
        ENT = s;
    }

    public String getDate() {
        if (sarStartDate.equals(sarEndDate)) {
            return sarStartDate;
        } else {
            return sarStartDate + " to " + sarEndDate;
        }
    }

    public String getOSInfo() {
        StringBuilder tmpstr = new StringBuilder();
        tmpstr.append("OS Type: ").append(ostype).append(" (").append(Detect).append(" detected)\n");
        if (OSversion != null) {
           tmpstr.append("OS Version: ").append(OSversion).append("\n");
        }
        if (Kernel != null) {
            tmpstr.append("Kernel Release: ").append(Kernel).append("\n");
        }
        if (CpuType != null) {
            tmpstr.append("CPU Type: ").append(CpuType).append("\n");
        }
        if (Hostname != null) {
            tmpstr.append("Hostname: ").append(Hostname).append("\n");
        }
        if (MacAddress != null) {
            tmpstr.append("Mac Address: ").append(MacAddress).append("\n");
        }
        if (Memory != null) {
            tmpstr.append("Memory: ").append(Memory).append("\n");
        }
        if (NBDisk != null) {
            tmpstr.append("Number of disks: ").append(NBDisk).append("\n");
        }
        if (NBCpu != null) {
            tmpstr.append("Number of CPU: ").append(NBCpu).append("\n");
        }
        if (ENT != null) {
            tmpstr.append("Ent: ").append(ENT).append("\n");
        }
        if (sarStartDate != null) {
            tmpstr.append("Start of SAR: ").append(sarStartDate).append("\n");
        }
        if (sarEndDate != null) {
            tmpstr.append("End of SAR: ").append(sarEndDate).append("\n");
        }

        tmpstr.append("\n");

        return tmpstr.toString();
    }

    public String getOriginal_line() {
        return original_line;
    }

    public void setOriginal_line(String original_line) {
        this.original_line = original_line;
    }

    public AllParser getParser() {
        return parser;
    }

    public void setParser(AllParser parser) {
        this.parser = parser;
    }

    public String gethostName() {
        return Hostname;
    }


    abstract public void parse_header(String s);
    
    protected OSConfig myosconfig = null;
    protected kSar mysar = null;
    protected Map<String,Object> ListofGraph = new HashMap<String, Object>();
    protected String currentStat = "NONE";
    protected String lastStat = null;
    protected Object currentStatObj = null;
    //List graphlist = new ArrayList();
    protected Second startofstat = null;
    protected Second endofstat = null;
    protected int firstdatacolumn =0;

    private String ostype = null;
    private String Hostname = null;
    private String OSversion = null;
    private String Kernel = null;
    private String CpuType = null;
    private String sarStartDate = null;
    private String sarEndDate = null;
    private String MacAddress = null;
    private String Memory = null;
    private String NBDisk = null;
    private String NBCpu = null;
    private String ENT = null;
    private String Detect = null;
    private String original_line=null;
    private AllParser parser = null;
    protected int day = 0;
    protected int month = 0;
    protected int year = 0;
    
}
