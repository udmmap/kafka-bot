package org.regibot.models.telegram;

import java.util.List;

public class ReplyKeyboardMarkup {
    private List<List<KeyboardButton>> keyboard;

    public List<List<KeyboardButton>> getKeyboard() {
        return keyboard;
    }

    public void setKeyboard(List<List<KeyboardButton>> keyboard) {
        this.keyboard = keyboard;
    }
}
