package org.example.Entities;

public class Favoris {
    private int id;
    private int coursId;

    public Favoris() {}

    public Favoris(int coursId) {
        this.coursId = coursId;
    }

    public Favoris(int id, int coursId) {
        this.id = id;
        this.coursId = coursId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCoursId() {
        return coursId;
    }

    public void setCoursId(int coursId) {
        this.coursId = coursId;
    }

    @Override
    public String toString() {
        return "Favoris{" +
                "id=" + id +
                ", coursId=" + coursId +
                '}';
    }
}
