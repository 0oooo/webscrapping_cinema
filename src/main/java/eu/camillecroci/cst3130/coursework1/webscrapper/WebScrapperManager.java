package eu.camillecroci.cst3130.coursework1.webscrapper;

public class WebScrapperManager {

    public WebScrapperManager(){
    }

    public void scrape(){
        WebScrapperVueCinema vue = new WebScrapperVueCinema();
        vue.start();
//        WebScrapperCineworld cw = new WebScrapperCineworld();
//        cw.start();
    }
}
