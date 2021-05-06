package org.regibot.action.step;

import org.regibot.action.Action;
import org.regibot.action.UserContext;
import org.regibot.storage.DAO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Step {
    protected static final Logger logger = LoggerFactory.getLogger(Action.class);

    private Step next = null;
    private Step prev = null;

    public Step getNext(){
        return next;
    };

    public Step setNext(Step next) {
        this.next = next;
        next.setPrev(this);
        return this;
    }

    public Step getPrev() { return prev; };

    protected void setPrev(Step prev) {this.prev = prev;}

    abstract public Step execute(UserContext context, DAO dao) throws Throwable;

    abstract protected Step writeQuestion(UserContext context, DAO dao) throws Throwable;
}
