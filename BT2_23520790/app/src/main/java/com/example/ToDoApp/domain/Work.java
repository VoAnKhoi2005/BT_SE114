package com.example.ToDoApp.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public class Work implements Serializable {
    public String Id;
    public String Title = "";
    public String Description = "";
    public Date DeadLine;
    public boolean Status = false;

    private Work(){}

    public Work(String title, String description, Date deadLine, boolean status){
        Id = UUID.randomUUID().toString();
        Title = title;
        Description = description;
        DeadLine = deadLine;
        Status = status;
    }
}
