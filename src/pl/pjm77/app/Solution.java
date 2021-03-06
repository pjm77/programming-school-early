package pl.pjm77.app;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

public class Solution {
    private int id;
    private Timestamp created;
    private Timestamp updated;
    private String description;
    private int exercise_id;
    private int user_id;

    public Solution() {
    }

    public Solution(String description, int exercise_id, int user_id) {
        this.description = description;
        this.exercise_id = exercise_id;
        this.user_id = user_id;
    }

    public Solution(Timestamp created, Timestamp updated, String description, int exercise_id, int user_id) {
        this.created = created;
        this.updated = updated;
        this.description = description;
        this.exercise_id = exercise_id;
        this.user_id = user_id;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public Timestamp getUpdated() {
        return updated;
    }

    public void setUpdated(Timestamp updated) {
        this.updated = updated;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getExercise_id() {
        return exercise_id;
    }

    public void setExercise_id(int exercise_id) {
        this.exercise_id = exercise_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUsers_id(int user_id) {
        this.user_id = user_id;
    }

    public int getId() {
        return id;
    }

    void saveSolutionToDB() {
        try (Connection con = SQLConnection.connect()) {
            if (this.id == 0) {
                String sql = "INSERT INTO solution(created, updated, description, exercise_id, user_id) VALUES (?, ?, ?, ?, ?);";
                String[] generatedColumns = {" ID "};
                Date date = new Date();
                java.sql.Timestamp sqlDate = new java.sql.Timestamp(date.getTime());
                try (PreparedStatement ps = con.prepareStatement(sql, generatedColumns)) {
                    ps.setTimestamp(1, sqlDate);
                    ps.setTimestamp(2, null);
                    ps.setString(3, this.description);
                    ps.setInt(4, this.exercise_id);
                    ps.setInt(5, this.user_id);
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            this.id = rs.getInt(1);
                        }
                    }
                }
            } else {
                String sql = "UPDATE solution SET updated=Now(), description=?, exercise_id=?, user_id=? WHERE id = ?;";
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setString(1, this.description);
                    ps.setInt(2, this.exercise_id);
                    ps.setInt(3, this.user_id);
                    ps.setInt(4, this.id);
                    ps.executeUpdate();
                }
            }
        } catch (MySQLIntegrityConstraintViolationException m) {
            System.out.println("No such user or exercise!");
        } catch (SQLException e) {
            System.out.println("Database error!");
            e.printStackTrace();
        }
    }

    static Solution loadSolutionById(int id) {
        try (Connection con = SQLConnection.connect()) {
            String sql = "SELECT * FROM solution WHERE id=?;";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return loadSolution(rs);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Database error!");
            e.printStackTrace();
        }
        System.out.println("No such solution!");
        return null;
    }

    void deleteSolution() {
        try (Connection con = SQLConnection.connect()) {
            String sql = "DELETE FROM solution WHERE id=?";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, this.id);
                ps.executeUpdate();
                this.id = 0;
            }
        } catch (SQLException e) {
            System.out.println("Database error!");
            e.printStackTrace();
        }
    }

    static Solution[] loadAllSolutions() {
        String sql = "SELECT * FROM solution;";
        return loadSolutionsBy(true, sql, 0);
    }

    static Solution[] loadAllByUserId(int user_id) {
        String sql = "SELECT * FROM solution WHERE user_id=?;";
        return loadSolutionsBy(false, sql, user_id);
    }

    static Solution[] loadAllByExerciseId(int exercise_id) {
        String sql = "SELECT * FROM solution WHERE exercise_id=? ORDER BY created;";
        return loadSolutionsBy(false, sql, exercise_id);
    }

    private static Solution[] loadSolutionsBy(boolean loadAll, String sql, int param) {
        List<Solution> solutionsByParamArrayList = new ArrayList<>();
        try (Connection con = SQLConnection.connect()) {
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                if (!loadAll) ps.setInt(1, param);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        solutionsByParamArrayList.add(loadSolution(rs));
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Database error!");
            e.printStackTrace();
        }
        Solution[] solutionsByParamArray = new Solution[solutionsByParamArrayList.size()];
        solutionsByParamArray = solutionsByParamArrayList.toArray(solutionsByParamArray);
        return solutionsByParamArray;
    }

    private static Solution loadSolution(ResultSet rs) throws SQLException {
        Solution loadedSolution = new Solution();
        loadedSolution.id = rs.getInt("id");
        loadedSolution.created = rs.getTimestamp("created");
        loadedSolution.updated = rs.getTimestamp("updated");
        loadedSolution.description = rs.getString("description");
        loadedSolution.exercise_id = rs.getInt("exercise_id");
        loadedSolution.user_id = rs.getInt("user_id");
        return loadedSolution;
    }

    @Override
    public String toString() {
        return this.id + ": " + this.description + " created: " + this.created + " updated: " + this.updated + " exercise: " + this.exercise_id + " user: " + this.user_id;
    }
}