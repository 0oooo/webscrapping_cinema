package eu.camillecroci.cst3130.coursework1.webscraper;

import eu.camillecroci.cst3130.coursework1.Cinema;
import eu.camillecroci.cst3130.coursework1.DAO.CinemaDAO;
import eu.camillecroci.cst3130.coursework1.DAO.MovieDAO;
import eu.camillecroci.cst3130.coursework1.DAO.ScreeningDAO;
import eu.camillecroci.cst3130.coursework1.Movie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import java.util.Calendar;
import java.util.Date;

public class WebScraper extends Thread {

    protected CinemaDAO cinemaDAO;
    protected MovieDAO movieDAO;
    protected ScreeningDAO screeningDAO;

    public CinemaDAO getCinemaDAO() {
        return cinemaDAO;
    }

    public void setCinemaDAO(CinemaDAO cinemaDAO) {
        this.cinemaDAO = cinemaDAO;
    }

    public MovieDAO getMovieDAO() {
        return movieDAO;
    }

    public void setMovieDAO(MovieDAO movieDAO) {
        this.movieDAO = movieDAO;
    }

    public ScreeningDAO getScreeningDAO() {
        return screeningDAO;
    }

    public void setScreeningDAO(ScreeningDAO screeningDAO) {
        this.screeningDAO = screeningDAO;
    }

    protected void scrollToElement(JavascriptExecutor js, WebElement top, WebElement element) {
        js.executeScript("arguments[0].scrollIntoView();", element);
        try {
            Thread.sleep(500);
        } catch (Exception e) {
            e.printStackTrace();
        }
        js.executeScript("arguments[0].scrollIntoView();", top);
        try {
            Thread.sleep(500);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected Date setTimeForScreeningDate(Date date, int hour, int minutes) {
        date.setHours(hour);
        date.setMinutes(minutes);
        date.setSeconds(00);
        return date;
    }

    protected Date getNextDate(Date currDate, int amount) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(currDate);

        // manipulate date
        cal.add(Calendar.DATE, amount);

        // convert calendar to date
        Date modifiedDate = cal.getTime();
        return modifiedDate;
    }


    protected synchronized Movie addMovie(String title, String description, String imgUrl) {
        return movieDAO.addMovie(title, description, imgUrl);
    }

    protected synchronized void saveScreeningInDatabase(Cinema cinema, Movie movie, Date screeningDate, String details, String url) {
        screeningDAO.addScreening(screeningDate, movie, cinema, details, url);
    }

}
