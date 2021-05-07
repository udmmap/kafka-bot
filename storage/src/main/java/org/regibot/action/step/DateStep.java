package org.regibot.action.step;

import org.regibot.action.UserContext;
import org.regibot.models.telegram.KeyboardButton;
import org.regibot.storage.DAO;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.stream.Collectors;

public class DateStep extends Step{
    private final DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    @Override
    public Step execute(UserContext context, DAO dao) throws Throwable {
        try {
            context.journal.date = new Date(dateFormat.parse(context.messageIn).getTime());

            if (getNext() != null) return getNext().writeQuestion(context, dao);

            return this;
        } catch (Throwable throwable) {
            logger.error("User "+context.getUserId().toString(), throwable);

            return getPrev().writeQuestion(context, dao);
        }
    }

    @Override
    protected Step writeQuestion(UserContext context, DAO dao) throws Throwable {
        var dates = dao.getFreeDates(context.journal.doctorId);
        if (dates.isEmpty()) {
            context.messageOut.append("\nНет дат для записи.");
            return getPrev().writeQuestion(context, dao);
        } else {
            context.messageOut.append("\nВыберите желаемую дату посещения.");
            context.keyboard = dates.stream()
                    .map(dateFormat::format)
                    .map(s->{
                        var kb = new KeyboardButton();
                        kb.setText(s);
                        return Arrays.asList(kb);
                    })
            .collect(Collectors.toList());
            return this;
        }
    }
}
