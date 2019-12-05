package eu.camillecroci.cst3130.coursework1.webscraper;

import eu.camillecroci.cst3130.coursework1.Cinema;
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

public class WebScraperVueCinema extends WebScraper {

    private String VUE_URL_BASE = "https://www.myvue.com/cinema/";

    public String getCinemaUrl(String cinemaName) {
        return VUE_URL_BASE + cinemaName + "/whats-on";
    }

    /**
     * To get all the movies (that are loaded as you scroll)
     * We scroll the page down to the loader until the list of movies stops changing
     */
    private void scrollDown(WebDriver driver, JavascriptExecutor js, WebElement top) {
        WebElement dataloader = driver.findElement(By.id("filmlist__data-loader"));
        List<WebElement> tempList = driver.findElements(By.className("filmlist__item"));
        int oldSize = tempList.size();
        int newSize = 0;
        boolean hasGrown = true;
        while (hasGrown) {

            scrollToElement(js, top, dataloader);

            waitThread(500);
            scrollToElement(js, top, dataloader);

            newSize = tempList.size();
            if (newSize != oldSize) {
                oldSize = newSize;
            } else {
                hasGrown = false;
            }
        }
    }

    /*
     * In Vue Cinema, the images are lazy loaded, so we go through each images
     * And we wait for the data attribute "loaded" to be true before getting the url
     * out of the image
     */
    private void loadAllImages(List<WebElement> moviesList, WebDriver driver, JavascriptExecutor js, WebElement top) {
        for (WebElement movie : moviesList) {
            boolean loaded = true;
            do {
                scrollToElement(js, top, movie);
                WebElement title = movie.findElement(By.className("subtitle"));
                String loadStatus = movie.findElement(By.className("filmlist__poster")).getAttribute("data-loaded");
                if (loadStatus == null || !loadStatus.equalsIgnoreCase("true")) {
                    loaded = false;
                } else if (loadStatus.equalsIgnoreCase("true")) {
                    System.out.println("Image loaded for vue: " + title.getText());
                    loaded = true;
                }
            } while (loaded != true);
        }
    }

    private String parseDetails(String element) {
        String other = element.substring(element.indexOf("\n") + 1);
        other = other.replace("\n", " - ");
        other = other.replace("- -", "- ");
        return other;
    }

    public int[] parseTime(String time) { // "2:30 PM "
        int[] parsedTime = new int[2];
        char separatorTime = ':';
        char separatorMeridiem = 'M';

        int hour = Integer.parseInt(time.substring(0, time.indexOf(separatorTime)));

        int min = Integer.parseInt(time.substring((time.indexOf(separatorTime) + 1), time.indexOf(separatorMeridiem) - 2));

        String meridiem = time.substring(time.indexOf(separatorMeridiem) - 1, time.indexOf(separatorMeridiem));

        //if it's PM, we had 12 to store it more easily in the db (and also because the french do it that (logical) way
        if (meridiem.equalsIgnoreCase("P") && hour != 12) {
            hour = hour + 12;
        }
        parsedTime[0] = hour;
        parsedTime[1] = min;
        return parsedTime;
    }

    public void scrapeMovies(String location, Cinema cinema) throws IOException {

        String vueUrl = getCinemaUrl(location);
        ChromeOptions options = new ChromeOptions();
        options.setHeadless(true);
        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, 1);
        JavascriptExecutor js = (JavascriptExecutor) driver;

        System.out.println("----------------- SCRAPPING FOR " + location + " -------------------------");

        driver.get(vueUrl);

        waitThread(2000);

        //Wait for page to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("whats-on-filters")));

        Date date = new Date();

        WebElement top = driver.findElement(By.id("whats-on-filters"));

        for(int dayIndex = 0; dayIndex < 7; dayIndex++) { //maybe reduce to 6?

            scrollDown(driver, js, top);

            //Output details for individual products
            List<WebElement> moviesList = driver.findElements(By.className("filmlist__inner"));

            //no movie found (maybe it's late at night?)
            if(moviesList.size() < 1){
                return;
            }

            // We are waiting to have the image of the last movie to be loaded to scrap
            loadAllImages(moviesList, driver, js, top);

            System.out.println("________________________________VUE : NEW DAY___________________________");
            System.out.println("The date is: "  + date);

            for (WebElement movie : moviesList) {

                String title = movie.findElement(By.className("subtitle")).getText();

                String description = movie.findElement(By.className("filmlist__synopsis")).getText();

                String imageUrl = movie.findElement(By.className("filmlist__poster")).getAttribute("src");

                Movie currentMovie = new Movie();
                //Database search for the movie id. It will be added if it's not in the db
                try {
                    currentMovie = super.addMovie(title, description, imageUrl);
                } catch (NullPointerException NPE){
                    continue;
                }
                catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Problem when getting new movie from the database");
                    System.out.println("Title = " + title);
                    System.out.println("Description id = " + description);
                    System.out.println("Image Url = " + imageUrl);
                    System.out.println("Final ID of movie? = " + currentMovie.getId());
                }

                WebElement times = movie.findElement(By.className("filmlist__times__inner"));
                List<WebElement> details = times.findElements(By.className("small"));
                //for each movie we will get a list of hours + added details that goes with the screening

                // Clean the array
                for(int i = 0; i < details.size(); i++){
                    String aa = details.get(i).getText();
                    if(details.get(i).getText() == null || details.get(i).getText().equalsIgnoreCase("")){
                        details.remove(details.get(i));
                    }
                }



                for (WebElement detail : details) {
                    Date screeningDate;
                    String detailPerScreening;
                    String urlForScreening ;
                    try {
                        int hour = parseTime(detail.getText())[0];
                        int min = parseTime(detail.getText())[1];
                        screeningDate = super.setTimeForScreeningDate(date, hour, min);
                        detailPerScreening = parseDetails(detail.getText());
                        urlForScreening = movie.findElement(By.className("small")).getAttribute("href");
                    } catch (Exception e) {
                        continue;
                    }

                    //Each "detail" from the list is a new screening so we are saving a new one everytime
                    try {
                        System.out.println("ATTEMPTING TO SAVE A SCREENING FOR VUE");
                        super.saveScreeningInDatabase(cinema, currentMovie, screeningDate, detailPerScreening, urlForScreening);
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("Problem when saving new screening in the database");
                        System.out.println("Cinema id = " + cinema.getId());
                        System.out.println("Movie id = " + currentMovie);
                        System.out.println("Screening date = " + detailPerScreening);
                        System.out.println("Url = " + urlForScreening);
                    }
                }
            }
            goNextDay(js, dayIndex);
        }

        //Exit driver and close Chrome
        driver.quit();
    }

    public void run() {
        List<Cinema> allVueCinema = cinemaDAO.getCinemasByCompanyName("Vue");
        for (Cinema cinema : allVueCinema) {
            if(cinema.isActive()) {
                try {
                    scrapeMovies(cinema.getCinemaNameUrl(), cinema);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
