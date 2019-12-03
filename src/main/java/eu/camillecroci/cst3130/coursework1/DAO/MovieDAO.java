package eu.camillecroci.cst3130.coursework1.DAO;

import eu.camillecroci.cst3130.coursework1.Movie;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.persistence.NoResultException;
import java.util.List;

public class MovieDAO extends AbstractDAO {

    public Movie addMovie(String name, String description, String url) {


        Movie movie = searchMovieByName(name);
        // to avoid putting a movie that is already in the db
        if(movie.getName() == name && movie.getDescription().equals("")){ //trick to replace the movies that have an empty description (from cineworld) with the same with description (from vue)

        } else if(!movie.getName().equalsIgnoreCase("DefaultDefault")){
            return movie;
        }

        Session session = super.getCurrentSession();

        movie.setName(cleanName(name));
        movie.setDescription(description);
        movie.setUrl(url);

        session.beginTransaction();

        session.save(movie);

        session.getTransaction().commit();

        System.out.println("Movie added to database with ID" + movie.getId());

        session.close();

        return movie;
    }

    private String cleanName(String name){
        return name.toLowerCase()
                .replace("'", "")
                .replace(":", "")
                .replace("-", "")
                .replace("  ", " ")
                .replace("unlimited screening", "")
                .replace("movies for juniors", "");
    }

    public Movie searchMovieByName(String name) {

        name = cleanName(name);

        Session session = super.getCurrentSession();

        ApplicationContext context = new ClassPathXmlApplicationContext("Beans.xml");
        Movie movie = (Movie) context.getBean("defaultMovie");

        try {
            session.beginTransaction();

            Query query = session.createQuery("from Movie where name = '" + name + "'");
            List<Movie> movies = query.list();

            if(movies.size() > 0){
                //we return the first result
                movie = movies.get(0);
            }
        } catch (HibernateException e) {
            if (session.getTransaction() != null) session.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            session.close();
            return movie;
        }
    }

    public void shutDown() {
        super.getCurrentSession().close();
    }
}
