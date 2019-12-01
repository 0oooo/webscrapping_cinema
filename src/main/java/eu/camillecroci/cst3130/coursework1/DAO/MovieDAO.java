package eu.camillecroci.cst3130.coursework1.DAO;

import eu.camillecroci.cst3130.coursework1.Movie;
import org.hibernate.HibernateException;
import org.hibernate.Session;

public class MovieDAO extends AbstractDAO {


    public void addMovie(String name, String description, String url) {

        // to avoid putting a movie that is already in the db
        if(searchMovieIdByName(name) != 0){
            return;
        }

        Session session = super.getCurrentSession();

        Movie movie = new Movie();
        movie.setName(escapeSpecialCharacter(name));
        movie.setDescription(description);
        movie.setUrl(url);

        session.beginTransaction();

        session.save(movie);

        session.getTransaction().commit();

        System.out.println("Movie added to database with ID" + movie.getId());

        session.close();
    }

    private String escapeSpecialCharacter(String name){
        return name.replace("'", "");
    }

    public int searchMovieIdByName(String name) {

       name = escapeSpecialCharacter(name);

        Session session = super.getCurrentSession();

        Movie movie = new Movie();

        try {
            session.beginTransaction();
            System.out.println("I am searching for: " + name);
            //NB: for search you need to use the class name and class property (here Cinema with a capital and name, not cinema_name
//            List<Cinema> cinemasList = session.createQuery("from Cinema where name = \'" + name + "\'").getResultList();
            movie = (Movie) session.createQuery("from Movie where name = '" + name + "'").getSingleResult();
            System.out.println("I have found " + movie.getName());
        } catch (HibernateException e) {
            if (session.getTransaction() != null) session.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            session.close();
            return movie.getId();
        }
    }
    public void shutDown() {
        super.getCurrentSession().close();

    }
}
