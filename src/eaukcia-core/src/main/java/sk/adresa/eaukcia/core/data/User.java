package sk.adresa.eaukcia.core.data;

import sk.adresa.eaukcia.core.data.LoggableObject;

public class User implements LoggableObject {
    
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_USER = "USER";
   
    private String login;
    private String role;
    private String name, surname;

    public User() {
    }
    
    public User(User template){
        login = template.getLogin();
        role = template.getRole();
        name = template.getName();
        surname = template.getSurname();
    }

    public User(String login) {
        this.login = login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getLogin() {
        return login;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getSurname() {
        return surname;
    }


    @Override
    public String toString() {
        return "\nObjekt User:{"
                + "\n  Login: " + getLogin()
                + "\n  Name: " + getName()
                + "\n  Surname: " + getSurname()
                + "\n  Role: " + getRole()
                + "\n}";

    }


    public void setRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    @Override
    public String toLogString() {
        return null;
    }

    
    
    public String getName() {
        return name;
    }
    
    
    
    
}
