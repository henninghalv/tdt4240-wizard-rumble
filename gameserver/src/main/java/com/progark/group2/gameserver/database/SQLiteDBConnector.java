package com.progark.group2.gameserver.database;


import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

public class SQLiteDBConnector {

    private static SQLiteDBConnector instance;
    private Connection conn;
    private static final String DB_URL = "jdbc:sqlite:./gameserver/src/main/java/com/progark/group2/gameserver/database/playerdatabase.db";

    public SQLiteDBConnector() throws ClassNotFoundException {
        conn = null;
        // Register the library on classpath
        Class.forName("org.sqlite.JDBC");
    }

    /**
     * There should only be one connection object, which only the master server (which is also
     * a singleton) uses.
     * @return The instance of the DB connector
     * @throws ClassNotFoundException
     */
    public static SQLiteDBConnector getInstance() throws ClassNotFoundException {
        if(instance == null){
            instance = new SQLiteDBConnector();
        }
        return instance;
    }

    /**
     * Run this method to connect to the database.
     */
    public void connect() {
        try {
            // Create a connection to the database
            conn = DriverManager.getConnection(DB_URL);
            System.out.println("Connection to SQLite has been established.");
            // Checks if the players table is missing, aka. the DB is empty
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet resultSet = metaData.getTables(null, null, "players", null);
            // If the DB is empty, run initial setup
            if (!resultSet.isBeforeFirst() ) {
                System.out.println("No data...");
                System.out.println("Setting up DB...");
                setup();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            // TODO: Send ServerErrorResponse that something went wrong.
        }
    }

    /**
     * Creates the "players" table in the DB if it does not exist to avoid SQL Exceptions.
     * @throws SQLException
     */
    private void setup() throws SQLException {
        String query = "CREATE TABLE players (id int, username varchar(255))";
        Statement stmt = conn.createStatement();
        ResultSet resultSet = stmt.executeQuery(query);
        resultSet.close();
        System.out.println("Done!");
    }

    // TODO: Update when players get more fields than id and username
    /**
     * Queries the DB to get all registered players and their metadata.
     * @return Returns an ArrayList of all the players in the DB
     * @throws SQLException
     */
    public ArrayList<HashMap<Integer, String>> getAllPlayers() throws SQLException {
        String query = "SELECT * FROM players";

        Statement stmt = conn.createStatement();
        ResultSet resultSet = stmt.executeQuery(query);

        ArrayList<HashMap<Integer, String>> players = new ArrayList<HashMap<Integer, String>>();

        while(resultSet.next()){
            HashMap<Integer, String> player = new HashMap<Integer, String>();
            player.put(resultSet.getInt("id"), resultSet.getString("username"));
            players.add(player);
        }

        return players;
    }

    /**
     * Queries the database with the unique id and gets the player that corresponds to the id.
     * TODO: This should be extended to return a custom Player object instead of a simple hashmap.
     * @param id
     * @return A Hashmap with id as the key and username as the value.
     * @throws SQLException
     */
    public HashMap<Integer, String> getPlayer(int id) throws SQLException {
        String query = String.format("SELECT * FROM players WHERE id=%d", id);

        Statement stmt = conn.createStatement();
        ResultSet resultSet = stmt.executeQuery(query);
        HashMap<Integer, String> player = new HashMap<Integer, String>();
        if(resultSet.next()){
            player.put(resultSet.getInt("id"), resultSet.getString("username"));
        }

        return player;
    }

    /**
     * Queries the database for the highest id in the "players" table.
     * @return The highest id in the DB as an int > 0, or 0 if the table is empty
     * @throws SQLException
     */
    public int getHighestId() throws SQLException {
        String query = String.format("SELECT * FROM players ORDER BY ID DESC LIMIT 1");

        Statement stmt = conn.createStatement();
        ResultSet resultSet = stmt.executeQuery(query);
        // If there are no players in the DB, return highest id = 0
        if(resultSet.next()){
            return resultSet.getInt("id");
        }
        return 0;
    }

    /**
     * Takes an id that should be the highest id in the DB + 1. Use the method getHighestId to get
     * the highest id in the DB. Inserts a new row with this id and the username.
     * @param id
     * @param username
     * @throws SQLException
     */
    public void createNewPlayer(int id, String username) throws SQLException {
        String query = "INSERT INTO players(id,username) VALUES(?,?)";

        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setInt(1, id);
        pstmt.setString(2, username);
        pstmt.executeUpdate();

    }






}
