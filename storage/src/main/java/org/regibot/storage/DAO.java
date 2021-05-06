package org.regibot.storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.regibot.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private final Connection connection;

    public DAO(Connection connection) {
        this.connection = connection;
    }

    public HashMap<Integer,String> getFreeDoctors() throws SQLException {
        HashMap<Integer,String> doctors = new HashMap<>();
        try (
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
}
