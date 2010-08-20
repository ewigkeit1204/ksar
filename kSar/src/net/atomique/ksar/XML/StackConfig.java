/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.atomique.ksar.XML;

/**
 *
 * @author Max
 */
public class StackConfig {

    public StackConfig(String s) {
        Title=s;
    }

    public String[] getHeader() {
        return Header;
    }

    public String getTitle() {
        return Title;
    }

    public void setHeaderStr(String s) {
        this.Header=s.split("\\s+");
        HeaderStr=s;
    }

    public String getHeaderStr() {
        return HeaderStr;
    }
    
    public void print() {
        System.out.println("Title " + Title);
        System.out.println("HeaderStr " + HeaderStr);
        System.out.println("Header " + Header);

    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setSize(String s) {
        Integer tmp = new Integer(s);
        if (tmp == null) {
            return;
        }
        this.size = tmp.intValue();
    }
    private int size = 1;
    private String Title = null;
    private String [] Header = null;
    private String HeaderStr = null;
}
