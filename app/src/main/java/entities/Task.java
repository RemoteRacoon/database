package entities;

public class Task {
    private Integer id;
    private String user_login;
    private Integer client_id;

    public String getUser_login() {
        return user_login;
    }

    public Integer getClient_id() {
        return client_id;
    }

    public void setUser_login(String user_login) {
        this.user_login = user_login;
    }

    public void setClient_id(Integer client_id) {
        this.client_id = client_id;
    }
}
