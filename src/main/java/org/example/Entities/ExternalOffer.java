package org.example.Entities;

public class ExternalOffer {
    private String title;
    private String company;
    private String location;
    private String url;
    private String description;
    private String level;

    public ExternalOffer() {}

    public ExternalOffer(String title, String company, String location, String url, String description, String level) {
        this.title = title;
        this.company = company;
        this.location = location;
        this.url = url;
        this.description = description;
        this.level = level;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
}
