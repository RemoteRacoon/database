package entities;

import java.util.Date;

public class Task {
    private Integer id;
    private String user_login;
    private Integer client_id;
    private Date allocation_date;

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

    public void setAllocation_date(Date d) {
        this.allocation_date = d;
    }

    public Date getAllocation_date() {
        return this.allocation_date;
    }
}
