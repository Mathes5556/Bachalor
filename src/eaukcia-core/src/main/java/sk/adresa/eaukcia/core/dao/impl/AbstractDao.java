package sk.adresa.eaukcia.core.dao.impl;

import org.apache.ibatis.session.SqlSession;
import sk.adresa.eaukcia.core.exception.EaukciaObjectNotFoundException;
import sk.adresa.eaukcia.core.exception.EaukciaRuntimeDataAccessException;
import sk.adresa.eaukcia.core.util.Assert;

public class AbstractDao {
    protected SqlSession sqlSession;

    public void setSqlSession(SqlSession sqlSession) {
        Assert.isNotNull(sqlSession, "sqlSession");
        this.sqlSession = sqlSession;
    }
    
    protected void checkFound(int required, int found) throws EaukciaObjectNotFoundException{
        if(required == found) {
            return;
        } else if(required > found) {
            throw new EaukciaObjectNotFoundException(String.format("Required: %d Found: %d", required, found));
        } else {
            throw new EaukciaRuntimeDataAccessException(String.format("Required: %d Found: %d", required, found), EaukciaRuntimeDataAccessException.Error.INCONSISTENT_STATE);
        }
    }
    
    protected void checkFoundMin(int required, int found) throws EaukciaObjectNotFoundException{
        if(required <= found) {
            return;
        } else  {
            throw new EaukciaObjectNotFoundException(String.format("Required: %d Found: %d", required, found));
        } 
    }
}
