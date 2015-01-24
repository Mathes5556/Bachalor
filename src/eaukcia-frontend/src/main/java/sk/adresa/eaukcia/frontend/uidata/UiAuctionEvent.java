/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.adresa.eaukcia.frontend.uidata;

import sk.adresa.eaukcia.core.data.AuctionEvent;
import sk.adresa.eaukcia.frontend.resources.ResourceReader;

/**
 *
 * @author juraj
 */
public class UiAuctionEvent extends AuctionEvent {
    
    private String uiValue;
    private String uiActionDescription;
    
    public UiAuctionEvent() {}
    public UiAuctionEvent(AuctionEvent event) {
        super(event);
        uiActionDescription = ResourceReader.getTranslation("action_"+event.getAction());
    }
    
    public String getUiStyle(){
        String style = "";
        if (getTime().getTime() + 60000 > System.currentTimeMillis())
            style="background-color:#FF2200;";
        return style;
    }
     
    public void setUiValue(String uiValue) {
        this.uiValue = uiValue;
    }

    public String getUiValue() {
        return uiValue;
    }

    public String getUiActionDescription() {
        return uiActionDescription;
    }

    public void setUiActionDescription(String uiActionDescription) {
        this.uiActionDescription = uiActionDescription;
    }
    
    
}
