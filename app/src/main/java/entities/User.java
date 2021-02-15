package entities;

public class User {
    private Integer id;
    private String login;
    private String first_name;
    private String last_name;

    public String getLogin() {
        return login;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }
}

