package org.regibot.models.telegram;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageOut {
    @JsonAlias("chat_id")
    private String chatId;

    private String text;

    @JsonAlias("reply_to_message_id")
    private Integer replyToMessageId;

    @JsonAlias("reply_markup")
    private ReplyKeyboardMarkup replyMarkup;

    public String getChatId() {
        return chatId;
    }

    @JsonProperty("chat_id")
    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @JsonProperty("reply_to_message_id")
    public Integer getReplyToMessageId() {
        return replyToMessageId;
    }

    public void setReplyToMessageId(Integer replyToMessageId) {
        this.replyToMessageId = replyToMessageId;
    }

    @JsonProperty("reply_markup")
    public ReplyKeyboardMarkup getReplyMarkup() {
        return replyMarkup;
    }

    public void setReplyMarkup(ReplyKeyboardMarkup replyMarkup) {
        this.replyMarkup = replyMarkup;
    }
}
