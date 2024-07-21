package com.javacode.test.execeptions.responces;

import lombok.Getter;

import java.util.Date;
import java.util.Objects;

@Getter
public class AppError {
    private final int status;
    private final String message;
    private final Date timestamp;

    public AppError(int status, String message, Date timestamp) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
    }

    public AppError(int status, String message) {
        this(status, message, new Date());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        AppError that = (AppError) obj;
        return this.status == that.status &&
                Objects.equals(this.message, that.message) &&
                Objects.equals(this.timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, message, timestamp);
    }

    @Override
    public String toString() {
        return "AppError[" +
                "status=" + status + ", " +
                "message=" + message + ", " +
                "timestamp=" + timestamp + ']';
    }

}