import java.io.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class HighScoreManager {
    private static HighScoreManager instance;
    private Map<String, ArrayList<Score>> highScoresByDifficulty;
    private final String SCORE_FILE = "highscores.txt";

    private HighScoreManager() {
        highScoresByDifficulty = new HashMap<>();
        highScoresByDifficulty.put("Easy", new ArrayList<>());
        highScoresByDifficulty.put("Medium", new ArrayList<>());
        highScoresByDifficulty.put("Hard", new ArrayList<>());
        highScoresByDifficulty.put("Insane", new ArrayList<>());
        loadScores();
    }

    public static HighScoreManager getInstance() {
        if (instance == null) {
            instance = new HighScoreManager();
        }
        return instance;
    }

    public boolean isHighScore(int score, String difficulty) {
        ArrayList<Score> scores = highScoresByDifficulty.get(difficulty);
        if (scores == null) return false;

        // If there are no scores, it's a high score
        if (scores.size() == 0) return true;


        // Get the lowest score in top 10 (the last one since sorted descending)
        int lowestTopScore = scores.get(scores.size() - 1).score;

        // Only return true if score is GREATER than the lowest top score
        return score > lowestTopScore;
    }

    public void addScore(String name, int score, String difficulty) {
        ArrayList<Score> scores = highScoresByDifficulty.get(difficulty);
        if (scores == null) return;

        // Add the new score
        scores.add(new Score(name, score, difficulty));

        // Sort (highest first)
        Collections.sort(scores);

        // Keep only top 3
        if (scores.size() > 3) {
            scores = new ArrayList<>(scores.subList(0, 3));
            highScoresByDifficulty.put(difficulty, scores);
        }

        saveScores();
    }

    public String getFormattedScores(String difficulty) {
        ArrayList<Score> scores = highScoresByDifficulty.get(difficulty);
        if (scores == null || scores.isEmpty()) {
            return "No high scores for " + difficulty + " yet!";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("🏆 ").append(difficulty.toUpperCase()).append(" HIGH SCORES 🏆\n\n");
        for (int i = 0; i < scores.size(); i++) {
            Score s = scores.get(i);
            sb.append(String.format("%d. %s - %d points\n",
                    i + 1, s.name, s.score));
        }
        return sb.toString();
    }

    public String getAllFormattedScores() {
        StringBuilder sb = new StringBuilder();
        sb.append("🏆 HIGH SCORES BY DIFFICULTY 🏆\n\n");

        String[] difficulties = {"Easy", "Medium", "Hard", "Insane"};
        for (String diff : difficulties) {
            sb.append(getFormattedScores(diff)).append("\n");
        }
        return sb.toString();
    }


    private void loadScores() {
        try (BufferedReader reader = new BufferedReader(new FileReader(SCORE_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String name = parts[0];
                    int score = Integer.parseInt(parts[1]);
                    String difficulty = parts[2];

                    // Only add if it's a valid difficulty
                    if (difficulty.equals("Easy") || difficulty.equals("Medium") ||
                            difficulty.equals("Hard") || difficulty.equals("Insane")) {

                        ArrayList<Score> scores = highScoresByDifficulty.get(difficulty);
                        if (scores != null) {
                            scores.add(new Score(name, score, difficulty));
                        }
                    
                    }
                }
            }

            // Sort each difficulty's scores
            for (ArrayList<Score> scores : highScoresByDifficulty.values()) {
                Collections.sort(scores);
            }
        } catch (IOException e) {
            System.out.println("No existing high scores file.");
        }
    }

    private void saveScores() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(SCORE_FILE))) {
            for (Map.Entry<String, ArrayList<Score>> entry : highScoresByDifficulty.entrySet()) {
                for (Score s : entry.getValue()) {
                    writer.println(s.name + "," + s.score + "," + s.difficulty);
                }
            }
        } catch (IOException e) {
            System.out.println("Error saving scores: " + e);
        }
    }

    private class Score implements Comparable<Score> {
        String name;
        int score;
        String difficulty;

        Score(String name, int score, String difficulty) {
            this.name = name;
            this.score = score;
            this.difficulty = difficulty;
        }

        @Override
        public int compareTo(Score other) {
            return other.score - this.score; // Higher scores first
        }
    }
}