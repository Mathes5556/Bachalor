/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.adresa.eaukcia.core.data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author mathes
 */
public class Offer {
    
    int id;
    
    User user;
    
    Date date;

    ArrayList<Event> events = new ArrayList<Event>();

    public Offer(int id, User user, Date date) {
        this.user = user;
        this.id = id;
        this.date = date;
    }
    
    public void addEvent(Event event){
        events.add(event);
    }
    
    public ArrayList<Event> getEvents() {
        return events;
    }

    public void setEvents(ArrayList<Event> events) {
        this.events = events;
    }
    
    
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    
 //   public BigDecimal getSumOfOffer(){
//        BigDecimal result = BigDecimal.ZERO;
//        int badValues = 0;
//        for(Event event : this.events){
//            if(event.getOfferedValue() != null){
//                result = result.add(event.getOfferedValue());
//            }
//            else{
//                badValues ++;
//            }
//        }
  //  --    System.out.print("bolo null v offeroch " + Integer.toString(badValues) + " z " + Integer.toString(this.getEvents().size()));
   //     return result;
   // }
}
