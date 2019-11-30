package eu.camillecroci.cst3130.coursework1.DAO;

import eu.camillecroci.cst3130.coursework1.Movie;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.util.Date;

public class MovieDAO {

    private SessionFactory sessionFactory;

    public MovieDAO(){}

    public void init() {
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

    public void addMovie(String name, String description,String details, String url) {

        Session session = sessionFactory.getCurrentSession();

        Movie movie = new Movie();

        movie.setName(name);
        movie.setDescription(description);
        movie.setDetails(details);
        movie.setUrl(url);

        session.beginTransaction();

        session.save(movie);

        session.getTransaction().commit();

        session.close();
        System.out.println("Movie added to database with ID" + movie.getId());
    }

    public int searchMovieIdByName(String name) {
        Session session = sessionFactory.getCurrentSession();
        Movie movie = new Movie();

        try {
            session.beginTransaction();

            //NB: for search you need to use the class name and class property (here Cinema with a capital and name, not cinema_name
//            List<Cinema> cinemasList = session.createQuery("from Cinema where name = \'" + name + "\'").getResultList();
            movie = (Movie) session.createQuery("from Movie where name = \'" + name + "\'").getSingleResult();

        } catch (HibernateException e) {
            if (session.getTransaction() != null) session.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            session.close();
            return movie.getId();
        }
    }

    public Movie getMovieById(int id){
        Session session = sessionFactory.getCurrentSession();
        Movie movie = new Movie();
        try {
            session.beginTransaction();

            movie = (Movie) session.get(Movie.class, id);

            session.getTransaction().commit();

        } catch (HibernateException e) {
            if (session.getTransaction() != null) session.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            session.close();
            return movie;
        }
    }

    public void shutDown() {
        sessionFactory.close();
    }
}
