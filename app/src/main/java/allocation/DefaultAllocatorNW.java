package allocation;

import db.Db;
import entities.Client;
import entities.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Allocate clients
 */
public class DefaultAllocatorNW extends TimerTask implements Allocation {
   private ArrayList<User> users = new ArrayList<>();
   private HashMap<User, HashMap<Date, ArrayList<Client>>> client_user_pool = new HashMap<>();
   private ArrayList<Client> clientsToAllocate;
   private Db db;

   public DefaultAllocatorNW() {
      try {
         this.db = Db.getInstance();
         fetchClientToAllocate();
      } catch (SQLException e) {
         System.out.println(e.getMessage());
      }
   }

   private void fetchUsersAndClients() {
      try {
         ResultSet set = db.execute("select user_login, client_id, allocation_date from act_ru_task;");

         /**
          * Iterate over the result from database and populate the pool
          */
         while (set.next()) {
            String user_login = (String) set.getObject(1);
            Integer client_id = (Integer) set.getObject(2);
            Date date = (Date) set.getObject(3);
            User user = new User();
            Client client = new Client();

            user.setLogin(user_login);
            client.setClient_id(client_id);

            if (this.client_user_pool.get(user) == null && this.client_user_pool.get(user).get(date) == null) {
               this.client_user_pool.put(user, new HashMap<Date, ArrayList<Client>>(){{
                  put(date, new ArrayList<>(Arrays.asList(client)));
               }});
            } else if (this.client_user_pool.get(user) != null && this.client_user_pool.get(user).get(date) != null) {
               this.client_user_pool.get(user).get(date).add(client);
            } else {
               this.client_user_pool.get(user).put(date, new ArrayList<>(Arrays.asList(client)));
            }
         }

         this.client_user_pool.forEach((user, entry) -> {
            user.addClientPerDay(entry);
            this.users.add(user);
         });
      } catch (SQLException e) {
         System.out.println(e.getMessage());
      }
   }

   /**
    * Распределяем нагрузку за весь период
    */
   private void prepareToAllocate() {
      int num_of_clients_to_allocate = clientsToAllocate.size();
      HashMap<User, Integer> user_client_load = new HashMap<>();

      users.forEach(user -> {
         user_client_load.put(user, user.getNumOfClients());
      });

      /**
       * Max load of user's clients during the whole period
       */
      int max_load = -1;

      for (Map.Entry<User, Integer> entry: user_client_load.entrySet()) {
         if (max_load == -1 || entry.getValue().compareTo(max_load) > 0) {
            max_load = entry.getValue();
         }
      }

      /**
       * The variable shows how many clients
       * we have to add to a user to be equal
       * with the max value (max_load)
       */
      int portion_to_make_equal = 0;

      for (User user: users) {
         portion_to_make_equal += max_load - user.getNumOfClients();
      }


      /**
       * Для каждого пользователя считаем наилучший вариант количества
       * распределяемых клиентов.
       * Выше был объявлен параметр portion_to_make_equal, который считает,
       * сколько пользователей нужно добавить для каждого клиента, чтобы сравняться
       * с пользователем, у которого количество клиентов максимально.
       * 1. Если пользователей для распределения меньше, чем portion_to_make_equal
       */
      if (num_of_clients_to_allocate < portion_to_make_equal) {
         for (User user: users) {
            int num_of_cl_to_buffer = (int) Math.floor(num_of_clients_to_allocate * user.getNumOfClients() / portion_to_make_equal);
            for (int i = 0; i < num_of_cl_to_buffer; i++) {
               user.putClientToBuffer(clientsToAllocate.remove(i));
            }
         }

         /**
          * Проверяем остаток
          */
         int residual = num_of_clients_to_allocate;

         for (User user: users) {
            residual -= user.getNumOfBufferedClients();
         }

         /**
          * Если остаток остался, сортируем пользователей и
          * добавляем по одному с наименьшим количеством
          */
         if (residual != 0) {
            users.sort(Comparator.comparingInt(User::getNumOfBufferedClients));

            for (int i = 0; i < residual; i++) {
               users.get(i).putClientToBuffer(clientsToAllocate.remove(i));
            }
         }
      } else if (num_of_clients_to_allocate > portion_to_make_equal) {
         /**
          * Остаток при распределении
          */
         int residual = num_of_clients_to_allocate % portion_to_make_equal;

         for (User user: users) {
            int client_to_allocate = max_load - user.getNumOfClients();

            for (int i = 0; i < client_to_allocate; i++) {
               user.putClientToBuffer(clientsToAllocate.remove(i));
            }

            if (residual != 0) {
               user.putClientToBuffer(clientsToAllocate.remove(0));
            }
            residual--;
         }
      } else {
         for (User user: users) {
            int client_to_allocate = max_load - user.getNumOfClients();

            for (int i = 0; i < client_to_allocate; i++) {
               user.putClientToBuffer(clientsToAllocate.remove(i));
            }
         }
      }

   }

   private void allocateClientsPerDate() {
      for (User user: users) {
         user.releaseBuffer();
      }
   }

   private void fetchClientToAllocate() {
      clientsToAllocate = new ArrayList<>();
      int randomNumber = (int) (Math.random() * 20 + 10);
      for (int i = 0; i < randomNumber; i++) {
         Client cl = new Client();
         int client_id = (int) (new Date().getTime() / 10000);
         cl.setClient_id(client_id);
         clientsToAllocate.add(cl);
      }
   }

   public void allocate() {
      fetchUsersAndClients();
      prepareToAllocate();
      allocateClientsPerDate();
   }

   @Override
   public void run() {
      allocate();
   }
}
