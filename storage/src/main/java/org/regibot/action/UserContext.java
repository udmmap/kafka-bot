package org.regibot.action;

import org.regibot.action.step.Step;
import org.regibot.models.db.Journal;
import org.regibot.models.telegram.KeyboardButton;

import java.util.LinkedList;
import java.util.List;

public class UserContext {
    public Integer status = 0;
    public final StringBuilder messageOut = new StringBuilder();
    public List<List<KeyboardButton>> keyboard = null;
    public final LinkedList<String> messageStack = new LinkedList<String>();

    public final Journal journal = new Journal();

    private Step currentStep;

    public UserContext(Step currentStep){
        this.currentStep = currentStep;
    };

    public Step getCurrentStep() {
        return currentStep;
    }

    public void setCurrentStep(Step currentStep) {
        this.currentStep = currentStep;
    }
}
