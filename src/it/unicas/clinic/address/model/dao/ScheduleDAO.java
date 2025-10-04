package it.unicas.clinic.address.model.dao;

import it.unicas.clinic.address.model.Schedule;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public interface ScheduleDAO <T>{
    List<T> select(T s) throws StaffException;
    void update(T s) throws StaffException;
    void insert(T s) throws StaffException;
    void delete(T s) throws StaffException;
    boolean isAvailable(LocalDate day, LocalTime time, int staff_id);
    public Schedule getLastSchedule() throws ScheduleException;
    ArrayList<T> futureSchedule(int staff_id) throws SQLException;

    List<Schedule> getfutureSchedule(LocalDate d, int id) throws RuntimeException;
}
