package it.unicas.clinic.address.model;

/**
 * Implements the Credential table from the database.
 */
public class Credential {
    private String username;
    private String password;
    int owner;

    public Credential(String username, String password, int owner) {
        this.username = username;
        this.password = password;
        this.owner = owner;
    }

    public String getUsername() {return username;}

    public String getPassword() {return password;}

    public int getOwner() {return owner;}

    public void setUsername(String username) {this.username = username;}

    public void setPassword(String password) {this.password = password;}
}
