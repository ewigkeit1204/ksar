/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar.Solaris;

import net.atomique.ksar.Graph.LineGraph;
import net.atomique.ksar.AllParser;
import net.atomique.ksar.Graph.BaseGraph;
import net.atomique.ksar.Graph.BaseList;
import net.atomique.ksar.GlobalOptions;
import net.atomique.ksar.Graph.LineList;
import net.atomique.ksar.Graph.StackedList;
import net.atomique.ksar.kSar;
import org.jfree.data.time.Second;

/**
 *
 * @author Max
 */
public class Parser extends AllParser {

    public Parser(kSar hissar) {
        super(hissar);
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
            if ( ! "DISKS".equals(currentStat)  ) {
                return -1;
            }
            firstdatacolumn=0;
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
            firstdatacolumn=1;
        }

        
        //00:20:01     CPU  i000/s  i001/s  i002/s  i008/s  i009/s  i010/s  i011/s  i012/s  i014/s
        if ("CPU".equals(columns[firstdatacolumn]) && line.matches(".*i([0-9]+)/s.*")) {
                currentStat = "IGNORE";
                return 1;
        }
      

        if (columns.length == 2) {
            // * 00:00:00       proc/s
            if ("proc/s".equals(columns[firstdatacolumn])) {
                currentStat = "PROC";
                getStatGraph("PROC", line);
                return 0;
            }
            // * 00:00:02      cswch/s
            if ("cswch/s".equals(columns[firstdatacolumn])) {
                currentStat = "CSWCH";
                getStatGraph("CSWCH", line);
                return 0;
            }
        }

        if (columns.length == 3) {
            // * 00:00:02         INTR    intr/s
            if ("INTR".equals(columns[firstdatacolumn])
                    && "intr/s".equals(columns[firstdatacolumn+1])) {
                currentStat = "INTR";
                getStatGraph("INTR", line);
                return 0;
            }
            // * 00:00:02     pswpin/s pswpout/s
            if ("pswpin/s".equals(columns[firstdatacolumn])
                    && "pswpout/s".equals(columns[firstdatacolumn+1])) {
                currentStat = "SWAP";
                getStatGraph("SWAP", line);
                return 0;
            }
            //* 19:12:11       proc/s   cswch/s
            if ("proc/s".equals(columns[firstdatacolumn])
                    && "cswch/s".equals(columns[firstdatacolumn+2])) {
                currentStat = "PROC2";
                getStatGraph("PROC2", line);
                return 0;
            }
        }

        if (columns.length == 4) {
            if ("DEV".equals(columns[firstdatacolumn])
                    && "tps".equals(columns[firstdatacolumn+1])
                    && "blks/s".equals(columns[firstdatacolumn+2])) {
                currentStat = "DEV";
                getStatGraph("DEV", line);
                return 0;
            }
            //* 20:58:24      frmpg/s   bufpg/s   campg/s
            if ("frmpg/s".equals(columns[firstdatacolumn])
                    && "bufpg/s".equals(columns[firstdatacolumn+1])
                    && "campg/s".equals(columns[firstdatacolumn+2])) {
                currentStat = "PAGE";
                getStatGraph("PAGE", line);
                return 0;
            }

        }

        if (columns.length == 5) {
            //* 00:00:02      frmpg/s   shmpg/s   bufpg/s   campg/s
            if ("frmpg/s".equals(columns[firstdatacolumn])
                    && "shmpg/s".equals(columns[firstdatacolumn+1])
                    && "bufpg/s".equals(columns[firstdatacolumn+2])
                    && "campg/s".equals(columns[firstdatacolumn+3])) {
                currentStat = "PAGE";
                getStatGraph("PAGE", line);
                return 0;
            }
            //* 00:00:02      runq-sz  plist-sz   ldavg-1   ldavg-5
            if ("runq-sz".equals(columns[firstdatacolumn])
                    && "plist-sz".equals(columns[firstdatacolumn+1])
                    && "ldavg-1".equals(columns[firstdatacolumn+2])
                    && "ldavg-5".equals(columns[firstdatacolumn+3])) {
                currentStat = "LOAD";
                getStatGraph("LOAD", line);
                return 0;
            }
            //* 00:00:00       DEV              tps    rd_sec/s  wr_sec/s
            if ("DEV".equals(columns[firstdatacolumn])
                    && "tps".equals(columns[firstdatacolumn+1])
                    && "rd_sec/s".equals(columns[firstdatacolumn+2])
                    && "wr_sec/s".equals(columns[firstdatacolumn+3])) {
                currentStat = "DEV";
                getStatGraph("DEV", line);
                return 0;
            }
            //* 20:58:24     pgpgin/s pgpgout/s   fault/s  majflt/s
            if ("pgpgin/s".equals(columns[firstdatacolumn])
                    && "pgpgout/s".equals(columns[firstdatacolumn+1])
                    && "fault/s".equals(columns[firstdatacolumn+2])
                    && "majflt/s".equals(columns[firstdatacolumn+3])) {
                currentStat = "PAGING";
                getStatGraph("PAGING", line);
                return 0;
            }
            // 19:12:11    dentunusd   file-nr  inode-nr    pty-nr
            if ("dentunusd".equals(columns[firstdatacolumn])
                    && "file-nr".equals(columns[firstdatacolumn+1])
                    && "inode-nr".equals(columns[firstdatacolumn+2])
                    && "pty-nr".equals(columns[firstdatacolumn+3])) {
                currentStat = "IGNORE";
                return 1;
            }
        }

        if (columns.length == 6) {
            //* 12:00:03 AM       CPU     %user     %nice   %system   %idle
            if ("%user".equals(columns[firstdatacolumn+1])
                    && "%nice".equals(columns[firstdatacolumn+2])
                    && "%system".equals(columns[firstdatacolumn+3])
                    && "%idle".equals(columns[firstdatacolumn+4])) {
                currentStat = "CPU";
                getStatGraph("CPU", line);
                return 0;
            }
            //* 00:00:02          tps      rtps      wtps   bread/s   bwrtn/s
            if ("tps".equals(columns[firstdatacolumn])
                    && "rtps".equals(columns[firstdatacolumn+1])
                    && "wtps".equals(columns[firstdatacolumn+2])
                    && "bread/s".equals(columns[firstdatacolumn+3])
                    && "bwrtn/s".equals(columns[firstdatacolumn+4])) {
                currentStat = "IO";
                getStatGraph("IO", line);
                return 0;
            }
            //* 00:00:02       totsck    tcpsck    udpsck    rawsck   ip-frag
            if ("totsck".equals(columns[firstdatacolumn])
                    && "tcpsck".equals(columns[firstdatacolumn+1])
                    && "udpsck".equals(columns[firstdatacolumn+2])
                    && "rawsck".equals(columns[firstdatacolumn+3])
                    && "ip-frag".equals(columns[firstdatacolumn+4])) {
                currentStat = "SOCKET";
                getStatGraph("SOCKET", line);
                return 0;
            }
            //* 00:00:02      runq-sz  plist-sz   ldavg-1   ldavg-5   ladvg-15
            if ("runq-sz".equals(columns[firstdatacolumn])
                    && "plist-sz".equals(columns[firstdatacolumn+1])
                    && "ldavg-1".equals(columns[firstdatacolumn+2])
                    && "ldavg-5".equals(columns[firstdatacolumn+3])
                    && "ldavg-15".equals(columns[firstdatacolumn+4])) {
                currentStat = "LOAD";
                getStatGraph("LOAD", line);
                return 0;
            }
            //* 19:12:11    kbswpfree kbswpused  %swpused  kbswpcad   %swpcad
            if ("kbswpfree".equals(columns[firstdatacolumn])
                    && "kbswpused".equals(columns[firstdatacolumn+1])
                    && "%swpused".equals(columns[firstdatacolumn+2])
                    && "kbswpcad".equals(columns[firstdatacolumn+3])
                    && "%swpcad".equals(columns[firstdatacolumn+4])) {
                currentStat = "KSWAP";
                getStatGraph("KSWAP", line);
                return 0;
            }

        }
        if (columns.length == 7) {
            //* 00:00:00          CPU     %user     %nice   %system   %iowait  %idle
            if ("%user".equals(columns[firstdatacolumn+1])
                    && "%nice".equals(columns[firstdatacolumn+2])
                    && "%system".equals(columns[firstdatacolumn+3])
                    && "%iowait".equals(columns[firstdatacolumn+4])
                    && "%idle".equals(columns[firstdatacolumn+5])) {
                currentStat = "CPU";
                getStatGraph("CPU", line);
                return 0;
            }
            //00:00:02     pgpgin/s pgpgout/s  activepg  inadtypg  inaclnpg  inatarpg
            if ("pgpgin/s".equals(columns[firstdatacolumn])
                    && "pgpgout/s".equals(columns[firstdatacolumn+1])
                    && "activepg".equals(columns[firstdatacolumn+2])
                    && "inadtypg".equals(columns[firstdatacolumn+3])
                    && "inaclnpg".equals(columns[firstdatacolumn+4])
                    && "inatarpg".equals(columns[firstdatacolumn+5])) {
                currentStat = "PAGING";
                getStatGraph("PAGING", line);
                return 0;
            }
            //* 20:58:24       call/s retrans/s    read/s   write/s  access/s  getatt/s
            if ("call/s".equals(columns[firstdatacolumn])
                    && "retrans/s".equals(columns[firstdatacolumn+1])
                    && "read/s".equals(columns[firstdatacolumn+2])
                    && "write/s".equals(columns[firstdatacolumn+3])
                    && "access/s".equals(columns[firstdatacolumn+4])
                    && "getatt/s".equals(columns[firstdatacolumn+5])) {
                currentStat = "NFSC";
                getStatGraph("NFSC", line);
                return 0;
            }
            //* 19:12:11       totsck    tcpsck    udpsck    rawsck   ip-frag    tcp-tw
            if ("totsck".equals(columns[firstdatacolumn])
                    && "tcpsck".equals(columns[firstdatacolumn+1])
                    && "udpsck".equals(columns[firstdatacolumn+2])
                    && "rawsck".equals(columns[firstdatacolumn+3])
                    && "ip-frag".equals(columns[firstdatacolumn+4])
                    && "tcp-tw".equals(columns[firstdatacolumn+5])) {
                currentStat = "SOCKET";
                getStatGraph("SOCKET", line);
                return 0;
            }
        }
        if (columns.length == 8) {
            //* 17:37:47          CPU     %user     %nice   %system   %iowait  %steal   %idle
            if ("%user".equals(columns[firstdatacolumn])
                    && "%nice".equals(columns[firstdatacolumn+2])
                    && "%system".equals(columns[firstdatacolumn+3])
                    && "%iowait".equals(columns[firstdatacolumn+4])
                    && "%steal".equals(columns[firstdatacolumn+5])
                    && "%idle".equals(columns[firstdatacolumn+6])) {
                currentStat = "CPU";
                getStatGraph("CPU", line);
                return 0;
            }
            //* 20:58:24          TTY   rcvin/s   xmtin/s framerr/s prtyerr/s     brk/s   ovrun/s
            if ("TTY".equals(columns[firstdatacolumn])
                    && "rcvin/s".equals(columns[firstdatacolumn+1])
                    && "xmtin/s".equals(columns[firstdatacolumn+2])
                    && "framerr/s".equals(columns[firstdatacolumn+3])
                    && "prtyerr/s".equals(columns[firstdatacolumn+4])
                    && "brk/s".equals(columns[firstdatacolumn+5])
                    && "ovrun/s".equals(columns[firstdatacolumn+6])) {
                currentStat = "IGNORE";
                return 1;
            }
            //19:12:11    kbmemfree kbmemused  %memused kbbuffers  kbcached  kbcommit   %commit
            if ( "kbmemfree".equals(columns[firstdatacolumn])
                    && "kbmemused".equals(columns[firstdatacolumn+1])
                    && "%memused".equals(columns[firstdatacolumn+2])
                    && "kbbuffers".equals(columns[firstdatacolumn+3])
                    && "kbcached".equals(columns[firstdatacolumn+4])
                    && "kbcommit".equals(columns[firstdatacolumn+5])
                    && "%commit".equals(columns[firstdatacolumn+6])) {
                currentStat = "KMEM";
                getStatGraph("KMEM", line);
                return 0;
            }
        }

        if (columns.length == 9) {
            //* 00:00:02        IFACE   rxpck/s   txpck/s   rxbyt/s   txbyt/s   rxcmp/s   txcmp/s  rxmcst/s
            if ("IFACE".equals(columns[firstdatacolumn])
                    && "rxpck/s".equals(columns[firstdatacolumn+1])
                    && "txpck/s".equals(columns[firstdatacolumn+2])
                    && "rxbyt/s".equals(columns[firstdatacolumn+3])
                    && "txbyt/s".equals(columns[firstdatacolumn+4])
                    && "rxcmp/s".equals(columns[firstdatacolumn+5])
                    && "txcmp/s".equals(columns[firstdatacolumn+6])
                    && "rxmcst/s".equals(columns[firstdatacolumn+7])) {
                currentStat = "INET1";
                getStatGraph("INET1", line);
                return 0;
            }
            //* 19:12:11        IFACE   rxpck/s   txpck/s    rxkB/s    txkB/s   rxcmp/s   txcmp/s  rxmcst/s
            if ("IFACE".equals(columns[firstdatacolumn])
                    && "rxpck/s".equals(columns[firstdatacolumn+1])
                    && "txpck/s".equals(columns[firstdatacolumn+2])
                    && "rxkB/s".equals(columns[firstdatacolumn+3])
                    && "txkB/s".equals(columns[firstdatacolumn+4])
                    && "rxcmp/s".equals(columns[firstdatacolumn+5])
                    && "txcmp/s".equals(columns[firstdatacolumn+6])
                    && "rxmcst/s".equals(columns[firstdatacolumn+7])) {
                currentStat = "INET1";
                getStatGraph("INET1", line);
                return 0;
            }
        }


        if (columns.length == 10) {

            //* 00:00:00          CPU     %usr %nice %sys %iowait %steal %irq %soft %guest %idle
            if ("%usr".equals(columns[firstdatacolumn+1])
                    && "%nice".equals(columns[firstdatacolumn+2])
                    && "%sys".equals(columns[firstdatacolumn+3])
                    && "%iowait".equals(columns[firstdatacolumn+4])
                    && "%steal".equals(columns[firstdatacolumn+5])
                    && "%irq".equals(columns[firstdatacolumn+6])
                    && "%guest".equals(columns[firstdatacolumn+7])
                    && "%idle".equals(columns[firstdatacolumn+8])) {
                currentStat = "CPU";
                getStatGraph("CPU", line);
                return 0;
            }
            //* 00:00:02    kbmemfree kbmemused  %memused kbmemshrd kbbuffers  kbcached kbswpfree kbswpused  %swpused
            if ("kbmemfree".equals(columns[firstdatacolumn])
                    && "kbmemused".equals(columns[firstdatacolumn+1])
                    && "%memused".equals(columns[firstdatacolumn+2])
                    && "kbmemshrd".equals(columns[firstdatacolumn+3])
                    && "kbbuffers".equals(columns[firstdatacolumn+4])
                    && "kbcached".equals(columns[firstdatacolumn+5])
                    && "kbswpfree".equals(columns[firstdatacolumn+6])
                    && "kbswpused".equals(columns[firstdatacolumn+7])
                    && "%swpused".equals(columns[firstdatacolumn+8])) {
                currentStat = "KMEM";
                getStatGraph("KMEM", line);
                return 0;
            }
            //* 20:58:24    kbmemfree kbmemused  %memused kbbuffers  kbcached kbswpfree kbswpused  %swpused  kbswpcad
            if ("kbmemfree".equals(columns[firstdatacolumn])
                    && "kbmemused".equals(columns[firstdatacolumn+1])
                    && "%memused".equals(columns[firstdatacolumn+2])
                    && "kbbuffers".equals(columns[firstdatacolumn+3])
                    && "kbcached".equals(columns[firstdatacolumn+4])
                    && "kbswpfree".equals(columns[firstdatacolumn+5])
                    && "kbswpused".equals(columns[firstdatacolumn+6])
                    && "%swpused".equals(columns[firstdatacolumn+7])
                    && "kbswpcad".equals(columns[firstdatacolumn+8])) {
                currentStat = "KMEM";
                getStatGraph("KMEM", line);
                return 0;
            }
            //* 20:58:24          DEV       tps  rd_sec/s  wr_sec/s  avgrq-sz  avgqu-sz     await     svctm     %util
            if ("DEV".equals(columns[firstdatacolumn])
                    && "tps".equals(columns[firstdatacolumn+1])
                    && "rd_sec/s".equals(columns[firstdatacolumn+2])
                    && "wr_sec/s".equals(columns[firstdatacolumn+3])
                    && "avgrq-sz".equals(columns[firstdatacolumn+4])
                    && "avgqu-sz".equals(columns[firstdatacolumn+5])
                    && "await".equals(columns[firstdatacolumn+6])
                    && "svctm".equals(columns[firstdatacolumn+7])
                    && "%util".equals(columns[firstdatacolumn+8])) {
                currentStat = "DEV";
                getStatGraph("DEV", line);
                return 0;
            }
            //06:54:02    dentunusd   file-sz  inode-sz  super-sz %super-sz  dquot-sz %dquot-sz  rtsig-sz %rtsig-sz
            if ("dentunusd".equals(columns[firstdatacolumn])
                    && "file-sz".equals(columns[firstdatacolumn+1])
                    && "inode-sz".equals(columns[firstdatacolumn+2])
                    && "super-sz".equals(columns[firstdatacolumn+3])
                    && "%super-sz".equals(columns[firstdatacolumn+4])
                    && "dquot-sz".equals(columns[firstdatacolumn+5])
                    && "%dquot-sz".equals(columns[firstdatacolumn+6])
                    && "rtsig-sz".equals(columns[firstdatacolumn+7])
                    && "%rtsig-sz".equals(columns[firstdatacolumn+8])) {
                currentStat = "IGNORE";
                return 1;
            }
            //* 19:12:11     pgpgin/s pgpgout/s   fault/s  majflt/s  pgfree/s pgscank/s pgscand/s pgsteal/s    %vmeff
            if ("pgpgin/s".equals(columns[firstdatacolumn])
                    && "pgpgout/s".equals(columns[firstdatacolumn+1])
                    && "fault/s".equals(columns[firstdatacolumn+2])
                    && "majflt/s".equals(columns[firstdatacolumn+3])
                    && "pgfree/s".equals(columns[firstdatacolumn+4])
                    && "pgscank/s".equals(columns[firstdatacolumn+5])
                    && "pgscand/s".equals(columns[firstdatacolumn+6])
                    && "pgsteal/s".equals(columns[firstdatacolumn+7])
                    && "%vmeff".equals(columns[firstdatacolumn+8])) {
                currentStat = "PAGING";
                getStatGraph("PAGING", line);
                return 1;
            }
        }

        if (columns.length == 11 ) {
            //* 20:58:24        IFACE   rxerr/s   txerr/s    coll/s  rxdrop/s  txdrop/s  txcarr/s  rxfram/s  rxfifo/s  txfifo/s
            if ("IFACE".equals(columns[firstdatacolumn])
                    && "rxerr/s".equals(columns[firstdatacolumn+1])
                    && "txerr/s".equals(columns[firstdatacolumn+2])
                    && "coll/s".equals(columns[firstdatacolumn+3])
                    && "rxdrop/s".equals(columns[firstdatacolumn+4])
                    && "txdrop/s".equals(columns[firstdatacolumn+5])
                    && "txcarr/s".equals(columns[firstdatacolumn+6])
                    && "rxfram/s".equals(columns[firstdatacolumn+7])
                    && "rxfifo/s".equals(columns[firstdatacolumn+8])
                    && "txfifo/s".equals(columns[firstdatacolumn+9])) {
                currentStat = "INET2";
                getStatGraph("INET2", line);
                return 0;
            }
            //00:00:02    dentunusd   file-sz  %file-sz  inode-sz  super-sz %super-sz  dquot-sz %dquot-sz  rtsig-sz %rtsig-sz
            if ("dentunusd".equals(columns[firstdatacolumn])
                    && "file-sz".equals(columns[firstdatacolumn+1])
                    && "%file-sz".equals(columns[firstdatacolumn+2])
                    && "inode-sz".equals(columns[firstdatacolumn+3])
                    && "super-sz".equals(columns[firstdatacolumn+4])
                    && "%super-sz".equals(columns[firstdatacolumn+5])
                    && "dquot-sz".equals(columns[firstdatacolumn+6])
                    && "%dquot-sz".equals(columns[firstdatacolumn+7])
                    && "rtsig-sz".equals(columns[firstdatacolumn+8])
                    && "%rtsig-sz".equals(columns[firstdatacolumn+9])) {
                currentStat = "IGNORE";
                return 1;
            }
            //19:12:11        CPU      %usr     %nice      %sys   %iowait    %steal      %irq     %soft    %guest     %idle
            if ("%usr".equals(columns[firstdatacolumn+1])
                    && "%nice".equals(columns[firstdatacolumn+2])
                    && "%sys".equals(columns[firstdatacolumn+3])
                    && "%iowait".equals(columns[firstdatacolumn+4])
                    && "%steal".equals(columns[firstdatacolumn+5])
                    && "%irq".equals(columns[firstdatacolumn+6])
                    && "%soft".equals(columns[firstdatacolumn+7])
                    && "%guest".equals(columns[firstdatacolumn+8])
                    && "%idle".equals(columns[firstdatacolumn+9])) {
                currentStat = "CPU";
                getStatGraph("CPU", line);
                return 0;
            }
        }
        if (columns.length == 12 ) {
            //* 20:58:24      scall/s badcall/s  packet/s     udp/s     tcp/s     hit/s    miss/s   sread/s  swrite/s saccess/s sgetatt/s
            if ("scall/s".equals(columns[firstdatacolumn])
                    && "badcall/s".equals(columns[firstdatacolumn+1])
                    && "packet/s".equals(columns[firstdatacolumn+2])
                    && "udp/s".equals(columns[firstdatacolumn+3])
                    && "tcp/s".equals(columns[firstdatacolumn+4])
                    && "hit/s".equals(columns[firstdatacolumn+5])
                    && "miss/s".equals(columns[firstdatacolumn+6])
                    && "sread/s".equals(columns[firstdatacolumn+7])
                    && "swrite/s".equals(columns[firstdatacolumn+8])
                    && "saccess/s".equals(columns[firstdatacolumn+9])
                    && "sgetatt/s".equals(columns[firstdatacolumn+10])) {
                currentStat = "NFSM";
                getStatGraph("NFSM", line);
                return 0;
            }
        }

        if (lastStat != null) {
            if (!lastStat.equals(currentStat) && GlobalOptions.isDodebug() ) {
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
        
        currentStatObj = getStatGraph(currentStat, null);

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

    public Object getStatGraph(String s, String h) {
        Object o = GraphList.get(s);

        if (o != null) {
            return o;
        }
        if (h == null) {
            return null;
        }

        if ("CPU".equals(s)) {
            o = new StackedList(mysar, "CPU", h, firstdatacolumn);
        }
        if ("INTR".equals(s)) {
            o = new LineList(mysar, "interrupts", h, firstdatacolumn);
            ((BaseList) o).create_newplot("intr", "intr/s");
        }
        if ("INET1".equals(s)) {
            o = new LineList(mysar, "Interfaces traffic", h, firstdatacolumn);
            ((BaseList) o).create_newplot("pakcet", "rxpck/s txpck/s");
            ((BaseList) o).create_newplot("bytes", "rxbyt/s txbyt/s rxkB/s txkB/s");
            ((BaseList) o).create_newplot("cast", "rxcmp/s txcmp/s rxmcst/s");
        }
        if ("INET2".equals(s)) {
            o = new LineList(mysar, "Interfaces errors", h, firstdatacolumn);
            ((BaseList) o).create_newplot("Rx", "rxerr/s rxdrop/s rxfram/s  rxfifo/s");
            ((BaseList) o).create_newplot("Tx", "txerr/s txdrop/s txcarr/s  txfifo/s");
            ((BaseList) o).create_newplot("Collision", "coll/s");
        }
        if ("DEV".equals(s)) {
            o = new LineList(mysar, "Devices", h, firstdatacolumn);
            ((BaseList) o).create_newplot("tps", "tps");
            ((BaseList) o).create_newplot("bls", "blks/s");
            ((BaseList) o).create_newplot("sector", "rd_sec/s wr_sec/s");
            ((BaseList) o).create_newplot("queue", "avgrq-sz avgqu-sz");
            ((BaseList) o).create_newplot("service", "await svctm");
            ((BaseList) o).create_newplot("sector", "%util");
        }
        if ("PROC".equals(s)) {
            o = new LineGraph(mysar, "Proc", h, firstdatacolumn, mysar.graphtree);
            ((BaseGraph) o).create_newplot("proc", "proc/s");
        }
        if ("CSWCH".equals(s)) {
            o = new LineGraph(mysar, "Cswch", h, firstdatacolumn, mysar.graphtree);
            ((BaseGraph) o).create_newplot("cswch", "cswch/s");
        }
        if ("PAGING".equals(s)) {
            o = new LineGraph(mysar, "Paging", h, firstdatacolumn, mysar.graphtree);
            ((BaseGraph) o).create_newplot("pgp", "pgpgin/s pgpgout/s");
            ((BaseGraph) o).create_newplot("pg", "activepg inadtypg inaclnpg inatarpg");
            ((BaseGraph) o).create_newplot("fault", "fault/s majflt/s");
            ((BaseGraph) o).create_newplot("free", "pgfree/s pgscank/s pgscand/s pgsteal/s");
            ((BaseGraph) o).create_newplot("vmeff", "%vmeff");            
        }
        if ("SWAP".equals(s)) {
            o = new LineGraph(mysar, "Swap", h, firstdatacolumn, mysar.graphtree);
            ((BaseGraph) o).create_newplot("swap", "pswpin/s pswpout/s");
        }
        if ("IO".equals(s)) {
            o = new LineGraph(mysar, "I/O", h, firstdatacolumn, mysar.graphtree);
            ((BaseGraph) o).create_newplot("tps", "tps rtps wtps");
            ((BaseGraph) o).create_newplot("byte", "bread/s bwrtn/s");
        }
        if ("PAGE".equals(s)) {
            o = new LineGraph(mysar, "Page", h, firstdatacolumn, mysar.graphtree);
            ((BaseGraph) o).create_newplot("Page", "frmpg/s shmpg/s bufpg/s campg/s");
        }
        if ("KMEM".equals(s)) {
            o = new LineGraph(mysar, "KMem", h, firstdatacolumn, mysar.graphtree);
            ((BaseGraph) o).create_newplot("KMem", "kbmemfree kbmemused kbmemshrd kbbuffers kbcached kbcommit");
            ((BaseGraph) o).create_newplot("Swp", "kbswpfree kbswpused kbswpcad");
            ((BaseGraph) o).create_newplot("%", "%memused %swpused %commit");
        }
        
        if ( "KSWAP".equals(s) ) {
            o = new LineGraph(mysar, "KSwap", h, firstdatacolumn, mysar.graphtree);
            ((BaseGraph) o).create_newplot("Swap", "kbswpfree kbswpused kbswpcad");
            ((BaseGraph) o).create_newplot("%", "%swpused %swpcad");
        }
        if ("LOAD".equals(s)) {
            o = new LineGraph(mysar, "Load", h, firstdatacolumn, mysar.graphtree);
            ((BaseGraph) o).create_newplot("RunQ", "runq-sz");
            ((BaseGraph) o).create_newplot("Plist", "plist-sz");
            ((BaseGraph) o).create_newplot("LA", "ldavg-1 ldavg-5 ldavg-15");
        }
        if ("SOCKET".equals(s)) {
            o = new LineGraph(mysar, "Socket", h, firstdatacolumn, mysar.graphtree);
            ((BaseGraph) o).create_newplot("Socket", "totsck tcpsck udpsck rawsck");
            ((BaseGraph) o).create_newplot("Frag", "ip-frag tcp-tw");
        }
        if ("NFSC".equals(s)) {
            o = new LineGraph(mysar, "NFS Client", h, firstdatacolumn, mysar.graphtree);
            ((BaseGraph) o).create_newplot("call", "call/s retrans/s");
            ((BaseGraph) o).create_newplot("I/O", "read/s write/s");
            ((BaseGraph) o).create_newplot("access", "access/s getatt/s");
        }
        if ("NFSM".equals(s)) {
            o = new LineGraph(mysar, "NFS Server", h, firstdatacolumn, mysar.graphtree);
            ((BaseGraph) o).create_newplot("call", "call/s badcalls/s");
            ((BaseGraph) o).create_newplot("I/O", "packet/s udp/s tcp/s");
            ((BaseGraph) o).create_newplot("cache", "hit/s miss/s");
            ((BaseGraph) o).create_newplot("traffic", "sread/s swrite/s saccess/s sgetatt/s");
        }        
        if ("PROC2".equals(s)) {
            o = new LineGraph(mysar, "Processus", h, firstdatacolumn, mysar.graphtree);
            ((BaseGraph) o).create_newplot("Proc", "proc/s");
            ((BaseGraph) o).create_newplot("context", "cswch/s");
        }
        if (o != null) {
            GraphList.put(s, o);
            return o;
        }
        return null;
    }
    
    
}
