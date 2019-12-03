package eu.camillecroci.cst3130.coursework1;

import eu.camillecroci.cst3130.coursework1.webscrapper.WebScrapperManager;
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
        WebScrapperManager WSM = (WebScrapperManager) context.getBean("myWebscrapperManager");
        WSM.scrape();
    }
}
