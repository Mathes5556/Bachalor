package sk.adresa.eaukcia.frontend.service;


import java.text.SimpleDateFormat;
import java.util.Calendar;

import java.util.Date;
import java.util.GregorianCalendar;


public class FrontendManager {
    
    private int clientWidth;
    private int clientHeight;
    private String mainPanelWidth="100%";
    
    public static String formatDate(Date d, String dateFormat){
        Calendar cal = new GregorianCalendar();
        cal.setTime(d);
        SimpleDateFormat dateFormatter = new SimpleDateFormat(dateFormat);
        return dateFormatter.format(cal.getTime());
    }
    
     public void setClientWidth(int w){
        clientWidth = w;
    }
                         
    public int getClientWidth(){
        return clientWidth;    
    }
                         
    public void setClientHeight(int h){
        clientHeight = h;    
    }
                         
    public int getClientHeight(){
        return clientHeight;
    }
    
    
    public String getMainPanelWidth(){
        return mainPanelWidth;
    }


}
