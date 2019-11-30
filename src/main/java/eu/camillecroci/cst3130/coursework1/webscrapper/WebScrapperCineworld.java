package eu.camillecroci.cst3130.coursework1.webscrapper;

import eu.camillecroci.cst3130.coursework1.Cinema;
import eu.camillecroci.cst3130.coursework1.DAO.CinemaDAO;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WebScrapperCineworld extends WebScrapper {

    private String CINEWORLD_URL_BASE = "https://www.cineworld.co.uk/#/buy-tickets-by-cinema?in-cinema=";
    private String PREORDER = "PRE-ORDER YOUR TICKETS NOW";

    public WebScrapperCineworld(){}

    public String getCinemaUrl(String cinemaName){
        CinemaDAO cinemaDAO = new CinemaDAO();
        cinemaDAO.init();
        Cinema cinema = cinemaDAO.searchCinemaByName(cinemaName);

        String cinemaUrl = "";
        String cinemaNameUrl = cinema.getCinemaNameUrl();

        cinemaUrl = CINEWORLD_URL_BASE + cinemaNameUrl;

        return cinemaUrl;
    }

    private void loadAllImages(List<WebElement> moviesList, WebDriver driver, JavascriptExecutor js){
        WebElement top = driver.findElement(By.className("main-menu"));

            boolean loaded = true;
            do {
                for (WebElement movie : moviesList) {
                    WebElement title = movie.findElement(By.className("qb-movie-name"));
                    List<WebElement> loadedImage = movie.findElements(By.className("v-lazy-loaded"));

                    this.scrollToElement(driver, js, top, movie);

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

    public void run(){
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


    public void scrapeMovies(String location) throws IOException {

        String cineworldUrl  = getCinemaUrl( location);

        ChromeOptions options  = new ChromeOptions();
        options.setHeadless(true);

        WebDriver driver = new ChromeDriver(options);

        JavascriptExecutor js = (JavascriptExecutor) driver;

        driver.get(cineworldUrl);

        //Wait for page to load
        try {
            Thread.sleep(3000);
        }
        catch(Exception ex){
            ex.printStackTrace();
        }

        //Output details for individual products
        List<WebElement> moviesList = driver.findElements(By.className("qb-movie"));

        String[] descriptionUrls = new String[moviesList.size()];
        int descriptionUrlIndex = 0;

        // We are waiting to have the image of the last movie to be loaded to scrap
        this.loadAllImages(moviesList, driver, js);

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
            descriptionUrls[descriptionUrlIndex] = descriptionUrl;
            descriptionUrlIndex++;

            // Title of the movie
            WebElement title = movie.findElement(By.className("qb-movie-name"));

            // URL of the image of the movie
            WebElement imageUrl = movie.findElement(By.className("v-lazy-loaded"));
            // List of details
            List<WebElement>  details  = movie.findElements(By.className("qb-screening-attributes"));

            List<WebElement> times = movie.findElements(By.className("btn-lg"));

            int[] hours = new int[times.size()];
            int[] minutes = new int[times.size()];
            String[] screeningUrl = new String[times.size()];
            int index = 0;
            for( WebElement time : times){
                hours[index] = this.parseTime(time.getText())[0];
                minutes[index] = this.parseTime(time.getText())[1];
                screeningUrl[index] = time.getAttribute("data-url");
                index++;
            }

            System.out.println("Title: " + title.getText());
//            System.out.println("Descrition: " + description);
            System.out.println("Image Url: " + imageUrl.getAttribute("src"));
            for(int i = 0; i < hours.length; i++){
                System.out.println("Dets: " + hours[i] + ":" + minutes[i]);
                System.out.println("Url : " + screeningUrl[i]);
            }

        }
        List<String> descriptionsMovies = new ArrayList<>();
        for(String url: descriptionUrls){
            if(url == null || url == ""){
                break;
            }
            driver.get(url);
            try {
                Thread.sleep(3000);
            }
            catch(Exception ex){
                ex.printStackTrace();
            }
            String description = driver.findElement(By.className("text-content")).getText();
            descriptionsMovies.add(description);
            System.out.println("Description : " + description);
        }

        //Exit driver and close Chrome
        driver.quit();
    }
}
