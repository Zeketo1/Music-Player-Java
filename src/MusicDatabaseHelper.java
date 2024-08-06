import java.sql.*;

public class MusicDatabaseHelper {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/nextmusic";
    private static final String DB_USER = "";
    private static final String DB_PASSWORD = "";

    // Database connection object
    private Connection connection;

    public MusicDatabaseHelper() {

        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to connect to the database");
        }


    }

    // Method to insert a played music path into the database
    public void insertMusicPath(String musicPath) {
        String query = "INSERT INTO played_music_paths (path) VALUES (?)";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, musicPath);
            statement.executeUpdate();
            System.out.println("Inserted music path into the database: " + musicPath);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to insert music path into the database");
        }
    }

    // Method to retrieve the history of played music paths from the database
    public void retrievePlayedMusicPaths() {
        String query = "SELECT * FROM played_music_paths";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            System.out.println("History of played music paths:");
            while (resultSet.next()) {
                String path = resultSet.getString("path");
                System.out.println(path);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to retrieve played music paths from the database");
        }
    }

    public static void main(String[] args) {
        MusicDatabaseHelper dbHelper = new MusicDatabaseHelper();

        dbHelper.insertMusicPath("path/to/played/music1.mp3");
        dbHelper.insertMusicPath("path/to/played/music2.mp3");

        dbHelper.retrievePlayedMusicPaths();
    }
}
