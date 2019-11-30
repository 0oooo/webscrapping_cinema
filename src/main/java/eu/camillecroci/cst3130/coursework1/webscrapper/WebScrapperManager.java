package eu.camillecroci.cst3130.coursework1.webscrapper;

public class WebScrapperManager {

    public WebScrapperManager(){
        scrape();
    }

    private void scrape(){
        WebScrapperVueCinema ws = new WebScrapperVueCinema();
        ws.init();
        ws.run();
//        WebScrapperCineworld cw = new WebScrapperCineworld();

    }
}
