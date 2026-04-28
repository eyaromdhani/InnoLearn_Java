package org.example.entity;

import java.time.LocalDateTime;

public class Review {
    private int id;
    private int eventId;
    private int userId;
    private String userName;
    private String comment;
    private int rating;
    private LocalDateTime dateReview;

    public Review() {}

    public Review(int eventId, int userId, String userName, String comment, int rating, LocalDateTime dateReview) {
        this.eventId = eventId;
        this.userId = userId;
        this.userName = userName;
        this.comment = comment;
        this.rating = rating;
        this.dateReview = dateReview;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public LocalDateTime getDateReview() { return dateReview; }
    public void setDateReview(LocalDateTime dateReview) { this.dateReview = dateReview; }

    @Override
    public String toString() {
        return userName + " (" + rating + "⭐): " + comment;
    }
}
