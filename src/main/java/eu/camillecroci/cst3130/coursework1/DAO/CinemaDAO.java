package eu.camillecroci.cst3130.coursework1.DAO;

import eu.camillecroci.cst3130.coursework1.Cinema;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

public class CinemaDAO extends AbstractDAO {

    public void addCinema(String name, String address, String phoneNumber, String companyname) {
        Session session = super.getCurrentSession();

        Cinema cinema = new Cinema();

        cinema.setName(name);
        cinema.setAddress(address);
        cinema.setPhone(phoneNumber);
        cinema.setCompanyName(companyname);

        session.beginTransaction();

        session.save(cinema);

        session.getTransaction().commit();

        System.out.println("Cinema added to database with ID" + cinema.getId());

        session.close();
    }

    public void updateCinemaName(int id, String name) {
        Session session = super.getCurrentSession();

        try {
            session.beginTransaction();

            Cinema cinema = (Cinema) session.get(Cinema.class, id);

            cinema.setName(name);

            session.update(cinema);

            session.getTransaction().commit();
        } catch (HibernateException e) {
            if (session.getTransaction() != null) session.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            session.close();
            System.out.println("Cinema " + id + " changed name for " + name + ".");
        }

    }

    public int searchCinemaIdByName(String name) {
        Session session = super.getCurrentSession();

        Cinema cinema = new Cinema();

        try {
            session.beginTransaction();

            //NB: for search you need to use the class name and class property (here Cinema with a capital and name, not cinema_name
//            List<Cinema> cinemasList = session.createQuery("from Cinema where name = \'" + name + "\'").getResultList();
            cinema = (Cinema) session.createQuery("from Cinema where name = \'" + name + "\'").getSingleResult();

        } catch (HibernateException e) {
            if (session.getTransaction() != null) session.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            session.close();
            return cinema.getId();
        }
    }

    public Cinema searchCinemaByName(String name) {
        Session session = super.getCurrentSession();
        Cinema cinema = new Cinema();
        try {
            session.beginTransaction();
            cinema = (Cinema) session.createQuery("from Cinema where name = \'" + name + "\'").getSingleResult();
        } catch (HibernateException e) {
            if (session.getTransaction() != null) session.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            session.close();
            return cinema;
        }
    }

    public int getCinemaNumber(){
        Session session = super.getCurrentSession();
        int totalNumOfCinemas = 0;
        try {
            session.beginTransaction();

            Query query = session.createQuery("select count (*) from Cinema");
        } catch (HibernateException e) {
            if (session.getTransaction() != null) session.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            session.close();
            return totalNumOfCinemas;
        }
    }

    public List<Cinema> getCinemasByCompanyName(String company){
        Session session = super.getCurrentSession();
        List<Cinema> allCinemas = new ArrayList<Cinema>();
        try {
            session.beginTransaction();
            allCinemas = session.createQuery("from Cinema where companyName = '" + company + "'").getResultList();
        } catch (HibernateException e) {
            if (session.getTransaction() != null) session.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            session.close();
            return allCinemas;
        }
    }

    public void deleteCinemaSimple(int id) {
        Session session = super.getCurrentSession();

        try {
            session.beginTransaction();

            Cinema cinema = (Cinema) session.get(Cinema.class, id);
            session.delete(cinema);

            session.getTransaction().commit();

        } catch (HibernateException e) {
            if (session.getTransaction() != null) session.getTransaction().rollback();
            e.printStackTrace();
        }
        session.close();
    }

    public Cinema getCinemaById(int id){
        Session session = super.getCurrentSession();
        Cinema cinema = new Cinema();
        try {
            session.beginTransaction();

            cinema = (Cinema) session.get(Cinema.class, id);

            session.getTransaction().commit();

        } catch (HibernateException e) {
            if (session.getTransaction() != null) session.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            session.close();
            return cinema;
        }
    }

    public void deleteCinemaSafe(int id) {
        Session session = super.getCurrentSession();
        try {
            session.beginTransaction();

            Object persistentInstance = session.load(Cinema.class, id);

            if (persistentInstance != null) {
                session.delete(persistentInstance);
            }

            session.getTransaction().commit();

        } catch (HibernateException e) {
            if (session.getTransaction() != null) session.getTransaction().rollback();
            e.printStackTrace();
        }
        session.close();
    }

}
