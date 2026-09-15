package com.assessment.api.base;

import com.assessment.api.services.BooksService;
import com.assessment.api.utils.PropertiesReader;
import org.testng.annotations.BeforeClass;

/**
 * Base class for API test classes. Wires up the reusable Service Object(s)
 * and the test-data properties reader so individual test classes stay focused
 * on scenarios and assertions rather than setup.
 */
public abstract class BaseApiTest {

    protected BooksService booksService;
    protected static final PropertiesReader TEST_DATA = new PropertiesReader("testdata.properties");

    @BeforeClass(alwaysRun = true)
    public void setUpService() {
        booksService = new BooksService();
    }
}
