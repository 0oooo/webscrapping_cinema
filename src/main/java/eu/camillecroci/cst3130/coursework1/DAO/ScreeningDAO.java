package eu.camillecroci.cst3130.coursework1.DAO;

import eu.camillecroci.cst3130.coursework1.Cinema;
import eu.camillecroci.cst3130.coursework1.Movie;
import eu.camillecroci.cst3130.coursework1.Screening;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.sql.Date;

public class ScreeningDAO {

    private SessionFactory sessionFactory;

    public ScreeningDAO(){}

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

    public void addScreening(Date screeningDate, Movie movie, Cinema cinema) {

        Session session = sessionFactory.getCurrentSession();

        Screening screening = new Screening();

        screening.setScreeningDate(screeningDate);
        screening.setMovie(movie);
        screening.setCinema(cinema);

        session.beginTransaction();

        session.save(screening);

        session.getTransaction().commit();

        session.close();
        System.out.println("Screening added to database with ID" + screening.getId());
    }

    public void shutDown() {
        sessionFactory.close();
    }
}
