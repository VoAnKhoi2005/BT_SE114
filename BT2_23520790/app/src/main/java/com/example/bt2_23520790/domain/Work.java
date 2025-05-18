package com.example.bt2_23520790.domain;

import java.io.Serializable;
import java.util.Date;

public class Work implements Serializable {
    public String Title;
    public Date DeadLine;
    public WorkStatus Status;

    public enum WorkStatus {
        NOT_STARTED("Not started"),
        IN_PROGRESS("In progress"),
        COMPLETED("Completed"),
        ON_HOLD("On hold"),
        CANCELLED("Cancelled");

        private final String description;

        WorkStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
