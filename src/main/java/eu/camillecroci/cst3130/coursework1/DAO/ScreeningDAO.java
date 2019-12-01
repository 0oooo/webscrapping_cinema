package eu.camillecroci.cst3130.coursework1.DAO;

import eu.camillecroci.cst3130.coursework1.Cinema;
import eu.camillecroci.cst3130.coursework1.Movie;
import eu.camillecroci.cst3130.coursework1.Screening;
import org.hibernate.Session;

import java.sql.Date;

public class ScreeningDAO extends AbstractDAO {

    public void addScreening(Date screeningDate, Movie movie, Cinema cinema, String details) {
        Session session = super.getCurrentSession();

        Screening screening = new Screening();

        screening.setScreeningDate(screeningDate);
        screening.setMovie(movie);
        screening.setCinema(cinema);

        session.beginTransaction();

        session.save(screening);

        session.getTransaction().commit();

        System.out.println("Screening added to database with ID" + screening.getId());

        session.close();
    }

}
