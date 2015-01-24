package sk.adresa.eaukcia.core.dao.impl;

import java.util.List;


import sk.adresa.eaukcia.core.dao.ConstantsDao;
import sk.adresa.eaukcia.core.data.Constant;

public class ConstantsDaoImpl extends AbstractDao implements ConstantsDao {

    private static final String DEFAULT_PREFIX = "sk.adresa.eaukcia.core.dao.impl.ConstantsMapper.";

    public ConstantsDaoImpl() {
    }


    @Override
    public Constant<String>[] getAuctionActions() {


        List list = sqlSession.selectList(DEFAULT_PREFIX + "getAuctionActions");
        return (Constant<String>[]) list.toArray(new Constant[0]);


    }

    @Override
    public Constant<String> getAuctionAction(String id) {


        Object object = sqlSession.selectOne(DEFAULT_PREFIX + "getAuctionAction", id);
        return object instanceof Constant ? ((Constant<String>) object) : null;


    }
}
