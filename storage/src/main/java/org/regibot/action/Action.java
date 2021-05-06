package org.regibot.action;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.regibot.action.step.Step;
import org.regibot.models.telegram.MessageOut;
import org.regibot.models.telegram.ReplyKeyboardMarkup;
import org.regibot.models.telegram.Update;
import org.regibot.storage.DAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Action {
    private static final Logger logger = LoggerFactory.getLogger(Action.class);

    private final Map<Long, UserContext> userContexts = new ConcurrentHashMap<>();
    private final Step script;
    private final DataSource dataSource;

    public Action(Step script, DataSource dataSource){
        this.script = script;
        this.dataSource = dataSource;
    }

    public String perform(String telegramUpdate){

        try {
            Long userId;
            UserContext context;
            var objectMapper = new ObjectMapper();

            Update objUpd = objectMapper.readValue(telegramUpdate, Update.class);
            userId = objUpd.getMessage().getFrom().getId();
            if (!userContexts.containsKey(userId)) {
              userContexts.putIfAbsent(userId,new UserContext(userId, script));
            }
            context = userContexts.get(userId);

            Step currentStep = context.getCurrentStep();
            if (currentStep==null){
                currentStep = script;
                context.messageStack.clear();
            }

            context.messageStack.push(objUpd.getMessage().getText());
            context.messageOut.setLength(0);
            context.keyboard = null;

            DAO dao = new DAO(dataSource);

            context.setCurrentStep(currentStep.execute(context, dao));

            var messageOut = new MessageOut();
            messageOut.setText(context.messageOut.toString());
            messageOut.setReplyToMessageId(objUpd.getMessage().getId());
            messageOut.setChatId(objUpd.getMessage().getChat().getId().toString());

            if (context.keyboard != null) {
                var replyMarkup = new ReplyKeyboardMarkup();
                replyMarkup.setKeyboard(context.keyboard);
                messageOut.setReplyMarkup(replyMarkup);
            }

            return (new ObjectMapper()).writeValueAsString(messageOut);

        } catch (JsonProcessingException e) {
            logger.error("JSON exception",e);
        } catch (SQLException e) {
            logger.error("Datasource exception",e);
        } catch (Throwable throwable) {
            logger.error("Exception",throwable);
        }

        return "Возникли непредвиденные трудности. Пожалуйста, обратитесь к разработчикам.";
    }
}
