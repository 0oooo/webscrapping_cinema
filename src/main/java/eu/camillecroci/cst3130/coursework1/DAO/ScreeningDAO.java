package eu.camillecroci.cst3130.coursework1.DAO;

import eu.camillecroci.cst3130.coursework1.Cinema;
import eu.camillecroci.cst3130.coursework1.Movie;
import eu.camillecroci.cst3130.coursework1.Screening;
import org.hibernate.Session;
import org.hibernate.query.Query;

import javax.persistence.NoResultException;
import java.util.Date;
import java.util.List;

/**
 *
 */

public class ScreeningDAO extends AbstractDAO {

    /**
     * This checks if a screening with the following attributes already exists.
     * @param screeningDate the date and time of a screening
     * @param movie the movie of a screening
     * @param cinema the cinema of a screening
     * @return returns 1 if a screening exists, 0 otherwise.
     */
    public int existsScreening(Date screeningDate, Movie movie, Cinema cinema) {
        Session session = super.getCurrentSession();
        try {
            session.beginTransaction();

            Query query = session.createQuery("select s from Screening as s where s.movie=:movie " +
                    "AND s.cinema=:cinema " +
                    "AND s.screeningDate=:date")
                    .setParameter("movie", movie)
                    .setParameter("cinema", cinema)
                    .setParameter("date", screeningDate);

            List<Screening> screenings = query.list();

            if (screenings.size() <= 0) {
                session.close();
                return 0;
            }
        } catch (NoResultException nre) {
            return 0;
        } catch (Exception e) {
            if (session.getTransaction() != null) session.getTransaction().rollback();
            e.printStackTrace();
        }
        session.close();
        return 1;
    }

    /**
     *
     * @param screeningDate
     * @param movie
     * @param cinema
     * @param details
     * @param url
     * @return
     */

    public int addScreening(Date screeningDate, Movie movie, Cinema cinema, String details, String url) {

        if (existsScreening(screeningDate, movie, cinema) != 0 ) {
            return 0;
        }
        Session session = super.getCurrentSession();
//
        Screening screening = new Screening();
//
        screening.setScreeningDate(screeningDate);
        screening.setMovie(movie);
        screening.setCinema(cinema);
        screening.setDetails(details);
        screening.setUrl(url);

        session.beginTransaction();

        session.save(screening);

        session.getTransaction().commit();

        System.out.println("Screening added:");
        System.out.println("Cinema " + cinema.getCompanyName() + " " + cinema.getName());
        System.out.println(" Movie " + movie.getName());

        session.close();

        return screening.getId();
    }

}
