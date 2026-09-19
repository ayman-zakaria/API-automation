package tests;

import Listeners.TestListener;
import Services.BooksService;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;

/* just hands every test class a ready-to-use BooksService. @Listeners is declared
 * once here so subclasses don't need to repeat it.
 */
@Listeners(TestListener.class)
public abstract class BaseTest {

    protected BooksService booksService;

    @BeforeClass(alwaysRun = true)
    public void setUpService() {
        booksService = new BooksService();
    }
}
