package eu.camillecroci.cst3130.coursework1.webscrapper;

import eu.camillecroci.cst3130.coursework1.Cinema;
import eu.camillecroci.cst3130.coursework1.DAO.CinemaDAO;
import eu.camillecroci.cst3130.coursework1.DAO.MovieDAO;
import eu.camillecroci.cst3130.coursework1.DAO.ScreeningDAO;
import eu.camillecroci.cst3130.coursework1.Movie;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Calendar;
import java.util.Date;

public class WebScrapper extends Thread {

    protected CinemaDAO cinemaDAO;
    protected MovieDAO movieDAO;
    protected ScreeningDAO screeningDAO;


    public void init() {

        ApplicationContext context = new ClassPathXmlApplicationContext("Beans.xml");

        cinemaDAO = (CinemaDAO) context.getBean("myCinemaDAO");
        movieDAO = (MovieDAO) context.getBean("myMovieDAO");
        screeningDAO = (ScreeningDAO) context.getBean("myScreeningDAO");
    }

    protected void scrollToElement(WebDriver driver, JavascriptExecutor js, WebElement top, WebElement element) {
        WebElement footer = driver.findElements(By.className("footer")).get(0);
        js.executeScript("arguments[0].scrollIntoView();", footer);
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
        js.executeScript("arguments[0].scrollIntoView();", element);
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
