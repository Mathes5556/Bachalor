/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.adresa.eaukcia.core.query;

import java.util.List;
import sk.adresa.eaukcia.core.util.Util;

/**
 *
 * @author juraj
 */
public class PaginatedList<T> {
    private Paging paging;
    private List<T> data;


    public PaginatedList(List<T> data, Paging paging){
       this.data = data;
       this.paging = paging;
    }


    public Paging getPaging() {
        return paging;
    }

    public List<T> getData() {
        return data;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        
        sb.append("\nObject PaginatedList: {");
        sb.append("\n  paging: ").append(Util.objectToString(paging));
        sb.append("\n  data: ").append(Util.objectToString(data));
        sb.append("\n}");

        return sb.toString();

    }
}
