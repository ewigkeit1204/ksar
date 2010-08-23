/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar.XML;

import java.util.ArrayList;

/**
 *
 * @author alex
 */
public class CnxHistory {

    public CnxHistory(String link) {
        this.link=link;
        commandList = new ArrayList<String>();
        String[] s = link.split("@", 2);
        if (s.length != 2) {
            return;
        }
        this.setUsername(s[0]);
        String[] t = s[1].split(":");
        if (t.length == 2) {
            this.setHostname(t[0]);
            this.setPort(t[1]);
        } else {
            this.setHostname(s[1]);
            this.setPort("22");
        }
    }

    public void addCommand(String s) {
        commandList.add(s);
    }

    public ArrayList<String> getCommandList() {
        return commandList;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getLink() {
        return link;
    }
    
    public boolean isValid() {
        if (username == null) {
            return false;
        }
        if (hostname == null) {
            return false;
        }
        if (commandList.isEmpty()) {
            return false;
        }
        return true;
    }

    public void dump() {
        System.out.println(username + "@" + hostname + ":" + commandList);
    }

    public String save() {
        StringBuilder tmp = new StringBuilder();
        tmp.append("\t\t<cnx link=\"" + username + "@" + hostname + ":" + port + "\">\n");
        for (int i = 0; i < commandList.size(); i++) {
            tmp.append("\t\t\t<command>").append(commandList.get(i)).append("</command>\n");
        }
        tmp.append("\t\t</cnx>\n");
        return tmp.toString();
    }
    private String username = null;
    private String hostname = null;
    private ArrayList<String> commandList = null;
    private String port = null;
    private String link = null;
}
