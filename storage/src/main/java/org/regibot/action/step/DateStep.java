package org.regibot.action.step;

import org.regibot.action.UserContext;
import org.regibot.storage.DAO;

public class DateStep extends Step{
    @Override
    public Step execute(UserContext context, DAO dao) throws Throwable {
        return null;
    }

    @Override
    protected Step writeQuestion(UserContext context, DAO dao) throws Throwable {


        return this;
    }
}
