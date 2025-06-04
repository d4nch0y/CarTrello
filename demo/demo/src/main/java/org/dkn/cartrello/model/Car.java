package org.dkn.cartrello.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String brand;
    private String model;
    private int year;

    private String photoFilename;
    private String engineType;
    private double horsepower;
    private double engineDisplacement; // in cubic meters

    @Column(nullable = true)
    private String engineCode; // Optional

    private String ownerUsername;  // <-- New field to track owner

    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ForumPost> forums = new ArrayList<>();

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public String getPhotoFilename() { return photoFilename; }
    public void setPhotoFilename(String photoFilename) { this.photoFilename = photoFilename; }

    public String getEngineType() { return engineType; }
    public void setEngineType(String engineType) { this.engineType = engineType; }

    public double getHorsepower() { return horsepower; }
    public void setHorsepower(double horsepower) { this.horsepower = horsepower; }

    public double getEngineDisplacement() { return engineDisplacement; }
    public void setEngineDisplacement(double engineDisplacement) { this.engineDisplacement = engineDisplacement; }

    public String getEngineCode() { return engineCode; }
    public void setEngineCode(String engineCode) { this.engineCode = engineCode; }

    public String getOwnerUsername() { return ownerUsername; }
    public void setOwnerUsername(String ownerUsername) { this.ownerUsername = ownerUsername; }

    public List<ForumPost> getForums() { return forums; }
    public void setForums(List<ForumPost> forums) { this.forums = forums; }
}
