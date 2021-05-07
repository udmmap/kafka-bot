package org.regibot.storage;

import java.sql.*;
import java.util.ArrayList;
import java.sql.Date;
import java.sql.Time;

import java.util.HashMap;
import java.util.List;

import org.regibot.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

public class DAO {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private final static String SQL_DOCTOR = "select d.id, d.name from doctor d\n" +
            "where\n" +
            "    d.id in (\n" +
            "        select s.id from schedule s\n" +
            "        where\n" +
            "            s.id not in (select j.schedule_id from journal j)\n" +
            "            and s.time > now()\n" +
            "    )";
    private final static String SQL_DATES = "select distinct s.time::date as date\n" +
            "from schedule s\n" +
            "where\n" +
            "    s.doctor_id = ?\n" +
            "    and s.id not in (select j.schedule_id from journal j)\n" +
            "    and s.time > now()\n" +
            "order by date";

    private final static String SQL_TIMES = "select s.id, s.time\n" +
            "from schedule s\n" +
            "where\n" +
            "  s.doctor_id = ?\n" +
            "  and s.time::date = ?\n" +
            "  and s.id not in (select j.schedule_id from journal j)\n" +
            "  and s.time > now()\n" +
            "order by time";

    private final static String SQL_JOURNAL_INSERT = "insert into journal (user_id, schedule_id) values (?, ?)";

    private final DataSource dataSource;

    public DAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public HashMap<Integer,String> getFreeDoctors() throws SQLException {
        HashMap<Integer,String> doctors = new HashMap<>();
        try (Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SQL_DOCTOR);
            ResultSet resultSet = preparedStatement.executeQuery();
        ){
            while (resultSet.next()){
                doctors.put(
                        resultSet.getInt("id")
                        ,resultSet.getString("name")
                );
            }
        }

        return doctors;
    }

    public List<Date> getFreeDates(Integer doctorId) throws SQLException {
        List<Date> dates = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SQL_DATES);
        ) {
            preparedStatement.setInt(1, doctorId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    dates.add(resultSet.getDate("date"));
                }
            }
        }
        return dates;
    }

    public HashMap<Integer, Timestamp> getFreeTimes(Integer doctorId, Date date) throws SQLException {
        var times = new HashMap<Integer, Timestamp>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_TIMES);
        ) {
            preparedStatement.setInt(1, doctorId);
            preparedStatement.setDate(2, date);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    times.put(resultSet.getInt("id"), resultSet.getTimestamp("time"));
                }
            }
        }
        return times;
    }

    public Integer setJournal(Long userId, Integer scheduleId) throws SQLException {
        Integer res = 0;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_JOURNAL_INSERT, Statement.RETURN_GENERATED_KEYS);
        ) {
            preparedStatement.setLong(1, userId);
            preparedStatement.setInt(2, scheduleId);

            preparedStatement.executeUpdate();

            ResultSet tableKeys = preparedStatement.getGeneratedKeys();
            tableKeys.next();
            res = tableKeys.getInt(1);

        }
        return res;
    }

}
