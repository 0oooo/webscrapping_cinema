package eu.camillecroci.cst3130.coursework1.webscrapper;

import eu.camillecroci.cst3130.coursework1.Cinema;
import eu.camillecroci.cst3130.coursework1.DAO.CinemaDAO;
import eu.camillecroci.cst3130.coursework1.Movie;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public class WebScrapperCineworld extends WebScrapper {

    private String CINEWORLD_URL_BASE = "https://www.cineworld.co.uk/#/buy-tickets-by-cinema?in-cinema=";
    private String PREORDER = "PRE-ORDER YOUR TICKETS NOW";
    private String JUNIOR_MOVIE = "Movies for Juniors: Discounted ticket price available, for children and accompanying adults. All customers aged 16 or over must be accompanying a child. Children under 14 years of age must be accompanied by an adult.";

    public String getCinemaUrl(String cinemaName){
        return CINEWORLD_URL_BASE + cinemaName;
    }

    private void loadAllImages(List<WebElement> moviesList, WebDriver driver, JavascriptExecutor js){
        WebElement top = driver.findElement(By.className("movie-row"));
            boolean loaded = true;
            do {
                for (WebElement movie : moviesList) {
                    WebElement title = movie.findElement(By.className("qb-movie-name"));

                    List<WebElement> loadedImage = movie.findElements(By.className("v-lazy-loaded"));

                    this.scrollToElement(js, top, movie);
                    for(WebElement img : loadedImage) {
                        System.out.println("AND THE PROOF IT IS ALL LOADED: " + img.getAttribute("src"));
                    }
                    if (loadedImage != null && !loadedImage.equals("")) {
                        loaded = true;
                    } else {
                        loaded = false;
                    }
                }
            } while (loaded != true);
        System.out.println("All loaded ");
    }

    private String getDescription(WebDriver driver, WebDriverWait wait, JavascriptExecutor js, WebElement link){
        js.executeScript("arguments[0].click()", link);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("text-content")));
        String description = this.cleanDescription(driver.findElement(By.className("text-content")).getText());
        return description;
    }

    private String cleanDescription(String description){
        if(description.toLowerCase().contains(JUNIOR_MOVIE.toLowerCase())){
            description = description.substring(description.indexOf(JUNIOR_MOVIE) + JUNIOR_MOVIE.length() + 1 );
        }
        return description;
    }

    private int[] parseTime(String time) {
        int[] parsedTime = new int[2];
        char separatorTime = ':';

        int hour = Integer.parseInt(time.substring(0, time.indexOf(separatorTime)));
        int min = Integer.parseInt(time.substring((time.indexOf(separatorTime) + 1)));

        parsedTime[0] = hour;
        parsedTime[1] = min;
        return parsedTime;
    }

    private void waitThread(){
        try {
            Thread.sleep(2000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void scrapeMovies(String location, Cinema cinema) throws IOException {

        String cineworldUrl  = getCinemaUrl( location);
        ChromeOptions options  = new ChromeOptions();
        options.setHeadless(true);
        WebDriver driver = new ChromeDriver(options);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        WebDriverWait wait = new WebDriverWait(driver, 1);

        System.out.println("----------------- SCRAPPING FOR " + location + " -------------------------");

        Date date = new Date();

        for(int dayIndex = 0; dayIndex < 7; dayIndex++) {

            System.out.println("________________________________ CW NEW DAY___________________________");
            System.out.println("The date is: "  + date);

            driver.get(cineworldUrl);

            try {
                Thread.sleep(2000);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //Wait for page to load
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("main-menu")));


            // Get the list of all the movies
            List<WebElement> moviesList = driver.findElements(By.className("qb-movie"));

            // If there is no movie, we go to the next day (for ex if scraping late at night)
            if (moviesList.size() < 1) {
                return;
            }

            // We are waiting to have the image of the last movie to be loaded to scrap
            this.loadAllImages(moviesList, driver, js);

            for (WebElement movie : moviesList) {


                // We ignore the movies that are not out yet (Pre-order in cineworld)
                String preorderedInfo = movie.findElement(By.className("qb-movie-info-column")).getText();
                if (preorderedInfo.toLowerCase().contains(PREORDER.toLowerCase())) {
                    break;
                }

                //Wait because we still had some issues here with unloaded title
                this.waitThread();

                // Title of the movie
                String title = movie.findElement(By.className("qb-movie-name")).getText();

                //todo find a solution for the description
//                WebElement link = movie.findElement(By.className("qb-movie-link"));
//                String description = this.getDescription(driver, wait, js,  link);
//                js.executeScript("window.history.back()");
//                //Wait because we still had some issues here with unloaded title
//                this.waitThread();
//                // We are waiting to have the image of the last movie to be loaded to scrap again
//                this.loadAllImages(moviesList, driver, js);

                String description = "";

                // URL of the image of the movie
                String imageUrl = movie.findElement(By.className("v-lazy-loaded")).getAttribute("src");

                //Database search for the movie id. It will be added if it's not in the db
                Movie currentMovie = new Movie();
                try {
                    currentMovie = super.addMovie(title, description, imageUrl);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // List of details ("2D" and time of screening)
                List<WebElement> details = movie.findElements(By.className("qb-movie-info-column"));
                for(WebElement detail : details){
                    // There is one screening type (2D) for several times. So we add the same details for all the time
                    // that are under the same class
                    String detailString = detail.findElement(By.className("qb-screening-attributes")).getText();
                    // We go through all the screening times
                    List<WebElement> times = detail.findElements(By.className("btn-lg"));
                    for(WebElement time : times){
                        Date screeningDate;
                        String urlForScreening ;
                        // Each screening time is parsed out of the details, then added to the date.
                        // We also get the url of the screening to be able to use it in our front end (let the user get the click)
                        try {
                            int hour = this.parseTime(time.getText())[0];
                            int min = this.parseTime(time.getText())[1];
                            screeningDate = setTimeForScreeningDate(date, hour, min);
                            urlForScreening = time.getAttribute("data-url");
                        } catch (Exception e) {
                            continue;
                        }
                        // Now we have all the details needed to add our screening in the database.
                        try {
                            super.saveScreeningInDatabase(cinema, currentMovie, screeningDate, detailString, urlForScreening);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            date = super.getNextDate(date, 1);
            int nextDay = dayIndex + 1;
            if(nextDay < 7)
                js.executeScript("document.getElementsByClassName('qb-calendar-widget')[0].querySelectorAll('[data-automation-id]')[" + nextDay + "].click()");
            try {
                Thread.sleep(2000);
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        //Exit driver and close Chrome
        driver.quit();
    }

    public void run(){
        super.init();
        List<Cinema> allCinewold = cinemaDAO.getCinemasByCompanyName("CineWorld");

        for(Cinema cinema : allCinewold){
            if(cinema.isActive()) {
                try {
                    scrapeMovies(cinema.getCinemaNameUrl(), cinema);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
