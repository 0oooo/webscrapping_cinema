package eu.camillecroci.cst3130.coursework1.webscrapper;

import eu.camillecroci.cst3130.coursework1.Cinema;
import eu.camillecroci.cst3130.coursework1.DAO.CinemaDAO;
import eu.camillecroci.cst3130.coursework1.DAO.MovieDAO;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WebScrapperVueCinema extends WebScrapper {

    private String VUE_URL_BASE = "https://www.myvue.com/cinema/";

    public WebScrapperVueCinema() {
    }

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

            this.scrollToElement(driver, js, top, dataloader);

            try {
                Thread.sleep(500);
            } catch (Exception e) {
                e.printStackTrace();
            }
            scrollToElement(driver, js, top, dataloader);

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
                this.scrollToElement(driver, js, top, movie);
                WebElement title = movie.findElement(By.className("subtitle"));
                String loadStatus = movie.findElement(By.className("filmlist__poster")).getAttribute("data-loaded");
                System.out.println(title.getText() + " => data loaded: " + loadStatus);
                if (loadStatus == null || !loadStatus.equalsIgnoreCase("true")) {
                    loaded = false;
                } else if (loadStatus.equalsIgnoreCase("true")) {
                    loaded = true;
                }
                System.out.println("Loaded the bool = " + loaded);
            } while (loaded != true);
        }
        System.out.println("All loaded ");
    }

    private int[] parseTime(String time) {
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

    private String parseDetails(String element) {
        String parsedDetails = "";
        String separator = "\n";

        String other = element.substring(element.indexOf("\n") + 1);

        other = other.replace("\n", " - ");
        other = other.replace("- -", "- ");

        return other;
    }

    public void run() {
        super.init();
        List<Cinema> allVueCinema = cinemaDAO.getCinemasByCompanyName("Vue");

        for (Cinema cinema : allVueCinema) {
            if(cinema.isActive()) {
                try {
                    scrapeMovies(cinema.getCinemaNameUrl());
                    super.saveMovieInDatabase();
                } catch (Exception e) {
                    e.printStackTrace();

                }
            }
        }
    }

    public void scrapeMovies(String location) throws IOException {

        String vueUrl = getCinemaUrl(location);

        System.out.println("----------------- SCRAPPING FOR " + location + " -------------------------");

        ChromeOptions options = new ChromeOptions();
        options.setHeadless(true);

        WebDriver driver = new ChromeDriver(options);

        WebDriverWait wait = new WebDriverWait(driver, 1);

        JavascriptExecutor js = (JavascriptExecutor) driver;

        driver.get(vueUrl);

        System.out.println("++++++++++++++ URL =  " + vueUrl + " +++++++++++++++++++");

        //Wait for page to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("whats-on-filters")));

        String page  = driver.getPageSource();

        WebElement top = driver.findElement(By.id("whats-on-filters"));

        this.scrollDown(driver, js, top);

        //Output details for individual products
        List<WebElement> moviesList = driver.findElements(By.className("filmlist__item"));

        //no movie found (maybe it's late at night?)
        if(moviesList.size() < 1){
            return;
        }

        // We are waiting to have the image of the last movie to be loaded to scrap
        this.loadAllImages(moviesList, driver, js, top);

        for (WebElement movie : moviesList) {
            WebElement title = movie.findElement(By.className("subtitle"));
            titles.add(title.getText());

            WebElement description = movie.findElement(By.className("filmlist__synopsis"));
            descriptions.add(description.getText());

            WebElement imageUrl = movie.findElement(By.className("filmlist__poster"));
            imgUrls.add(imageUrl.getAttribute("src"));

            List<WebElement> details = movie.findElements(By.className("small"));
            //for each movie we will get a list of hours + added details that goes with the screening

            // Trick to fix an issue with the last element of details being empty sometimes
            String lastElement = details.get(details.size() - 1).getText();
            if (lastElement == null || lastElement.equalsIgnoreCase("")) {
                details.remove(details.size() - 1);
            }

            ArrayList<Integer> hours = new ArrayList<Integer>();
            ArrayList<Integer> minutes = new ArrayList<Integer>();
            ArrayList<String> detailsPerScreening = new ArrayList<String>();
            ArrayList<String> screeningUrls = new ArrayList<String>();

            for (WebElement detail : details) {
                hours.add(this.parseTime(detail.getText())[0]);
                minutes.add(this.parseTime(detail.getText())[1]);
                detailsPerScreening.add(this.parseDetails(detail.getText()));
                screeningUrls.add(movie.findElement(By.className("small")).getAttribute("href"));
            }

            allHours.add(hours);
            allMinutes.add(minutes);
            allDetails.add(detailsPerScreening);
            bookingUrls.add(screeningUrls);


            System.out.println("Title: " + title.getText());
            System.out.println("Descrition: " + description.getText());
            System.out.println("Image Url: " + imageUrl.getAttribute("src"));
            for (int i = 0; i < hours.size(); i++) {
                System.out.println("Dets: " + hours.get(i) + ":" + minutes.get(i) + " - " + detailsPerScreening.get(i));
                System.out.println("Screening url: " + screeningUrls.get(i));
            }

        }

        //Exit driver and close Chrome
        driver.quit();
    }

    private void saveInDatabase(){

    }

    private void saveMovieInDatabase(String name, String detail, String description, String imgUrl){

    }

    private void test(){
        ArrayList<String> titles = super.titles;
        ArrayList<String> descriptions = super.descriptions;
        ArrayList<String> imgUrls = super.imgUrls;
        ArrayList<ArrayList<String>> allDetails = super.allDetails;
        ArrayList<ArrayList<Integer>> allHours = super.allHours;
        ArrayList<ArrayList<Integer>> allMinutes = super.allMinutes;
        ArrayList<ArrayList<String>> bookingUrls = super.bookingUrls;
    }

}
