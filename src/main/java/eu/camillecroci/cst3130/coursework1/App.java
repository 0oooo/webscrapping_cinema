package eu.camillecroci.cst3130.coursework1;

import eu.camillecroci.cst3130.coursework1.webscraper.WebScraperManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 *
 */
public class App 
{

    public static void main(String[] args )
    {
        ApplicationContext context = new ClassPathXmlApplicationContext("Beans.xml");
        WebScraperManager WSM = (WebScraperManager) context.getBean("myWebscraperManager");
        WSM.scrape();
    }
}
