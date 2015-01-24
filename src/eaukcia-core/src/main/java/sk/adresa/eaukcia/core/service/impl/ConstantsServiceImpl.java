/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.adresa.eaukcia.core.service.impl;

import sk.adresa.eaukcia.core.dao.ConstantsDao;
import sk.adresa.eaukcia.core.data.Constant;
import sk.adresa.eaukcia.core.service.ConstantsService;
import sk.adresa.eaukcia.core.util.Assert;

/**
 *
 * @author juraj
 */
public class ConstantsServiceImpl implements ConstantsService{
    
    private ConstantsDao constantsDao;

    public ConstantsServiceImpl(ConstantsDao constantsDao){
        Assert.isNotNull(constantsDao, "constantsDao");
        this.constantsDao = constantsDao;
    } 

    @Override
    public Constant<String>[] getAuctionActions() {return constantsDao.getAuctionActions();}
    @Override
    public Constant<String> getAuctionAction(String id) {return constantsDao.getAuctionAction(id);}

}
