package org.regibot.action.step;

import org.regibot.action.UserContext;
import org.regibot.models.telegram.KeyboardButton;
import org.regibot.storage.DAO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DoctorStep extends Step{


    @Override
    public Step execute(UserContext context, DAO dao) throws Throwable {

        try {
            String message = context.messageStack.peek();
            Matcher matcher = Pattern.compile("^[0-9]+").matcher(message);
            if (matcher.find()) {
                context.journal.doctorId = Integer.getInteger(message.substring(0, matcher.end()));
            } else {
                context.messageStack.removeFirst();
                return writeQuestion(context, dao);
            }

            if (getNext() != null) return getNext().writeQuestion(context, dao);

            return this;
        } catch (Throwable throwable) {
            context.messageStack.removeFirst();

            return getPrev().writeQuestion(context, dao);
        }
    }

    @Override
    protected Step writeQuestion(UserContext context, DAO dao) throws Throwable {
        var doctors = dao.getFreeDoctors();
        if (doctors.isEmpty()) {
            context.messageOut.append("\nНет свободных специалистов.");
            return getPrev().writeQuestion(context, dao);
        } else {
            context.messageOut.append("\nВыберите специалиста, к которому Вы хотите пойти.");
            context.keyboard = doctors.entrySet().stream()
                    .map((Map.Entry<Integer,String> entry)->{
                        var kb = new KeyboardButton();
                        kb.setText(entry.getKey().toString() + ". " + entry.getValue());
                        return Arrays.asList(kb);})
                    .collect(Collectors.toList());
            return this;
        }
    }
}
