package it.unicas.clinic.address.model;

import it.unicas.clinic.address.model.dao.StaffException;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDate;

/**
 * Implements the Staff table from the database.
 */
public class Staff {
    //the same field of the table Staff in the db
    private IntegerProperty id;
    private StringProperty name;
    private StringProperty surname;
    private StringProperty specialties;

    public LocalDate getFiredDate() {
        return firedDate;
    }

    public void setFiredDate(LocalDate firedDate) {
        this.firedDate = firedDate;
    }

    private LocalDate firedDate;
    //Constructos
    public Staff(Integer id, String name, String surname, String specialties){
        this.name = new SimpleStringProperty(name);
        this.surname = new SimpleStringProperty(surname);
        this.specialties = new SimpleStringProperty(specialties);
        if(id != null){
            this.id = new SimpleIntegerProperty(id);
        }
        else{
            this.id = new SimpleIntegerProperty(0);
        }
        firedDate=null;
    }
    public Staff(String name, String surname, String specialties){
        this.name = new SimpleStringProperty(name);
        this.surname = new SimpleStringProperty(surname);
        this.specialties = new SimpleStringProperty(specialties);
        firedDate=null;
        //this.id= new SimpleIntegerProperty(0);

    }
    public Staff(int id,String name, String surname, String specialties, LocalDate firedDate){
        this.id=new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.surname = new SimpleStringProperty(surname);
        this.specialties = new SimpleStringProperty(specialties);
        this.firedDate=firedDate;
        //this.id= new SimpleIntegerProperty(0);

    }

    public Staff() {
        this.id = new SimpleIntegerProperty(0);
        this.name = new SimpleStringProperty("");
        this.surname = new SimpleStringProperty("");
        this.specialties = new SimpleStringProperty("");
        firedDate=null;
    }

    //gets and sets

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getSurname() {
        return surname.get();
    }

    public StringProperty surnameProperty() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname.set(surname);
    }

    public String getSpecialties() {
        return specialties.get();
    }

    public StringProperty specialtiesProperty() {
        return specialties;
    }

    public void setSpecialties(String specialties) {
        this.specialties.set(specialties);
    }

    public boolean verifyStaff(Staff s){
        if (s.getName().equals("") || s.getSurname().equals("")
                || s.getSpecialties().equals("")) {
            return false;
        }
        else
            return true;
    }

    //Override toString

    @Override
    public String toString() {
        return
                "id: "+ id.getValue()+ ", " +
                "name: " + name.getValue() + ", " +
                "surname: " +surname.getValue() + ", " +
                "specialities: " +specialties.getValue() + "\n";
    }
    public static void main(String[] args){

    }
}
