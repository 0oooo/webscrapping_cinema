package eu.camillecroci.cst3130.coursework1.webscrapper;

public class WebScrapperManager {

    public WebScrapperManager(){
        scrape();
    }

    private void scrape(){
//        WebScrapperVueCinema ws = new WebScrapperVueCinema(true);
        WebScrapperCineworld cw = new WebScrapperCineworld(true);
    }
}
