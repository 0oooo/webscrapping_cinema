package eu.camillecroci.cst3130.coursework1;

import eu.camillecroci.cst3130.coursework1.DAO.CinemaDAO;
import eu.camillecroci.cst3130.coursework1.DAO.SessionFactoryManager;
import eu.camillecroci.cst3130.coursework1.webscraper.WebScraperVueCinema;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * Unit test for Vue WebScraper
 */
public class VueWebScraperTest
{


    /**
     * Test the function of parsing the string of time
     */
    @Test
    @DisplayName("Test add screening")
    public void testParseTime() {
        String timeStringToBeTested = "1:28 PM";
        int[] expectedResultArray = {13,28};

        WebScraperVueCinema webScraperVueCinema = new WebScraperVueCinema();

        int[] actualResultArray = webScraperVueCinema.parseTime(timeStringToBeTested);

        if(expectedResultArray[0] != actualResultArray[0] && expectedResultArray[1] != actualResultArray[1]){
            fail("The arrays do not match. Expected result: " + expectedResultArray[0] + " " + expectedResultArray[1] +
                    " and actual result: " + actualResultArray[0] + " " + actualResultArray[1] + ".");
        }

    }
}
