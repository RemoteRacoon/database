package entities;

public class Client {
    /**
     * @Column(name='id')
     */
    private Integer id;

    /**
     * @Column(name='client_id')
     */
    private Integer client_id;

    /**
     * @Column(name='client_ext_id');
     */
    private String client_ext_id;

    public Integer getClient_id() {
        return client_id;
    }

    public String getClient_ext_id() {
        return client_ext_id;
    }

    public void setClient_id(Integer client_id) {
        this.client_id = client_id;
    }

    public void setClient_ext_id(String client_ext_id) {
        this.client_ext_id = client_ext_id;
    }
}
