package eu.camillecroci.cst3130.coursework1.DAO;

import eu.camillecroci.cst3130.coursework1.Movie;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Set;

public class MovieDAO extends AbstractDAO {

    /**
     * Deviation of the main addMovie method for testing purposes:
     * This add movie but does not search if the movie is already in the database
     * @param name
     * @param description
     * @param url
     * @return
     */
    public Movie addMovieNoSearch(String name, String description, String url) {

        Movie movie = new Movie();

        Session session = super.getCurrentSession();

        movie.setName(cleanTitle(name));
        movie.setDescription(description);
        movie.setUrl(url);

        session.beginTransaction();

        session.save(movie);

        session.getTransaction().commit();

        System.out.println("Movie added to database with ID" + movie.getId());

        session.close();

        return movie;
    }

    public Movie addMovie(String name, String description, String url) {

        Movie searchedMovie = searchMovieByTitle(name);

        if(searchedMovie != null){
            return searchedMovie;
        }
        Movie movie = new Movie();

        Session session = super.getCurrentSession();

        movie.setName(cleanTitle(name));
        movie.setDescription(description);
        movie.setUrl(url);

        session.beginTransaction();

        session.save(movie);

        session.getTransaction().commit();

        System.out.println("Movie added to database with ID" + movie.getId());

        session.close();

        return movie;
    }

    public String cleanTitle(String title){
        title =  title.toLowerCase()
                .replace(" ii", " 2") // should find a way to make that more automatic
                .replace("'", "_")
                .replace(":", "")
                .replace("-", "")
                .replace("  ", " ")
                .replace("unlimited screening", "")
                .replace("movies for juniors", "")
                .replace(" ", "_");

        if ( title.charAt(title.length() - 1) == '_')
            title = title.substring(0, title.length() - 1);

        return title;
    }

    public Movie searchMovieByTitle(String title) {
        //remove all the special characters of titles to avoid entering the same movie twice
        title = cleanTitle(title);

        Session session = super.getCurrentSession();
        Movie movie = new Movie();

        try {
            session.beginTransaction();

            // search if we already have a movie in the db with this title
            Query query = session.createQuery("from Movie where name = '" + title + "'");
            List<Movie> movies = query.list();

            if(movies.size() > 0){
                //we return the first result
                movie = movies.get(0);
            } else{
                movie = null;
            }
        } catch (HibernateException e) {
            if (session.getTransaction() != null) session.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            session.close();
            return movie;
        }
    }

    public void addDescription(Set<String[]> allTitlesToDescriptionUrls){

        for(String[] titleToDescriptionUrl : allTitlesToDescriptionUrls){
            String title = titleToDescriptionUrl[0];
            title = cleanTitle(title);

            Movie searchedMovie = searchMovieByTitle(title);

            if(searchedMovie == null) {
                throw new NullPointerException("There is no movie we can add a description to");
            }

            if(searchedMovie.getDescription() == null || searchedMovie.getDescription().equals("")){
                String description = titleToDescriptionUrl[1];

                Session session = super.getCurrentSession();
                session.beginTransaction();
                Movie movieToUpdate = (Movie) session.get(Movie.class, searchedMovie.getId());
                movieToUpdate.setDescription(description);
                session.update(movieToUpdate);
                session.getTransaction().commit();
                System.out.println("Description added for movie : " + searchedMovie.getName());
                session.close();
            }
        }
    }

    public void shutDown() {
        super.getCurrentSession().close();
    }
}
