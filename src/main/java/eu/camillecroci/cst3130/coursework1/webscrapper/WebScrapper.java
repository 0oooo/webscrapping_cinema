package eu.camillecroci.cst3130.coursework1.webscrapper;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class WebScrapper extends Thread {

    private String VUE = "Vue";
    private String CINEWORLD = "Cineworld";



    public WebScrapper(){}


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

    //Get the cinemas corresponding to the name and scrape data from that particular.


}
