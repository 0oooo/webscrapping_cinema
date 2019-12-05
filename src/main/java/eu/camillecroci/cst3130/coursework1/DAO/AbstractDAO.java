package eu.camillecroci.cst3130.coursework1.DAO;

import org.hibernate.Session;

abstract public class AbstractDAO {

    private SessionFactoryManager sessionFactoryManager;

    protected Session getCurrentSession(){
        try {
            Session session = sessionFactoryManager.getSessionFactory().getCurrentSession();
            return session;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public SessionFactoryManager getSessionFactoryManager() {
        return sessionFactoryManager;
    }

    public void setSessionFactoryManager(SessionFactoryManager sessionFactoryManager) {
        this.sessionFactoryManager = sessionFactoryManager;
    }
}
