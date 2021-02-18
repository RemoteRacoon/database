package entities;

import db.Db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class User {
    private Integer id;
    private String login;
    private String first_name;
    private String last_name;

    private ArrayList<Account> accounts = new ArrayList<>();
    /**
     * Here we put clients which then will be put in a day schedule
     * according to an average day traffic.
     */
    private ArrayList<Client> clientsBuffer = new ArrayList<>();
    private HashMap<Date, ArrayList<Account>> accountsPerDay = new HashMap<>();
    private HashMap<Date, ArrayList<Client>> clientsPerDay = new HashMap<>();

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

    public void addAccount(Account acc) {
        accounts.add(acc);
    }

    public void addAccount(ArrayList<Account> accs) {
        accounts.addAll(accs);
    }

    public ArrayList<Account> removeAccount(int range) {
        ArrayList<Account> acc_to_remove = new ArrayList<>();

       for (int i = 0; i < range; i++) {
           acc_to_remove.add(accounts.remove(i));
       }

       return acc_to_remove;
    }

    public void addClient(Client cl) {
        clientsBuffer.add(cl);
    }

    public void addClient(ArrayList<Client> cls) {
        clientsBuffer.addAll(cls);
    }

    public void addClientPerDay(HashMap<Date, ArrayList<Client>> cls) {
        clientsPerDay.putAll(cls);
    }

    public int getNumOfAccounts() {
        return accounts.size();
    }

    public int getNumOfClients() {
        int num_of_clients = 0;

        for (Map.Entry<Date, ArrayList<Client>> entry: this.clientsPerDay.entrySet()) {
            num_of_clients += (entry.getValue()).size();
        }

        return num_of_clients;
    }

    public int getNumOfBufferedClients() {
        return clientsBuffer.size();
    }

    public void putClientToBuffer(Client cl) {
        clientsBuffer.add(cl);
    }

    public void putClientToBuffer(ArrayList<Client> cls) {
        clientsBuffer.addAll(cls);
    }

    public void fetchOccupiedAccountDates(Db db) {
        try {
            ResultSet set = db.execute(String.format("select * from (select art.user_login, art.allocation_date, from act_ru_task art\n" +
                    "    join relationship_client_account rca on art.client_id = rca.client_id\n" +
                    "    join account a on rca.account_id = a.account_id where a.is_deleted = 0 and (a.b1 > 0 or a.b2 > 0 or a.b3 > 0)\n" +
                    "    ) where user_login = %s;", this.getLogin()));

            /**
             * Here we put dates to allocate accounts according to an average load per day
             */
            while (set.next()) {
                Date date = (Date) set.getObject(2);

                if (this.accountsPerDay.get(date) == null) {
                    this.accountsPerDay.put(date, new ArrayList<>());
                }
            }


        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public HashMap<Date, ArrayList<Account>> getOccupiedDates() {
        return this.accountsPerDay;
    }

    public ArrayList<Account> getAccounts() {
        return this.accounts;
    }

    /**
     * Распределить новых клиентов по дням
     */
    public void releaseBuffer() {
        int total_load = getNumOfBufferedClients();
        int average_load = 0;
        int load_resid = 0;
        int num_of_days = 0;

        for (Map.Entry entry: clientsPerDay.entrySet()) {
            total_load += ((ArrayList) entry.getValue()).size();
            num_of_days += 1;
        }

        average_load = total_load / num_of_days;

        for (Map.Entry entry: clientsPerDay.entrySet()) {
            ArrayList<Client> cls = (ArrayList) entry.getValue();
            int load = cls.size();

            if (load > average_load) {
                int diff = load - average_load;

                for (int i = 0; i < diff; i++) {
                    clientsBuffer.add(cls.remove(i));
                }
            } else if (load < average_load) {
                int diff = average_load - load;

                for (int i = 0; i < diff; i++) {
                    cls.add(clientsBuffer.remove(i));
                }
            }
        }

        load_resid = clientsBuffer.size();
        /**
         * Распределить остаток
         */
        if (load_resid != 0) {
            for (Map.Entry entry: clientsPerDay.entrySet()) {
                ArrayList<Client> cls = (ArrayList) entry.getValue();
                cls.add(clientsBuffer.remove(0));
                average_load--;
            }
        }
    }
}

