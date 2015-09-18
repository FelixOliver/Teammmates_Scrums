package teammates.test.cases.ui.browsertests;

import java.io.File;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.util.Url;
import teammates.test.pageobjects.AppPage;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;

/** Covers the table sorting functionality
 */
public class AppPageUiTest extends BaseUiTestCase {
    private static Browser browser;
    private static AppPage page;
    
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        browser = BrowserPool.getBrowser();        
        page = AppPage.getNewPageInstance(browser).navigateTo(new Url(getPath(0)));
    }
    
    @Test
    public void testVerifyTablePattern() {
        String patternString = "0{*}1{*}2{*}3{*}15{*}24{*}33";
        page.verifyTablePattern(0, patternString);
        page.verifyTablePattern(0, 0, patternString);
        
        patternString = "01 January 2012{*}01 January 2013{*}02 January 2012{*}01 February 2012{*}" + 
                "03 February 2012{*}12 December 2011{*}25 July 2012";
        page.verifyTablePattern(2, patternString);
        page.verifyTablePattern(0, 2, patternString);
        
        patternString = "+2%{*}+1%{*}+3%{*}0%{*}+5%{*}-1%{*}+25%";
        page.verifyTablePattern(4, patternString);
        page.verifyTablePattern(0, 4, patternString);
        
        patternString = "N/S{*}E -2%{*}E +99%{*}E 0%{*}E +20%{*}E 0%{*}E +20%{*}E +5%";
        page.verifyTablePattern(1, 3, patternString);
        
        patternString = "Test 1{*}Test n{*}Test 2{*}Test m{*}Test 10{*}Test q{*}Test 5{*}Test 8";
        page.verifyTablePattern(2, 1, patternString);
        
        // users of this API are not supposed to let the following case happen
        // empty table--trivial case, will not be tested
        // empty cell--will not be tested
        // cannot find table--will not be tested
        // cannot find columns--will not be tested
    }
    
    //TODO: add test cases for other methods in AppPage

    private static String getPath(int pageNumber) throws Exception{
        String workingDirectory = new File(".").getCanonicalPath();
        return "file:///"+workingDirectory+"/src/test/resources/pages/appPage" + pageNumber + ".html";
    }

    @AfterClass
    public static void classTearDown() throws Exception {
        printTestClassFooter();
        BrowserPool.release(browser);
    }
}
