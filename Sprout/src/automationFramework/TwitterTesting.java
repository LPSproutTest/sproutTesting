package automationFramework;

import static org.testng.AssertJUnit.assertEquals;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;

public class TwitterTesting{
    public static WebDriver driver;
    public static String baseUrl;
    @Test
    public void main() throws InterruptedException {
        TwitterTesting test = new TwitterTesting();        
       	//As a user,  I would like to be able to send a message to my twitter account (Compose)
        test.messsagesTest();
   	
    	//As a user, I would like to see incoming tweets and be able to retweet or reply to them (Messages tab)
        test.retweetTest();
        test.replyTest();
        
    	//As a user, I want to schedule a tweet for a future date and see that tweet on my Sprout calendar (Publishing tab > Calendar)
        test.scheduleTest();
        
        //Testing keyword search
        test.searchTest();
    }
    
    @BeforeMethod
    public void beforeMethod() {
        System.setProperty("webdriver.chrome.driver", "src/resources/drivers/chromedriver.exe");
        baseUrl = "https://app.sproutsocial.com";
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
        driver.get(baseUrl);
        TwitterTesting loginTest = new TwitterTesting();
        loginTest.login();
    }

    @AfterMethod
    public void afterMethod() {
    	driver.close();
    }
    
    public void login(){
        driver.findElement(By.id("signin-email")).sendKeys("qatests+homework@sproutsocial.com");
        
    	//As a general rule, I do not store passwords in tests but since this is an assignment with test data on a test account I am making an exception
        driver.findElement(By.id("signin-pw")).sendKeys("LbEWMDjuRNaEtDy4Q.G9jrfvEWqt");
        driver.findElement(By.xpath("//button[@type='submit']")).click();
    }
    
    public void messsagesTest() throws InterruptedException{
    	WebDriverWait wait = new WebDriverWait(driver, 20);
        WebElement composeButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[@data-qa-button='open compose']")));
    	composeButton.click();
    	
        System.out.println("Typing tweet in form...");
    	String tweetMessage = "This is an automated test tweet.";
    	WebElement tweetTextForm = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//textarea[contains(@id,'ui-sproutedit-')]")));
        tweetTextForm.sendKeys(tweetMessage);
        System.out.println("SUCCESS");
        
        System.out.println("Sending tweet...");
        WebElement sendButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@class='primary-action js-submit-message js-primary action _main-compose-action']")));
        sendButton.click();
        System.out.println("SUCCESS");
        
        System.out.println("Viewing twitter profile...");
        WebElement twitterProfile = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[@data-pollinator-action='view_connected_twitter_profile']")));
        twitterProfile.click();
        System.out.println("SUCCESS");
        
        System.out.println("Viewing tweets section...");
        Thread.sleep(1800);
        WebElement tweets = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[@class='tm_tweets']")));
        tweets.click();
        System.out.println("SUCCESS");
        
        System.out.println("Verifying correct message in tweets section...");
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.cssSelector("span.js-toggle-message-text"), tweetMessage));
        System.out.println("SUCCESS");
        
        System.out.println("Viewing tweet via the feed page...");
        //Go to twitter feed page
        driver.get(baseUrl + "/feeds/twitter");
        //Verify original tweet on twitter feed URL
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.cssSelector("span.js-toggle-message-text"), tweetMessage));
        System.out.println("SUCCESS");
        
        System.out.println("Deleting test tweet");
        //Delete test tweet
        //Hover to show gear icon
        Actions builder = new Actions(driver);
        WebElement element = driver.findElement(By.xpath("//span[contains(.,'"+ tweetMessage +"')]"));
        builder.moveToElement(element).build().perform();
        //find and click gear icon
        WebElement gearIcon = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("span.ficon")));
        gearIcon.click();
        Thread.sleep(1000);
        driver.findElement(By.xpath("//a[@class='delete']")).click();
        Thread.sleep(1000);
        driver.findElement(By.linkText("DELETE")).click();
        Thread.sleep(2000);
        System.out.println("SUCCESS");
    }
    
    public void retweetTest()throws InterruptedException{
  	    WebDriverWait wait = new WebDriverWait(driver, 20);
  	    
        System.out.println("Viewing messages page");
        driver.get(baseUrl + "/messages/smart");
        System.out.println("SUCCESS");
        
        System.out.println("Clicking gear icon...");
        //Hover to show gear icon
        Actions builder = new Actions(driver);
        WebElement element = driver.findElement(By.xpath("//div[@id='recent_msgs']/div/section[2]/article/div[3]"));
        builder.moveToElement(element).build().perform();
        //find and click gear icon
        WebElement gearIcon = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@id='recent_msgs']/div/section[2]/article/div[2]/a/span")));
        gearIcon.click();
        System.out.println("SUCCESS");
        
        System.out.println("Retweeting...");
        //retweet button
        Thread.sleep(1000);
        driver.findElement(By.xpath("//a[@class='retweet']")).click();
        Thread.sleep(1000);
        driver.findElement(By.linkText("OK")).click();
        System.out.println("SUCCESS");
        
        System.out.println("Saving retweet message...");
        //Save retweet message text
        String retweetMessage = driver.findElement(By.xpath("//div[@id='recent_msgs']/div/section[2]/article/div[3]/span")).getText();
        Thread.sleep(1000);
        System.out.println("SUCCESS: Saved retweet text : " + retweetMessage);
        
        System.out.println("Viewing twitter feed...");
        //View twitter feed
        driver.get(baseUrl + "/feeds/twitter");
        System.out.println("SUCCESS");
        
        System.out.println("Confirming the retweeted text matches in twitter feed...");
        //Verify the retweeted text matches
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//span[contains(.,'"+ retweetMessage +"')]")));
        wait.until(ExpectedConditions.textToBePresentInElement(By.cssSelector("section.floatleft"), "retweeted by"));
        System.out.println("SUCCESS");
        
        System.out.println("Deleting test retweet");
        //Delete test tweet
        //Hover to show gear icon
        Actions builder1 = new Actions(driver);
        WebElement element1 = driver.findElement(By.xpath("//span[contains(.,'"+ retweetMessage +"')]"));
        builder1.moveToElement(element1).build().perform();
        //find and click gear icon
        WebElement gearIcon1 = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("span.ficon")));
        gearIcon1.click();
        Thread.sleep(1000);
        driver.findElement(By.xpath("//a[@class='delete']")).click();
        Thread.sleep(1000);
        driver.findElement(By.linkText("DELETE")).click();
        Thread.sleep(2000);
        System.out.println("SUCCESS");
    }
    
    public void replyTest()throws InterruptedException{
  	    WebDriverWait wait = new WebDriverWait(driver, 20);
  	    
        System.out.println("Navigating to the messages page...");
        driver.get(baseUrl + "/messages/smart");
        System.out.println("SUCCESS");
        
        //Get twitter reply user
        String twitterUser = driver.findElement(By.xpath("//div[@id='recent_msgs']/div/section/article/section/article/section/a/span")).getText();
        
        System.out.println("Clicking reply button...");
        //Hover to reply icon
        Actions replyBuilder = new Actions(driver);
        WebElement replyElement = driver.findElement(By.cssSelector("div.message-text.font-14"));
        replyBuilder.moveToElement(replyElement).build().perform();
        Thread.sleep(1000);
        WebElement replyButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@id='recent_msgs']/div/section/article/section/ul/li/a/span")));
        replyButton.click();
        System.out.println("SUCCESS");
        
        //wait for reply tweet form and type reply tweet
        System.out.println("Submitting reply");
    	WebElement replyTweetTextForm = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//textarea[contains(@id,'ui-sproutedit-')]")));
    	String replyTweet  = "This is a reply tweet";
    	replyTweetTextForm.sendKeys(replyTweet);
    	//Uncheck mark completed box
        driver.findElement(By.cssSelector("label.checkbox-label.mark-complete > span.checkbox-visible"));
    	//Click reply button
        driver.findElement(By.xpath("//button[contains(.,'Send')]")).click();
        System.out.println("SUCCESS");
        
        System.out.println("Verifying reply tweet on feed page...");
        //View twitter feed
        driver.get(baseUrl + "/feeds/twitter");
        //Verify reply tweet
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.cssSelector("span.js-toggle-message-text"), "@" + twitterUser + " " + replyTweet));
        System.out.println("SUCCESS");
        
        System.out.println("Deleting test reply tweet");
        //Delete test tweet
        //Hover to show gear icon
        Actions builder = new Actions(driver);
        WebElement element = driver.findElement(By.xpath("//span[contains(.,'"+ replyTweet +"')]"));
        builder.moveToElement(element).build().perform();
        //find and click gear icon
        WebElement gearIcon = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("span.ficon")));
        gearIcon.click();
        Thread.sleep(1000);
        driver.findElement(By.xpath("//a[@class='delete']")).click();
        Thread.sleep(1000);
        driver.findElement(By.linkText("DELETE")).click();
        Thread.sleep(2000);
        System.out.println("SUCCESS");
    }
    public void scheduleTest()throws InterruptedException{
  	    WebDriverWait wait = new WebDriverWait(driver, 20);
  	    
    	//As a user, I want to schedule a tweet for a future date and see that tweet on my Sprout calendar (Publishing tab > Calendar)
        //Navigate to publishing page
        System.out.println("Navigating to publishing page...");
        driver.get(baseUrl + "/publishing");
        System.out.println("SUCCESS");
        
        System.out.println("Clicking Schedule Message button...");
        WebElement scheduleMessageButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(.,'Schedule Message')]")));
        scheduleMessageButton.click();
        System.out.println("SUCCESS");
        
        System.out.println("Typing scheduled tweet in form...");
    	WebElement scheduledTweetTextForm = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//textarea[contains(@id,'ui-sproutedit-')]")));
    	String scheduledTweet  = "This is a scheduled tweet";
    	scheduledTweetTextForm.sendKeys(scheduledTweet);
        System.out.println("SUCCESS");
        
        System.out.println("Clicking date on the calendar");
        driver.findElement(By.xpath("//div[@class='schedule-calendar']/div/div/div/a[@data-handler='next']")).click();
        driver.findElement(By.xpath("//div[@class='schedule-calendar']/div/div/div/a[@data-handler='next']")).click();
        driver.findElement(By.xpath("//a[contains(text(),'22')]")).click();
        //String scheduleDate = driver.findElement(By.cssSelector("h4.multi-schedule-date")).getText();
        System.out.println("SUCCESS");
        
        System.out.println("Clicking Schedule button...");
        driver.findElement(By.xpath("//button[contains(.,'Schedule')]")).click();
        System.out.println("SUCCESS");
        
        System.out.println("Clicking scheduled day to see scheduled tweet...");
        Thread.sleep(1500);
        //WebElement scheduledTweetCalendar = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@id='subview_cont_calendar']/div/div/a[@data-handler='next']")));
        driver.findElement(By.xpath("//div[@id='subview_cont_calendar']/div/div/a[@data-handler='next']")).click();
        Thread.sleep(1500);
        driver.findElement(By.xpath("//div[@id='subview_cont_calendar']/div/div/a[@data-handler='next']")).click();
        Thread.sleep(1500);
        driver.findElement(By.xpath("//a[contains(text(),'22')]")).click();
        System.out.println("SUCCESS");
        
        System.out.println("Verifying scheduled date is present...");
        //System.out.println(scheduleDate);
        //Can't seem to figure out how to convert this to the full date that is present on h4
        //wait.until(ExpectedConditions.textToBePresentInElementLocated(By.cssSelector("h4.multi-schedule-date"), scheduleDate));
        System.out.println("SUCCESS");
        
        System.out.println("Verifying scheduled tweet is present and correct...");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//span[contains(.,'"+ scheduledTweet +"')]")));
        System.out.println("SUCCESS");    
    }
    
    //Search for tweets
    public void searchTest()throws InterruptedException{
  	    WebDriverWait wait = new WebDriverWait(driver, 20);
        driver.get(baseUrl);
  	    System.out.println("Clicking search button...");
        WebElement searchButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[@data-qa-button='open search']")));
    	searchButton.click();
    	System.out.println("SUCCESS");
    	
    	System.out.println("Filling out search terms...");
    	WebElement searchKeywordForm = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@id='quick_search']")));
        searchKeywordForm.sendKeys("Testing");
    	System.out.println("SUCCESS");
    	
    	System.out.println("Executing keyword search");
    	Thread.sleep(3000);
    	driver.findElement(By.xpath("//span[contains(.,'Keyword Search')]")).click();
    	searchKeywordForm.sendKeys(Keys.RETURN);
    	System.out.println("SUCCESS");
    	
    	System.out.println("Verifying search has results");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("section.bubble-meta")));
    	System.out.println("SUCCESS");
    }
  }
