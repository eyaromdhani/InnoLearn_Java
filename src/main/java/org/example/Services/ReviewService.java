package org.example.Services;

import org.example.Entities.Review;
import org.example.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewService {
    private Connection connection;

    public ReviewService() {
        this.connection = MyDataBase.getInstance().getConnection();
    }

    public void addReview(Review r) {
        String query = "INSERT INTO review (event_id, user_id, user_name, comment, rating, date_review) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, r.getEventId());
            statement.setInt(2, r.getUserId());
            statement.setString(3, r.getUserName());
            statement.setString(4, r.getComment());
            statement.setInt(5, r.getRating());
            statement.setTimestamp(6, r.getDateReview() != null ? Timestamp.valueOf(r.getDateReview()) : Timestamp.valueOf(java.time.LocalDateTime.now()));
            statement.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("Error adding review: " + ex.getMessage());
        }
    }

    public List<Review> getReviewsByEvent(int eventId) {
        List<Review> reviews = new ArrayList<>();
        String query = "SELECT * FROM review WHERE event_id = ? ORDER BY date_review DESC";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, eventId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    Review r = new Review();
                    r.setId(rs.getInt("id"));
                    r.setEventId(rs.getInt("event_id"));
                    r.setUserId(rs.getInt("user_id"));
                    r.setUserName(rs.getString("user_name"));
                    r.setComment(rs.getString("comment"));
                    r.setRating(rs.getInt("rating"));
                    Timestamp ts = rs.getTimestamp("date_review");
                    if (ts != null) r.setDateReview(ts.toLocalDateTime());
                    reviews.add(r);
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error getting reviews: " + ex.getMessage());
        }
        return reviews;
    }
}
