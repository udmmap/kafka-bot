package org.regibot.action.step;

import org.regibot.action.UserContext;
import org.regibot.storage.DAO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;

public abstract class Step {
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
