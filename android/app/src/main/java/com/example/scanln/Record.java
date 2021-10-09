package com.example.scanln;

import java.time.LocalDateTime;

public class Record {
    private String student_id;
    private String session_id;
    private LocalDateTime time;

    public Record(String student_id, String session_id, LocalDateTime time) {
        this.student_id = student_id;
        this.session_id = session_id;
        this.time = time;
    }

    public String getStudent_id() {
        return student_id;
    }

    public void setStudent_id(String student_id) {
        this.student_id = student_id;
    }

    public String getSession_id() {
        return session_id;
    }

    public void setSession_id(String session_id) {
        this.session_id = session_id;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }
}
