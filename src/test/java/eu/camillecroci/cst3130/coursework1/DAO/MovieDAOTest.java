package eu.camillecroci.cst3130.coursework1.DAO;

import eu.camillecroci.cst3130.coursework1.Movie;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * Unit test for ScreeningDAO class.
 */
@DisplayName("Test screening DAO")
public class MovieDAOTest
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
     * Test the function of adding a movie by title
     */
    @Test
    @DisplayName("Test add movie")
    public void testAddMovie() {
        SessionFactoryManager sessionFactoryManager = new SessionFactoryManager();
        sessionFactoryManager.setSessionFactory(sessionFactory);

        //Create an instance of the screeningDAO
        MovieDAO movieDAO = new MovieDAO();
        movieDAO.setSessionFactoryManager(sessionFactoryManager);

        //Create a mock movie with random title, description and url
        String randomString = String.valueOf(Math.random());

        Movie mockMovie = movieDAO.addMovieNoSearch(randomString, randomString, randomString);

        //Get a new session from the session factory manager
        Session session = sessionFactoryManager.getSessionFactory().getCurrentSession();

        //Start transaction
        session.beginTransaction();

        //Get movie with random name
        List<Movie>  movieList = session.createQuery("from Movie where name = '" + randomString +"'").getResultList();
        if(movieList.size() < 1){
            fail("Movie not successfully stored. Movie list size: " + movieList.size());
        }

        //Delete movie from database
        session.delete(movieList.get(0));

        //Commit transaction to save it to database
        session.getTransaction().commit();

        session.close();
    }


    /**
     * Test the function to add a movie in the database
     */
    @Test
    @DisplayName("Test search movie")
    public void testSearchMovie()
    {
        SessionFactoryManager sessionFactoryManager = new SessionFactoryManager();
        sessionFactoryManager.setSessionFactory(sessionFactory);

        //Create an instance of the screeningDAO
        MovieDAO movieDAO = new MovieDAO();
        movieDAO.setSessionFactoryManager(sessionFactoryManager);

        //Create a movie with random title, description and url
        String randomString = String.valueOf(Math.random());

        // Add that movie to the database
        // As it is using a method that was previously tested,
        // if there is a problem with that inner function it will be shown somewhere else
        Movie mockMovie = movieDAO.addMovieNoSearch(randomString, randomString, randomString);

        //Search our mock movie we previously added to the database
        Movie searchedMovie = movieDAO.searchMovieByTitle(randomString);
        if( searchedMovie.getId() != mockMovie.getId()){
            fail("Movie not found. Mock movie =  " + mockMovie.getId() +
                    " with name " + mockMovie.getName() +
                    " but searched movie = " + searchedMovie.getId() +
                    " with name " + searchedMovie.getName());
        }

        //Then search a movie that we did not add to the database

        //Create mock movie
        Movie mockMovieNotAdded = new Movie();

        //Create a new random title, description, and url
        String randomString2 = String.valueOf(Math.random());
        mockMovieNotAdded.setName(randomString2);
        mockMovieNotAdded.setDescription(randomString2);
        mockMovieNotAdded.setUrl(randomString2);

        //Search for that movie that is NOT in the database
        Movie searchedAbsentMovie = movieDAO.searchMovieByTitle(randomString2);
        if(searchedAbsentMovie != null){
            fail("The search returned a movie with id " + searchedAbsentMovie.getId() +
                    " when it should have returned null.");
        }

        //Get a new session from the session factory manager
        Session session = sessionFactoryManager.getSessionFactory().getCurrentSession();

        //Start transaction
        session.beginTransaction();

        //Delete movie from database
        session.delete(mockMovie);

        //Commit transaction to save it to database
        session.getTransaction().commit();

        session.close();
    }

    /**
     * Test the function to clean a title
     */
    @Test
    @DisplayName("Test clean title")
    public void testCleanTitle(){

        String stringToBeCleaned = "Hello ii ' : -  unlimited screening movies for juniors";
        String expectedString = "hello_2____";

        SessionFactoryManager sessionFactoryManager = new SessionFactoryManager();
        sessionFactoryManager.setSessionFactory(sessionFactory);

        //Create an instance of the screeningDAO
        MovieDAO movieDAO = new MovieDAO();
        movieDAO.setSessionFactoryManager(sessionFactoryManager);

        String cleanedTestedString = movieDAO.cleanTitle(stringToBeCleaned);

        if(!expectedString.equals(cleanedTestedString)){
            fail("The string do not match. Cleaning result: " + cleanedTestedString +
                    " and expected result: " + expectedString + ".");
        }

    }


}
