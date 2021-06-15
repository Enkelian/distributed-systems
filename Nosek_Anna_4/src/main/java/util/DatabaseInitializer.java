package util;

import java.sql.*;

public class DatabaseInitializer {

    public static final String TABLE_NAME = "ERRORS";
    public static final String CONNECTION_URL = "jdbc:sqlite:test.db";

    private static void populateDatabase(Connection connection) throws SQLException {
        PreparedStatement insertStatement;

        String insertStatementString = "INSERT INTO " + TABLE_NAME + " (SATELLITE_ID,ERROR_COUNT) " +
                "VALUES (?, 0 )" +
                "ON CONFLICT(SATELLITE_ID) DO UPDATE SET ERROR_COUNT = 0;";

        insertStatement = connection.prepareStatement(insertStatementString);
        for (int i = 100; i < 200; i++) {
            insertStatement.setString(1, Integer.toString(i));
            insertStatement.executeUpdate();

        }
        insertStatement.close();
        connection.commit();
    }

    private static void createTable(Connection connection) throws SQLException {
        Statement createTableStatement;

        createTableStatement = connection.createStatement();
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
                " (SATELLITE_ID INT PRIMARY KEY     NOT NULL," +
                " ERROR_COUNT            INT     NOT NULL)";

        createTableStatement.executeUpdate(sql);
        createTableStatement.close();
        connection.commit();
    }

    public static void initializeDatabase(){

        Connection connection;
        try {
            Class.forName("AppRunner");

            connection = DriverManager.getConnection(CONNECTION_URL);
            connection.setAutoCommit(false);

            createTable(connection);
            populateDatabase(connection);

            connection.close();

        } catch ( Exception e ) {
            e.printStackTrace();
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }

    }
}
