package com.lab1.server.domain;

public class Vacancy {
    private Integer id;
    private Speciality speciality;
    private String company;
    private String position;
    private int higherAgeLimit;
    private int lowerAgeLimit;
    private int salary;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Speciality getSpeciality() {
        return speciality;
    }

    public void setSpeciality(Speciality speciality) {
        this.speciality = speciality;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public int getHigherAgeLimit() {
        return higherAgeLimit;
    }

    public void setHigherAgeLimit(int higherAgeLimit) {
        this.higherAgeLimit = higherAgeLimit;
    }

    public int getLowerAgeLimit() {
        return lowerAgeLimit;
    }

    public void setLowerAgeLimit(int lowerAgeLimit) {
        this.lowerAgeLimit = lowerAgeLimit;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }
}
