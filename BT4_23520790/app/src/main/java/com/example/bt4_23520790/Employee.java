package com.example.bt4_23520790;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Employee implements Serializable {
    public Employee(){
        this.name ="name";
        this.age = 0;
        this.salary =0;
        this.profileImage ="url";
    }
    @SerializedName("id")
    private String id;

    @SerializedName("employee_name")
    private String name;

    @SerializedName("employee_age")
    private int age;

    @SerializedName("employee_salary")
    private int salary;

    @SerializedName("profile_image")
    private String profileImage;

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public int getSalary() { return salary; }
    public String getProfileImage() { return profileImage; }
    public void setName(String name){
        this.name = name;
    }
    public void setId(String id){
        this.id = id;
    }
    public void setAge(String age){
        if (!age.isEmpty()) {
            try {
                this.age = Integer.parseInt(age);
            } catch (NumberFormatException e) {
            }
        }
    }
    public void setSalary(String salary){
        if (!salary.isEmpty()) {
            try {
                this.salary = Integer.parseInt(salary);
            } catch (NumberFormatException e) {
            }
        }
    }
    public void setProfileImage(String url){
        this.profileImage = url;
    }
}

