package hProjekt.controller;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

import org.tudalgo.algoutils.student.annotation.StudentImplementationRequired;

import hProjekt.Config;

/**
 * Controller for managing the leaderboard functionality.
 * This class handles reading from, writing to, and initializing the leaderboard
 * CSV file.
 */
public class LeaderboardController {
    /**
     * Ensures the leaderboard CSV file exists.
     * If the file does not exist, it creates the file along with its parent
     * directories
     * and writes a header row to the file.
     */
    public static void initializeCsv() {
        try {
            if (!Files.exists(Config.CSV_PATH)) {
                Files.createDirectories(Config.CSV_PATH.getParent());
                BufferedWriter writer = Files.newBufferedWriter(Config.CSV_PATH);
                writer.write("PlayerName,AI,Timestamp,Score\n"); // CSV Header
                writer.close();
            }
        } catch (IOException e) {
            System.out.println("Couldn't create the leaderboard csv file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Appends a new player's data to the leaderboard CSV file.
     * Ensures the file is initialized before writing. Each entry includes
     * the player's name, AI status, a timestamp, and the player's score.
     *
     * @param playerName The name of the player.
     * @param score      The score achieved by the player.
     * @param ai         Indicates whether the player is an AI (true) or a human
     *                   (false).
     */
    @StudentImplementationRequired("P3.1")
    public static void savePlayerData(String playerName, int score, boolean ai) {
        try {
            // Initialize the CSV file
            initializeCsv();

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            BufferedWriter writer = Files.newBufferedWriter(Config.CSV_PATH);
            writer.write(playerName + "," + ai + "," + timestamp + "," + score); // New line
            writer.close();

        } catch (IOException e) {
            System.out.println("Couldn't write to the leaderboard csv file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Reads the leaderboard data from the CSV file and loads it into a list of
     * LeaderboardEntry objects.
     *
     * @return A list of LeaderboardEntry objects containing player data from the
     *         CSV file.
     */
    @StudentImplementationRequired("P3.2")
    public static List<LeaderboardEntry> loadLeaderboardData() {
        try {
            // Return the list of LeaderboardEntry objects
            return Files.newBufferedReader(Config.CSV_PATH)
            .lines()
            .skip(1) // Skip header
            .map(line -> line.split(","))// Split each line by ","
            .map(lineArray -> new LeaderboardEntry(lineArray[0], Boolean.parseBoolean(lineArray[1]),lineArray[2],
                Integer.parseInt(lineArray[3]))) // Create a new LeaderboardEntry object with the formatted data
            .collect(Collectors.toList()); // Return a LeaderboardEntry objects list

        } catch (IOException e) {
            System.out.println("Couldn't read from the leaderboard csv file: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
