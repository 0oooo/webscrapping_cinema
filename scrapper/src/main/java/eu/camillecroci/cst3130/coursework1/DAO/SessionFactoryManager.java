package eu.camillecroci.cst3130.coursework1.DAO;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class SessionFactoryManager {

    private static SessionFactory sessionFactory;

    public SessionFactoryManager(){
      if(sessionFactory == null){
          try {
              StandardServiceRegistryBuilder standardServiceRegistryBuilder = new StandardServiceRegistryBuilder();
              standardServiceRegistryBuilder.configure("hibernate.cfg.xml");
              StandardServiceRegistry registry = standardServiceRegistryBuilder.build();

              try {
                  sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
              } catch (Exception e) {
                  System.err.println("Session Factory build failed");
                  e.printStackTrace();
                  StandardServiceRegistryBuilder.destroy(registry);
              }
              System.out.println("Session factory build");
          } catch (Exception ex) {
              System.err.println("SessionFactory creation failed." + ex);
          }
      }
    }

    public static void setSessionFactory(SessionFactory sessionFactory) {
        SessionFactoryManager.sessionFactory = sessionFactory;
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
