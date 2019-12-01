package eu.camillecroci.cst3130.coursework1.DAO;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

abstract public class AbstractDAO {

    private SessionFactoryManager sessionFactoryManager;


    protected Session getCurrentSession(){
        try {
            Session session = this.sessionFactoryManager.getSessionFactory().getCurrentSession();
            return session;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
//
//    protected Session initSession(){
//        try {
//            Session session = this.getSessionFactory().getCurrentSession();
//            return session;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    public SessionFactoryManager getSessionFactoryManager() {
        return sessionFactoryManager;
    }

    public void setSessionFactoryManager(SessionFactoryManager sessionFactoryManager) {
        this.sessionFactoryManager = sessionFactoryManager;
    }
}
