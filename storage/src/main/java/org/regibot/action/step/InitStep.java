package org.regibot.action.step;

import org.regibot.action.UserContext;
import org.regibot.storage.DAO;

public class InitStep extends Step{
    @Override
    public Step execute(UserContext context, DAO dao) throws Throwable {
        context.messageOut.append("Добрый день!");
        if (getNext() != null) return getNext().writeQuestion(context, dao);
        return this;
    }

    @Override
    protected Step writeQuestion(UserContext context, DAO dao) {
        context.messageOut.append("\nЗапись невозможна");
        return this;
    }
}
