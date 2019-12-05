package eu.camillecroci.cst3130.coursework1.DAO;

import eu.camillecroci.cst3130.coursework1.Cinema;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Unit test for CinemaDAO
 */
public class CinemaDAOTest
{
    static SessionFactory sessionFactory;

    @BeforeAll
    static void init(){
        try {
            //Create a builder for the standard service registry
            StandardServiceRegistryBuilder standardServiceRegistryBuilder = new StandardServiceRegistryBuilder();

            //Load configuration from hibernate configuration file.
            //Here we are using a configuration file that specifies Java annotations.
            standardServiceRegistryBuilder.configure("hibernate.cfg.xml");

            //Create the registry that will be used to build the session factory
            StandardServiceRegistry registry = standardServiceRegistryBuilder.build();
            try {
                //Create the session factory - this is the goal of the init method.
                sessionFactory = new MetadataSources( registry ).buildMetadata().buildSessionFactory();
            }
            catch (Exception e) {
                    /* The registry would be destroyed by the SessionFactory,
                        but we had trouble building the SessionFactory, so destroy it manually */
                System.err.println("Session Factory build failed.");
                e.printStackTrace();
                StandardServiceRegistryBuilder.destroy( registry );
            }
            //Ouput result
            System.out.println("Session factory built.");
        }
        catch (Throwable ex) {
            // Make sure you log the exception, as it might be swallowed
            System.err.println("SessionFactory creation failed." + ex);
            ex.printStackTrace();
        }
    }

    /**
     * Test the function of getting the cinemas by company names
     */
    @Test
    @DisplayName("Test add screening")
    public void testGetCinemasByCompanyName() {
        SessionFactoryManager sessionFactoryManager = new SessionFactoryManager();
        sessionFactoryManager.setSessionFactory(sessionFactory);

        //Create an instance of the cinemaDAO
        CinemaDAO cinemaDAO = new CinemaDAO();
        cinemaDAO.setSessionFactoryManager(sessionFactoryManager);

        // Create a cinema to add with same random string for name, address, phone and companyName
        // The latest will be used to test this method
        String randomString = String.valueOf(Math.ceil(Math.random() * 10));

        //Get a new session from the session factory manager
        Session session = sessionFactoryManager.getSessionFactory().getCurrentSession();

        //Create a fake cinema
        Cinema cinema = new Cinema();

        //Set its name, address, phone and company name to our fake value
        cinema.setName(randomString);
        cinema.setAddress(randomString);
        cinema.setPhone(randomString);
        cinema.setCompanyName(randomString);
        cinema.setCinemaNameUrl(randomString);
        cinema.setActive(true);

        //Save it to the database
        session.beginTransaction();
        session.save(cinema);
        session.getTransaction().commit();
        session.close();

        //search that cinema by the company name
        List<Cinema> cinemasList = cinemaDAO.getCinemasByCompanyName(randomString);

        if(cinemasList.size()< 0){
            fail("Cinema not found. ");
        }

        session = sessionFactoryManager.getSessionFactory().getCurrentSession();
        //Start transaction
        session.beginTransaction();

        //Delete movie from database
        session.delete(cinemasList.get(0));

        //Commit transaction to save it to database
        session.getTransaction().commit();

        session.close();
    }
}
