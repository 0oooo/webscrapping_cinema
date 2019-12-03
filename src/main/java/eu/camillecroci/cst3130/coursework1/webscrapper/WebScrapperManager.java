package eu.camillecroci.cst3130.coursework1.webscrapper;

import java.util.List;

public class WebScrapperManager {

    private List<WebScrapper> webScrappersList;

    public void scrape(){
        for(WebScrapper ws : webScrappersList){
            ws.start();
        }

//        WebScrapperVueCinema vue = new WebScrapperVueCinema();
//        WebScrapperCineworld cw = new WebScrapperCineworld();
//
//        vue.start();
//        cw.start();
    }

    public List<WebScrapper> getWebScrappersList() {
        return webScrappersList;
    }

    public void setWebScrappersList(List<WebScrapper> webScrappersList) {
        this.webScrappersList = webScrappersList;
    }
}
