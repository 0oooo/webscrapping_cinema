package eu.camillecroci.cst3130.coursework1.webscrapper;

import eu.camillecroci.cst3130.coursework1.Cinema;
import eu.camillecroci.cst3130.coursework1.DAO.CinemaDAO;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WebScrapperCineworld extends WebScrapper {

    private String CINEWORLD_URL_BASE = "https://www.cineworld.co.uk/#/buy-tickets-by-cinema?in-cinema=";
    private String PREORDER = "PRE-ORDER YOUR TICKETS NOW";

    public WebScrapperCineworld(){}

    public String getCinemaUrl(String cinemaName){
        return CINEWORLD_URL_BASE + cinemaName;
    }

    private void loadAllImages(List<WebElement> moviesList, WebDriver driver, JavascriptExecutor js, WebElement top){

            boolean loaded = true;
            do {
                for (WebElement movie : moviesList) {
                    WebElement title = movie.findElement(By.className("qb-movie-name"));
                    List<WebElement> loadedImage = movie.findElements(By.className("v-lazy-loaded"));

                    this.scrollToElement(driver, js, top, movie);
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


    private int[] parseTime(String time){
        int[] parsedTime = new int[2];
        char separatorTime = ':';

        int hour = Integer.parseInt(time.substring(0, time.indexOf(separatorTime)));

        int min = Integer.parseInt(time.substring((time.indexOf(separatorTime) + 1)));

        parsedTime[0] = hour;
        parsedTime[1] = min;
        return parsedTime;
    }

    private void getAllDescriptions(ArrayList<String> descriptionUrls, WebDriver driver, WebDriverWait wait){
        for(String url: descriptionUrls){
            if(url == null || url == ""){
                break;
            }
            driver.get(url);
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("text-content")));
            String description = driver.findElement(By.className("text-content")).getText();
            descriptions.add(description);
            System.out.println("Description : " + description);
        }
    }

    public void run(){
        super.init();
        CinemaDAO cinemaDAO = new CinemaDAO();
        cinemaDAO.init();
        List<Cinema> allCinewold = cinemaDAO.getCinemasByCompanyName("CineWorld");

        for(Cinema cinema : allCinewold){
            try {
                scrapeMovies(cinema.getCinemaNameUrl());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //debug method if there is a problem with one cinema in particular
    public void runForOne(String cinema){
        try{
            scrapeMovies(cinema);
        } catch (IOException e){
            e.printStackTrace();
        }
    }


    public void scrapeMovies(String location) throws IOException {

        String cineworldUrl  = getCinemaUrl( location);

        System.out.println("----------------- SCRAPPING FOR " + location + " -------------------------");

        ChromeOptions options  = new ChromeOptions();
        options.setHeadless(true);

        WebDriver driver = new ChromeDriver(options);

        JavascriptExecutor js = (JavascriptExecutor) driver;

        WebDriverWait wait = new WebDriverWait(driver, 1);

        driver.get(cineworldUrl);

        System.out.println("++++++++++++++ URL =  " + cineworldUrl + " +++++++++++++++++++");

        //Wait for page to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("main-menu")));

        String page  = driver.getPageSource();

        WebElement top = driver.findElement(By.className("main-menu"));

        //Output details for individual products
        List<WebElement> moviesList = driver.findElements(By.className("qb-movie"));

        //no movie found (maybe it's late at night?)
        if(moviesList.size() < 1){
            return;
        }

        // We are waiting to have the image of the last movie to be loaded to scrap
        this.loadAllImages(moviesList, driver, js, top);


        // Each movie description is on another page, so in a first time we store those url
        // to then explore them one by one and get the description.
        ArrayList<String> descriptionUrls = new ArrayList<String>();

        for (WebElement movie : moviesList) {

            /*
             * Some movies are displayed in cineworld but not out yet: to ignore
             */
            WebElement preorderedInfo = movie.findElement(By.className("qb-movie-info-column"));
            String testPreorder = preorderedInfo.getText();
            if(testPreorder.toLowerCase().contains(PREORDER.toLowerCase())){
                break;
            }

            String descriptionUrl = movie.findElement(By.className("qb-movie-link")).getAttribute("href");
            descriptionUrls.add(descriptionUrl);

            // Title of the movie
            WebElement title = movie.findElement(By.className("qb-movie-name"));
            titles.add(title.getText());

            // URL of the image of the movie
            WebElement imageUrl = movie.findElement(By.className("v-lazy-loaded"));
            imgUrls.add(imageUrl.getAttribute("src"));

            // List of details
            List<WebElement>  details  = movie.findElements(By.className("qb-screening-attributes"));

            List<WebElement> times = movie.findElements(By.className("btn-lg"));

            ArrayList<Integer> hours = new ArrayList<Integer>();
            ArrayList<Integer> minutes = new ArrayList<Integer>();
            ArrayList<String> screeningUrls = new ArrayList<String>();
            for( WebElement time : times){
                hours.add(this.parseTime(time.getText())[0]);
                minutes.add(this.parseTime(time.getText())[1]);
                screeningUrls.add(time.getAttribute("data-url"));
            }

            allHours.add(hours);
            allMinutes.add(minutes);
            bookingUrls.add(screeningUrls);

            System.out.println("Title: " + title.getText());
            System.out.println("Image Url: " + imageUrl.getAttribute("src"));
            for(int i = 0; i < hours.size(); i++){
                System.out.println("Dets: " + hours.get(i) + ":" + minutes.get(i));
                System.out.println("Url : " + screeningUrls.get(i));
            }
        }

        this.getAllDescriptions(descriptionUrls, driver, wait);

        //Exit driver and close Chrome
        driver.quit();
    }

    public void test(){
        ArrayList<String> titles = super.titles;
        ArrayList<String> descriptions = super.descriptions;
        ArrayList<String> imgUrls = super.imgUrls;
        ArrayList<ArrayList<String>> allDetails = super.allDetails;
        ArrayList<ArrayList<Integer>> allHours = super.allHours;
        ArrayList<ArrayList<Integer>> allMinutes = super.allMinutes;
        ArrayList<ArrayList<String>> bookingUrls = super.bookingUrls;
    }
}
