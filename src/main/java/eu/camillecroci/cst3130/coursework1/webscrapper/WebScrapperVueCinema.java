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
import java.util.List;

public class WebScrapperVueCinema extends WebScrapper {

    private String VUE_URL_BASE = "https://www.myvue.com/cinema/";

    public WebScrapperVueCinema(){}

    public WebScrapperVueCinema(boolean scrape){
        try {
            scrapeMovies();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getCinemaUrl(String cinemaName){
        CinemaDAO cinemaDAO = new CinemaDAO();
        cinemaDAO.init();
        Cinema cinema = cinemaDAO.searchCinemaByName(cinemaName);

        String cinemaUrl = "";
        String cinemaNameUrl = cinema.getCinemaNameUrl();

        cinemaUrl = VUE_URL_BASE + cinemaNameUrl + "/whats-on";

        return cinemaUrl;
    }

    /**
     * To get all the movies (that are loaded as you scroll)
     * We scroll the page down to the loader until the list of movies stops changing
     */
    private void scrollDown(WebDriver driver, JavascriptExecutor js, WebElement top){
        WebElement dataloader = driver.findElement(By.id("filmlist__data-loader"));
        List<WebElement> tempList = driver.findElements(By.className("filmlist__item"));
        int oldSize = tempList.size();
        int newSize = 0;
        boolean hasGrown = true;
        while(hasGrown){

            this.scrollToElement(driver, js, top, dataloader);

            try {
                Thread.sleep(500);
            } catch (Exception e) {
                e.printStackTrace();
            }
            scrollToElement(driver, js, top, dataloader);

            newSize = tempList.size();
            if(newSize != oldSize){
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
    private void loadAllImages(List<WebElement> moviesList, WebDriver driver, JavascriptExecutor js,  WebElement top){

        for (WebElement movie : moviesList) {
            boolean loaded = true;
            do {
                this.scrollToElement(driver, js, top,  movie);
                WebElement title = movie.findElement(By.className("subtitle"));
                WebElement imageUrl = movie.findElement(By.className("filmlist__poster"));
                String loadStatus = imageUrl.getAttribute("data-loaded");
                System.out.println(title.getText() + " => data loaded: " + loadStatus);
                if (loadStatus == null || !loadStatus.equalsIgnoreCase("true")) {
                    loaded = false;
                } else if(loadStatus.equalsIgnoreCase("true")){
                    loaded = true;
                }
                System.out.println("Loaded the bool = " + loaded);
            } while (loaded != true);
        }
        System.out.println("All loaded ");
    }

    private int[] parseTime(String time){
        int[] parsedTime = new int[2];
        char separatorTime = ':';
        char separatorMeridiem = 'M';

        int hour = Integer.parseInt(time.substring(0, time.indexOf(separatorTime)));

        int min = Integer.parseInt(time.substring((time.indexOf(separatorTime) + 1), time.indexOf(separatorMeridiem) - 2));

        String meridiem = time.substring(time.indexOf(separatorMeridiem) - 1, time.indexOf(separatorMeridiem) );

        //if it's PM, we had 12 to store it more easily in the db (and also because the french do it that (logical) way
        if(meridiem.equalsIgnoreCase("P") && hour != 12){
            hour = hour + 12;
        }
        parsedTime[0] = hour;
        parsedTime[1] = min;
        return parsedTime;
    }

    private String parseDetails(String element){
        String parsedDetails = "";
        String separator = "\n";

        String other = element.substring(element.indexOf("\n") + 1);

        other = other.replace("\n", " - ");
        other = other.replace("- -", "- ");

        return other;
    }

    public void scrapeMovies() throws IOException {
        CinemaDAO cinemaDAO = new CinemaDAO();
        cinemaDAO.init();
        List<Cinema> allVueCinema = cinemaDAO.getCinemasByCompanyName("Vue");

        String vueUrl = getCinemaUrl( "Westfield (Sheperds Bush)");

       ChromeOptions options  = new ChromeOptions();
       options.setHeadless(true);

       WebDriver driver = new ChromeDriver(options);

       JavascriptExecutor js = (JavascriptExecutor) driver;

       driver.get(vueUrl);

        //Wait for page to load
        try {
            Thread.sleep(3000);
        }
        catch(Exception ex){
            ex.printStackTrace();
        }

        driver.getPageSource();

        WebElement top = driver.findElement(By.id("whats-on-filters"));

        this.scrollDown(driver, js, top);

        //Output details for individual products
        List<WebElement> moviesList = driver.findElements(By.className("filmlist__item"));

        // We are waiting to have the image of the last movie to be loaded to scrap
        this.loadAllImages(moviesList, driver, js, top);

        for (WebElement movie : moviesList) {
            WebElement title = movie.findElement(By.className("subtitle"));
            WebElement description = movie.findElement(By.className("filmlist__synopsis"));
            WebElement imageUrl = movie.findElement(By.className("filmlist__poster"));
            List<WebElement>  details  = movie.findElements(By.className("small"));

            //for each movie we will get a list of hours + added details that goes with the screening
            // Trick to fix an issue with the last element of details being empty sometimes
            String lastElement = details.get(details.size() - 1).getText();

            if(lastElement == null || lastElement.equalsIgnoreCase("")){
                details.remove(details.size() - 1);
            }
            int[] hours = new int[details.size()];
            int[] minutes = new int[details.size()];
            String[] detailsPerScreening = new String[details.size()];
            String[] screeningUrls = new String[details.size()];
            int index = 0;

            for( WebElement detail : details){
                //to avoid empty element (that happens when time after midnight).
                String detailString = detail.getText();
               // if(detailString != null && !detailString.equalsIgnoreCase("")) {
                    hours[index] = this.parseTime(detailString)[0];
                    minutes[index] = this.parseTime(detailString)[1];
                    detailsPerScreening[index] = this.parseDetails(detailString);
                    screeningUrls[index] = movie.findElement(By.className("small")).getAttribute("href");
                    index++;
               // }
            }

            System.out.println("Title: " + title.getText());
            System.out.println("Descrition: " + description.getText());
            System.out.println("Image Url: " + imageUrl.getAttribute("src"));
            for(int i = 0; i < hours.length; i++){
                System.out.println("Dets: " + hours[i] + ":" + minutes[i] + " - " + detailsPerScreening[i]);
                System.out.println("Screening url: " +  screeningUrls[i]);
            }

        }

        //Exit driver and close Chrome
        driver.quit();
    }

}