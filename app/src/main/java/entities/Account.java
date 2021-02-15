package entities;

import com.sun.org.apache.xpath.internal.operations.Bool;

public class Account {
    private Integer id;
    private Integer account_id;
    private String account_ext_id;
    private Bool is_deleted;
    private float b1;
    private float b2;
    private float b3;

    public Integer getAccount_id() {
        return account_id;
    }

    public String getAccount_ext_id() {
        return account_ext_id;
    }

    public Bool getIs_deleted() {
        return is_deleted;
    }

    public float getB1() {
        return b1;
    }

    public float getB2() {
        return b2;
    }

    public float getB3() {
        return b3;
    }

    public void setAccount_id(Integer account_id) {
        this.account_id = account_id;
    }

    public void setAccount_ext_id(String account_ext_id) {
        this.account_ext_id = account_ext_id;
    }

    public void setIs_deleted(Bool is_deleted) {
        this.is_deleted = is_deleted;
    }

    public void setB1(float b1) {
        this.b1 = b1;
    }

    public void setB2(float b2) {
        this.b2 = b2;
    }

    public void setB3(float b3) {
        this.b3 = b3;
    }
}
