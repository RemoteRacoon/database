package allocation;

import db.Db;
import entities.Account;
import entities.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Allocate accounts
 */
public class DefaultAllocatorC extends TimerTask implements Allocation {
    private ArrayList<User> users = new ArrayList<>();
    private Map<User, ArrayList<Account>> account_user_pool = new HashMap<>();
    private Db db;

    public DefaultAllocatorC() {
        try {
            this.db = Db.getInstance();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void fetchUsersAndAccounts() {
        try {
            ResultSet wholePeriodSet = db.execute("select art.user_login, a.account_id account_load from act_ru_task art\n" +
                    "    join relationship_client_account rca on art.client_id = rca.client_id\n" +
                    "    join account a on rca.account_id = a.account_id where a.is_deleted = 0 and (a.b1 > 0 or a.b2 > 0 or a.b3 > 0)\n" +
                    "    order by user_login;");

            /**
             * Iterate over the result from database and populate the pool
             */
            while (wholePeriodSet.next()) {
                String user_login = (String) wholePeriodSet.getObject(1);
                Integer account_id = (Integer) wholePeriodSet.getObject(2);
                User user = new User();
                Account acc = new Account();

                user.setLogin(user_login);
                acc.setAccount_id(account_id);

                if (this.account_user_pool.get(user) == null) {
                    this.account_user_pool.put(user, new ArrayList<>(
                            Collections.singletonList(acc)
                    ));
                } else {
                    this.account_user_pool.get(user).add(acc);
                }
            }

            this.account_user_pool.forEach((user, account) -> {
                this.users.add(user);
            });
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void allocateTotal() {

        for (int i = 0; i < users.size(); i++) {
            User user = this.users.get(i);
            ArrayList<Account> accounts = this.account_user_pool.get(user);
            user.addAccount(accounts);
        }

        int average_load;
        int total_load = 0;

        for (User user : users) {
            total_load += user.getNumOfAccounts();
        }

        average_load = total_load / users.size();

        /**
         * Allocate evenly within 2 iterations
         */
        for (int k = 0; k < 2; k++) {

            this.users.sort(Comparator.comparingInt(User::getNumOfAccounts));

            /**
             * Take 1 user with a greater amount of accounts and another with fewer ones.
             * (head and tail)
             */
            for (int i = this.users.size() - 1, j = 0; i > j; i--, j++) {
                User user1 = this.users.get(i);
                User user2 = this.users.get(j);

                if (user2.getNumOfAccounts() < average_load) {
                    int diff = user1.getNumOfAccounts() - average_load;
                    ArrayList<Account> accounts_to_move = user1.removeAccount(diff);
                    user2.addAccount(accounts_to_move);
                }
            }
        }
    }

    private void fetchDaysLoaded() {
        this.users.forEach(user -> {
            user.fetchOccupiedAccountDates(db);
        });

    }

    private void allocatePerDay() {
        this.users.forEach(user -> {
            HashMap<Date, ArrayList<Account>> occ_days = user.getOccupiedDates();
            int num_of_occupied_days = occ_days.size();
            int num_of_user_accounts = user.getNumOfAccounts();
            int main_accounts = num_of_user_accounts / num_of_occupied_days;
            int extra_accounts = num_of_user_accounts % num_of_occupied_days;

            ArrayList<Account> accounts = (ArrayList<Account>) user.getAccounts().clone();
            int i = 0;

            occ_days.forEach((day, accs) -> {
                if (i <= extra_accounts) {
                    for (int j = 0; j < main_accounts; j++) {
                        occ_days.get(day).add((accounts.remove(j)));
                    }
                } else {
                    for (int j = 0; j < extra_accounts; j++) {
                        occ_days.get(day).add(accounts.remove(j));
                    }
                }
            });
        });
    }


    public void allocate() {
        fetchUsersAndAccounts();
        allocateTotal();
        fetchDaysLoaded();
        allocatePerDay();
    }

    @Override
    public void run() {
        allocate();
    }
}
