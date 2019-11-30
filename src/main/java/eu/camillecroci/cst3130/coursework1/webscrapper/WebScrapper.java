package eu.camillecroci.cst3130.coursework1.webscrapper;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;

public class WebScrapper extends Thread {

    private String VUE = "Vue";
    private String CINEWORLD = "Cineworld";

    protected ArrayList<String> titles;
    protected ArrayList<String> dates;
    protected ArrayList<String> descriptions;
    protected ArrayList<String> imgUrls;
    protected ArrayList<ArrayList<String>> allDetails;
    protected ArrayList<ArrayList<Integer>> allHours;
    protected ArrayList<ArrayList<Integer>> allMinutes;
    protected ArrayList<ArrayList<String>> bookingUrls;

    public WebScrapper(){}

    public void init(){
        titles = new ArrayList<>();
        dates = new ArrayList<>();
        descriptions = new ArrayList<>();
        allDetails = new ArrayList<>();
        imgUrls = new ArrayList<>();
        allHours = new ArrayList<ArrayList<Integer>>();
        allMinutes = new ArrayList<ArrayList<Integer>>();
        bookingUrls = new ArrayList<ArrayList<String>>();
    }

    protected void scrollToElement(WebDriver driver, JavascriptExecutor js, WebElement top, WebElement element){
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


    public ArrayList<String> getTitles() {
        return titles;
    }

    public void setTitles(ArrayList<String> titles) {
        this.titles = titles;
    }

    public ArrayList<String> getDates() {
        return dates;
    }

    public void setDates(ArrayList<String> dates) {
        this.dates = dates;
    }

    public ArrayList<String> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(ArrayList<String> descriptions) {
        this.descriptions = descriptions;
    }

    public ArrayList<ArrayList<String>> getAllDetails() {
        return allDetails;
    }

    public void setAllDetails(ArrayList<ArrayList<String>> allDetails) {
        this.allDetails = allDetails;
    }

    public ArrayList<String> getImgUrls() {
        return imgUrls;
    }

    public void setImgUrls(ArrayList<String> imgUrls) {
        this.imgUrls = imgUrls;
    }

    public ArrayList<ArrayList<Integer>> getAllHours() {
        return allHours;
    }

    public void setAllHours(ArrayList<ArrayList<Integer>> allHours) {
        this.allHours = allHours;
    }

    public ArrayList<ArrayList<Integer>> getAllMinutes() {
        return allMinutes;
    }

    public void setAllMinutes(ArrayList<ArrayList<Integer>> allMinutes) {
        this.allMinutes = allMinutes;
    }

    public ArrayList<ArrayList<String>> getBookingUrls() {
        return bookingUrls;
    }

    public void setBookingUrls(ArrayList<ArrayList<String>> bookingUrls) {
        this.bookingUrls = bookingUrls;
    }
}
