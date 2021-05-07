package org.regibot.models.telegram;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ReplyKeyboardMarkup {
    private List<List<KeyboardButton>> keyboard;

    private Boolean oneTimeKeyboard = true;

    public List<List<KeyboardButton>> getKeyboard() {
        return keyboard;
    }

    public void setKeyboard(List<List<KeyboardButton>> keyboard) {
        this.keyboard = keyboard;
    }

    @JsonProperty("one_time_keyboard")
    public Boolean getOneTimeKeyboard() {
        return oneTimeKeyboard;
    }

    public void setOneTimeKeyboard(Boolean oneTimeKeyboard) {
        this.oneTimeKeyboard = oneTimeKeyboard;
    }

}
