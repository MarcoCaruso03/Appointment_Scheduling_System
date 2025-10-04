package it.unicas.clinic.address.model;

import java.time.LocalDate;

/**
 * Implements the Client table from the database.
 */
public class Client {

    private int id;
    private String name;
    private String surname;
    private String email;
    private String number;

    public LocalDate getCancellationDate() {
        return cancellationDate;
    }

    public void setCancellationDate(LocalDate cancellationDate) {
        this.cancellationDate = cancellationDate;
    }

    private LocalDate cancellationDate;
    public Client(){
        this.id=0;
        this.name="";
        this.surname="";
        this.email="";
        this.number="";
        this.cancellationDate=null;
    }
    public Client(int id, String name, String surname, String email, String number) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.number = number;
        this.cancellationDate=null;
    }
    public Client(int id, String name, String surname, String email, String number, LocalDate cancellation) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.number = number;
        this.cancellationDate=cancellation;
    }

    // getters
    public int getId() {return id;}
    public String getName() {return name;}
    public String getSurname() {return surname;}
    public String getEmail() {return email;}
    public String getNumber() {return number;}

    // setters
    public void setId(int id) {this.id = id;}
    public void setName(String name) {this.name = name;}
    public void setSurname(String surname) {this.surname = surname;}
    public void setEmail(String email) {this.email = email;}
    public void setNumber(String number) {this.number = number;}

    public String toString() {
        return "id: " + this.id +
                " name: " + this.name +
                " surname: " + this.surname +
                " email: " + this.email +
                " Phone number: " + this.number;
    }
}
