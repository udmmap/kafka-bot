package org.regibot.action.step;

import org.regibot.action.UserContext;
import org.regibot.models.telegram.KeyboardButton;
import org.regibot.storage.DAO;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TimeStep extends Step{
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @Override
    public Step execute(UserContext context, DAO dao) throws Throwable {
        try {
            Matcher matcher = Pattern.compile("^\\[[0-9]+\\]").matcher(context.messageIn);
            Integer scheduleId;
            if (matcher.find()) {
                scheduleId = Integer.parseInt(context.messageIn.substring(1, matcher.end()-1));
            } else {
                return writeQuestion(context, dao);
            }

            Integer recId = dao.setJournal(context.getUserId(), scheduleId);

            context.messageOut.append(String.format("Вы успешно записались. Код записи [%d]", recId));
            return null;

        } catch (Throwable throwable) {
            logger.error("User "+context.getUserId().toString(), throwable);

            return getPrev().writeQuestion(context, dao);
        }
    }

    @Override
    protected Step writeQuestion(UserContext context, DAO dao) throws Throwable {
        var times = dao.getFreeTimes(context.journal.doctorId, context.journal.date);
        if (times.isEmpty()) {
            context.messageOut.append("\nНет временных окон для записи.");
            return getPrev().writeQuestion(context, dao);
        } else {
            context.messageOut.append("\nВыберите желаемое временное окно для посещения.");
            context.keyboard = times.entrySet().stream()
                    .map((Map.Entry<Integer, Timestamp> entry)->{
                        var kb = new KeyboardButton();
                        kb.setText(
                                "["
                                + entry.getKey().toString()
                                + "] "
                                + entry.getValue().toLocalDateTime().format(dateTimeFormatter)
                        );
                        return Arrays.asList(kb);})
                    .collect(Collectors.toList());
            return this;
        }
    }
}
