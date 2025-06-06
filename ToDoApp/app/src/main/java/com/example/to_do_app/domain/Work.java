package com.example.to_do_app.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Work implements Serializable {
    public String Id;
    public String Title = "";
    public String Description = "";
    public Date DeadLine;
    public boolean Status = false;
    public boolean Selected = false;

    private Work(){}

    public Work(String id, String title, String description, Date deadLine, boolean status){
        Id = id;
        Title = title;
        Description = description;
        DeadLine = deadLine;
        Status = status;
    }

    public String getId(){
        return this.Id;
    }

    public String getTitle(){
        return this.Title;
    }

    public String getDesc(){
        return this.Description;
    }

    public Date getDeadLine(){
        return this.DeadLine;
    }

    public Boolean isDone(){
        return this.Status;
    }
}
