package eu.camillecroci.cst3130.coursework1.webscraper;

import eu.camillecroci.cst3130.coursework1.Cinema;
import eu.camillecroci.cst3130.coursework1.DAO.CinemaDAO;

import java.util.List;

public class WebScraperManager {

    private List<WebScraper> webScrapersList;

    public void scrape(){
        for(WebScraper ws : webScrapersList){
            ws.start();
        }

    }

    public List<WebScraper> getWebScrapersList() {
        return webScrapersList;
    }

    public void setWebScrapersList(List<WebScraper> webScrapersList) {
        this.webScrapersList = webScrapersList;
    }
}
