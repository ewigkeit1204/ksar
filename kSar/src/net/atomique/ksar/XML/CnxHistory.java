/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.atomique.ksar.XML;

/**
 *
 * @author alex
 */
public class CnxHistory {

    public CnxHistory() {
        
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
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

    public boolean isValid() {
        if ( username == null) {
            return false;
        }
        if ( hostname == null) {
            return false;
        }
        if ( command == null ) {
            return false;
        }
        return true;
    }

    public void dump() {
        System.out.println( username + "@" + hostname +":" + command);
    }
    
    private String username = null;
    private String hostname = null;
    private String command = null;
}
