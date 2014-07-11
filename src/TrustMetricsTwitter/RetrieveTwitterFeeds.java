package TrustMetricsTwitter; 

import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.northconcepts.datapipeline.core.DataEndpoint;
import com.northconcepts.datapipeline.core.DataReader;
import com.northconcepts.datapipeline.core.DataWriter;
import com.northconcepts.datapipeline.core.Field;
import com.northconcepts.datapipeline.core.Record;
import com.northconcepts.datapipeline.core.StreamWriter;
import com.northconcepts.datapipeline.csv.CSVReader;
import com.northconcepts.datapipeline.csv.CSVWriter;
import com.northconcepts.datapipeline.job.JobTemplate;
import com.northconcepts.datapipeline.json.JsonReader;
import com.northconcepts.datapipeline.json.builder.JsonField;
import com.northconcepts.datapipeline.transform.BasicFieldTransformer;
import com.northconcepts.datapipeline.transform.SetCalculatedField;
import com.northconcepts.datapipeline.transform.SetField;
import com.northconcepts.datapipeline.transform.Transformer;
import com.northconcepts.datapipeline.transform.TransformingReader;
import com.northconcepts.datapipeline.validate.ValidatingReader;
import com.northconcepts.datapipeline.filter.FieldFilter;
import com.northconcepts.datapipeline.filter.rule.IsNotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.Hashtable;

public class RetrieveTwitterFeeds 
{
	  static String AccessToken = "22440636-aAsWewFH4XsVldICls5Bpy1LTgiaIuXIxWqBRWgtb";
	  static String AccessSecret = "xuhcY9h77v6fpZuIXNQj6MDH1BjjJbpA7RcjZZXg1eSbd";
	  static String ConsumerKey = "jXIycnRHrIFZQ6FbDdLZ0raaS";
	  static String ConsumerSecret = "6sRLrmEtbaoGAlNyXYFo1Rgb9FeuDYbn7hccHZsDfBAXyNfyXA";
	  static Hashtable<String, String> twitterAccountDetails = new Hashtable<String, String>();
	  public static final Logger log = DataEndpoint.log;
	  //20 News Websites 
	  public static final String userDataURL = "https://api.twitter.com/1.1/users/lookup.json?screen_name=Independent,nytimes,guardian,TheEconomist,USATODAY,BloombergNews,AP,msnbc,Forbes,FinancialTimes,"
	  		+ "BBCWorld,timesofindia,Reuters,WSJ,CNET,CBSNews,HuffingtonPost,dailytelegraph,CNN,ABC";
	  //25 Fake Sites 
	  public final static String badUserDataURL = "https://api.twitter.com/1.1/users/lookup.json?screen_name=TheFakeCNN,NYTOnIt,TheOnion,FakeSTcom,TheFakeESPN,BreakingNews,FakeYahooNews,NotSportsCenter,newsthump,BBCMews,feministtswift," + 
									"FakeAPStylebook,BBCSporf,PinterestFake,Bill_Nye_tho,SadPaulGiamatti,SeinfeldToday,Seinfeld2000,NotCoatFactory,TomHankThatsMe,notzuckerberg,Jesus_M_Christ,FakeSteranko,Kimkierkegaard,PrinceTweets2U";
	  public final String goodUserFileLocation = "../TrustMetricsTwitter/files/UserAccounts/UserAccountInfoJSON";
	  public final static String badUserFileLocation = "../TrustMetricsTwitter/files/UserAccounts/BadAccountInfoJSON"; 
	 
	public static void main(String[] args) throws Exception
	{
		mainForGoodUserData();
        //mainForBadUserData();
	}

	private static void mainForBadUserData() throws IOException, OAuthCommunicationException, OAuthExpectationFailedException, OAuthMessageSignerException {
		OAuthConsumer consumer = new CommonsHttpOAuthConsumer(ConsumerKey, ConsumerSecret);
        consumer.setTokenWithSecret(AccessToken, AccessSecret);
        
        File file = new File("../TrustMetricsTwitter/files/LastBadTweetID.txt");
    	String[][] arrayLine = new String[25][2];
        
        if (file.length() > 0) {
        	BufferedReader br = new BufferedReader(new FileReader("../TrustMetricsTwitter/files/LastBadTweetID.txt"));        
        	String[] word = new String[50]; 
        	String line;
        	String wholeString = ""; 
        	while ((line = br.readLine()) != null) {
        		wholeString += line + " ";
        	}
        
        	word = wholeString.split("[\\p{Punct}\\s]+");
        	
        	int count = 0;
        	for (int i=0; i<25; i++){
        		for (int j=0; j<2; j++){
        			arrayLine[i][j] = word[count];
        			count++;
        		}
        	}
        	br.close();
        }
        boolean firstTime = true; 
        if (arrayLine[0][0] == null)
        {	
        	firstTime = true; 
        	System.out.println("Inside zero arrayline Bad");
        	twitterAccountDetails.put("BreakingNews","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=BreakingNews&include_entities=true&include_rts=true&count=200");
        	twitterAccountDetails.put("PrinceTweets2U","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=PrinceTweets2U&include_entities=true&include_rts=true&count=200");	
        	twitterAccountDetails.put("Jesus_M_Christ","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=Jesus_M_Christ&include_entities=true&include_rts=true&count=200");
        	twitterAccountDetails.put("SeinfeldToday","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=SeinfeldToday&include_entities=true&include_rts=true&count=200");
        	twitterAccountDetails.put("FakeYahooNews","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=FakeYahooNews&include_entities=true&include_rts=true&count=200"); 
        	twitterAccountDetails.put("newsthump","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=newsthump&include_entities=true&include_rts=true&count=200");
        	twitterAccountDetails.put("notzuckerberg","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=notzuckerberg&include_entities=true&include_rts=true&count=200");     	
        	twitterAccountDetails.put("NotSportsCenter","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=NotSportsCenter&include_entities=true&include_rts=true&count=200");
        	twitterAccountDetails.put("FakeAPStylebook","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=FakeAPStylebook&include_entities=true&include_rts=true&count=200"); 
        	twitterAccountDetails.put("TomHankThatsMe","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=TomHankThatsMe&include_entities=true&include_rts=true&count=200");
        	twitterAccountDetails.put("NYTOnIt","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=NYTOnIt&include_entities=true&include_rts=true&count=200");
        	twitterAccountDetails.put("TheFakeCNN","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=TheFakeCNN&include_entities=true&include_rts=true&count=200");
        	twitterAccountDetails.put("BBCSporf","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=BBCSporf&include_entities=true&include_rts=true&count=200");
        	twitterAccountDetails.put("feministtswift","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=feministtswift&include_entities=true&include_rts=true&count=200");
        	twitterAccountDetails.put("TheOnion","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=TheOnion&include_entities=true&include_rts=true&count=200");
        	twitterAccountDetails.put("Kimkierkegaard","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=Kimkierkegaard&&include_entities=true&include_rts=true&count=200");
        	twitterAccountDetails.put("Bill_Nye_tho","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=Bill_Nye_tho&include_entities=true&include_rts=true&count=200");
        	twitterAccountDetails.put("FakeSteranko","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=FakeSteranko&include_entities=true&include_rts=true&count=200");  
        	twitterAccountDetails.put("NotCoatFactory","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=NotCoatFactory&&include_entities=true&include_rts=true&count=200"); 
        	twitterAccountDetails.put("SadPaulGiamatti","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=SadPaulGiamatti&include_entities=true&include_rts=true&count=200");        	
        	twitterAccountDetails.put("PinterestFake","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=PinterestFake&include_entities=true&include_rts=true&count=200");
        	twitterAccountDetails.put("Seinfeld2000","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=Seinfeld2000&include_entities=true&include_rts=true&count=200");
        	twitterAccountDetails.put("BBCMews","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=BBCMews&include_entities=true&include_rts=true&count=200");
        	twitterAccountDetails.put("TheFakeESPN","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=TheFakeESPN&include_entities=true&include_rts=true&count=200");       	
        	twitterAccountDetails.put("FakeSTcom","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=FakeSTcom&include_entities=true&include_rts=true&count=200");	
        }
        else 
        {	
        	System.out.println("Inside one arrayline Bad");
        	firstTime = false; 
        	twitterAccountDetails.put("BreakingNews","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=BreakingNews&include_entities=true&include_rts=true&count=200&since_id="+arrayLine[0][1]);
        	twitterAccountDetails.put("PrinceTweets2U","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=PrinceTweets2U&include_entities=true&include_rts=true&count=200&since_id="+arrayLine[1][1]);	
        	twitterAccountDetails.put("Jesus_M_Christ","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=Jesus_M_Christ&include_entities=true&include_rts=true&count=4&since_id="+arrayLine[2][1]);
        	twitterAccountDetails.put("SeinfeldToday","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=SeinfeldToday&include_entities=true&include_rts=true&count=4");
        	twitterAccountDetails.put("FakeYahooNews","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=FakeYahooNews&include_entities=true&include_rts=true&count=200&since_id="+arrayLine[4][1]); 
        	twitterAccountDetails.put("newsthump","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=newsthump&include_entities=true&include_rts=true&count=200&since_id="+arrayLine[5][1]);
        	twitterAccountDetails.put("notzuckerberg","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=notzuckerberg&include_entities=true&include_rts=true&count=200&since_id="+arrayLine[6][1]);     	
        	twitterAccountDetails.put("NotSportsCenter","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=NotSportsCenter&include_entities=true&include_rts=true&count=200&since_id="+arrayLine[7][1]);
        	twitterAccountDetails.put("FakeAPStylebook","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=FakeAPStylebook&include_entities=true&include_rts=true&count=200&since_id="+arrayLine[8][1]); 
        	twitterAccountDetails.put("TomHankThatsMe","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=TomHankThatsMe&include_entities=true&include_rts=true&count=200&since_id="+arrayLine[9][1]);
        	twitterAccountDetails.put("NYTOnIt","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=NYTOnIt&include_entities=true&include_rts=true&count=200&since_id="+arrayLine[10][1]);
        	twitterAccountDetails.put("TheFakeCNN","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=TheFakeCNN&include_entities=true&include_rts=true&count=200&since_id="+arrayLine[11][1]);
        	twitterAccountDetails.put("BBCSporf","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=BBCSporf&include_entities=true&include_rts=true&count=200&since_id="+arrayLine[12][1]);
        	twitterAccountDetails.put("feministtswift","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=feministtswift&include_entities=true&include_rts=true&count=200&since_id="+arrayLine[13][1]);
        	twitterAccountDetails.put("TheOnion","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=TheOnion&include_entities=true&include_rts=true&count=200&since_id="+arrayLine[14][1]);
        	twitterAccountDetails.put("Kimkierkegaard","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=Kimkierkegaard&&include_entities=true&include_rts=true&count=200&since_id="+arrayLine[15][1]);
        	twitterAccountDetails.put("SadPaulGiamatti","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=SadPaulGiamatti&include_entities=true&include_rts=true&count=200&since_id="+arrayLine[16][1]);
        	twitterAccountDetails.put("NotCoatFactory","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=NotCoatFactory&&include_entities=true&include_rts=true&count=200&since_id="+arrayLine[17][1]); 
        	twitterAccountDetails.put("FakeSteranko","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=FakeSteranko&include_entities=true&include_rts=true&count=200&since_id="+arrayLine[18][1]);
        	twitterAccountDetails.put("Bill_Nye_tho","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=Bill_Nye_tho&include_entities=true&include_rts=true&count=200&since_id="+arrayLine[19][1]);
        	twitterAccountDetails.put("Seinfeld2000","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=Seinfeld2000&include_entities=true&include_rts=true&count=4&since_id="+arrayLine[20][1]);          	
        	twitterAccountDetails.put("PinterestFake","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=PinterestFake&include_entities=true&include_rts=true&count=200&since_id="+arrayLine[21][1]);
        	twitterAccountDetails.put("TheFakeESPN","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=TheFakeESPN&include_entities=true&include_rts=true&count=4");  
        	twitterAccountDetails.put("BBCMews","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=BBCMews&include_entities=true&include_rts=true&count=200&since_id="+arrayLine[23][1]); 	     	
        	twitterAccountDetails.put("FakeSTcom","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=FakeSTcom&include_entities=true&include_rts=true&count=200&since_id="+arrayLine[24][1]);	
        }
        
        System.out.println(twitterAccountDetails);
        String lastIDSaveFileLocation = "../TrustMetricsTwitter/files/LastBadTweetID.txt";
        int classifier = 0; 
        
        for (String key : twitterAccountDetails.keySet()){
        	//getTweetsInJSON(consumer, key, twitterAccountDetails.get(key));
        	//saveLastID(key,firstTime,lastIDSaveFileLocation); 
        }
        
        for (String key : twitterAccountDetails.keySet()){
        	//createCSVBasicTweetData(key,classifier);   
        }      
        
        //getUserDataJSON(consumer,badUserDataURL,badUserFileLocation);
		
	}
	
	private static void mainForGoodUserData() throws IOException, OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException{
		OAuthConsumer consumer = new CommonsHttpOAuthConsumer(ConsumerKey, ConsumerSecret);
        consumer.setTokenWithSecret(AccessToken, AccessSecret);
        
        File file = new File("../TrustMetricsTwitter/files/LastTweetID.txt");
    	String[][] arrayLine = new String[20][2];
        
        if (file.length() > 0) {
        	BufferedReader br = new BufferedReader(new FileReader("../TrustMetricsTwitter/files/LastTweetID.txt"));        
        	String[] word = new String[40]; 
        	String line;
        	String wholeString = ""; 
        	while ((line = br.readLine()) != null) {
        		wholeString += line + " ";
        	}
        
        	word = wholeString.split("[\\p{Punct}\\s]+");
        	
        	int count = 0;
        	for (int i=0; i<20; i++){
        		for (int j=0; j<2; j++){
        			arrayLine[i][j] = word[count];
        			count++;
        		}
        	}
        	br.close();
        }
        boolean firstTime = true; 
        if (arrayLine[0][0] == null)
        {	
        	firstTime = true; 
        	System.out.println("Inside zero arrayline");
        	twitterAccountDetails.put("Independent","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=Independent&include_entities=true&include_rts=true&count=200");
        	twitterAccountDetails.put("nytimes","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=nytimes&include_entities=true&include_rts=true&count=200");
        	twitterAccountDetails.put("guardian","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=guardian&include_entities=true&include_rts=true&count=200");
        	twitterAccountDetails.put("TheEconomist","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=TheEconomist&include_entities=true&include_rts=true&count=200");
        	twitterAccountDetails.put("USATODAY","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=USATODAY&include_entities=true&include_rts=true&count=200");
        	twitterAccountDetails.put("BloombergNews","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=BloombergNews&include_entities=true&include_rts=true&count=200");
        	twitterAccountDetails.put("AP","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=AP&include_entities=true&include_rts=true&count=200");       	
        	twitterAccountDetails.put("msnbc","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=msnbc&include_entities=true&include_rts=true&count=200");
        	twitterAccountDetails.put("Forbes","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=Forbes&include_entities=true&include_rts=true&count=200");
        	twitterAccountDetails.put("FinancialTimes","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=FinancialTimes&include_entities=true&include_rts=true&count=200");
        	twitterAccountDetails.put("BBCWorld","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=BBCWorld&include_entities=true&include_rts=true&count=200");
        	twitterAccountDetails.put("timesofindia","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=timesofindia&include_entities=true&include_rts=true&count=200");       	
        	twitterAccountDetails.put("Reuters","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=Reuters&include_entities=true&include_rts=true&count=200");
        	twitterAccountDetails.put("WSJ","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=WSJ&include_entities=true&include_rts=true&count=200");
        	twitterAccountDetails.put("CNET","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=CNET&include_entities=true&include_rts=true&count=200");
        	twitterAccountDetails.put("CBSNews","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=CBSNews&include_entities=true&include_rts=true&count=200");
        	twitterAccountDetails.put("HuffingtonPost","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=HuffingtonPost&include_entities=true&include_rts=true&count=200");
        	twitterAccountDetails.put("dailytelegraph","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=dailytelegraph&include_entities=true&include_rts=true&count=200");       	       	       	
        	twitterAccountDetails.put("CNN","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=CNN&&include_entities=true&include_rts=true&count=200");   	
        	twitterAccountDetails.put("ABC","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=ABC&include_entities=true&include_rts=true&count=200");	
        }
        else 
        {	
        	System.out.println("Inside one arrayline");
        	firstTime = false;         	       	
        	twitterAccountDetails.put("Independent","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=Independent&include_entities=true&include_rts=true&count=200&since_id="+arrayLine[0][1]);
        	twitterAccountDetails.put("nytimes","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=nytimes&include_entities=true&include_rts=true&count=200&since_id="+arrayLine[1][1]);
        	twitterAccountDetails.put("Guardian","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=guardian&include_entities=true&include_rts=true&count=200&since_id="+arrayLine[2][1]);
        	twitterAccountDetails.put("TheEconomist","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=TheEconomist&include_entities=true&include_rts=true&count=200&since_id="+arrayLine[3][1]);
        	twitterAccountDetails.put("USATODAY","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=USATODAY&include_entities=true&include_rts=true&count=200&since_id="+arrayLine[4][1]);
        	twitterAccountDetails.put("BloombergNews","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=BloombergNews&include_entities=true&include_rts=true&count=200&since_id="+arrayLine[5][1]);
        	twitterAccountDetails.put("AP","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=AP&include_entities=true&include_rts=true&count=200&since_id="+arrayLine[6][1]);       	
        	twitterAccountDetails.put("msnbc","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=msnbc&include_entities=true&include_rts=true&count=200&since_id="+arrayLine[7][1]);
        	twitterAccountDetails.put("Forbes","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=Forbes&include_entities=true&include_rts=true&count=200&since_id="+arrayLine[8][1]);
        	twitterAccountDetails.put("FinancialTimes","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=FinancialTimes&include_entities=true&include_rts=true&count=200&since_id="+arrayLine[9][1]);
        	twitterAccountDetails.put("BBCWorld","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=BBCWorld&include_entities=true&include_rts=true&count=200&since_id="+arrayLine[10][1]);
        	twitterAccountDetails.put("timesofindia","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=timesofindia&include_entities=true&include_rts=true&count=200&since_id="+arrayLine[11][1]);       	
        	twitterAccountDetails.put("Reuters","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=Reuters&include_entities=true&include_rts=true&count=200&since_id="+arrayLine[12][1]);
        	twitterAccountDetails.put("WSJ","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=WSJ&include_entities=true&include_rts=true&count=200&since_id="+arrayLine[13][1]);
        	twitterAccountDetails.put("CNET","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=CNET&include_entities=true&include_rts=true&count=200&since_id="+arrayLine[14][1]);
        	twitterAccountDetails.put("CBSNews","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=CBSNews&include_entities=true&include_rts=true&count=200&since_id="+arrayLine[15][1]);
        	twitterAccountDetails.put("dailytelegraph","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=dailytelegraph&include_entities=true&include_rts=true&count=200&since_id="+arrayLine[16][1]);  
        	twitterAccountDetails.put("HuffingtonPost","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=HuffingtonPost&include_entities=true&include_rts=true&count=200&since_id="+arrayLine[17][1]);            	     	       	  	
        	twitterAccountDetails.put("CNN","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=CNN&&include_entities=true&include_rts=true&count=200&since_id="+arrayLine[18][1]);   	
        	twitterAccountDetails.put("ABC","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=ABC&include_entities=true&include_rts=true&count=200&since_id="+arrayLine[19][1]);	
        }
        
        System.out.println(twitterAccountDetails);
        String lastIDSaveFileLocation = "../TrustMetricsTwitter/files/LastTweetID.txt";
        int classifier = 1; 
        
        for (String key : twitterAccountDetails.keySet()){
        	//getTweetsInJSON(consumer, key, twitterAccountDetails.get(key));
        	//saveLastID(key,firstTime,lastIDSaveFileLocation); 
        }
        
        for (String key : twitterAccountDetails.keySet()){
        	createCSVBasicTweetData(key,classifier);   
        }      
        
        //getUserDataJSON(consumer,badUserDataURL,badUserFileLocation);
	}

	private static void getUserDataJSON (OAuthConsumer OAuthConsumerKeyandSecret, String userAccountURL, String userFileAccountString) throws OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException, ClientProtocolException, IOException{
		HttpGet userAccountRequest = new HttpGet(userAccountURL);
		OAuthConsumerKeyandSecret.sign(userAccountRequest);
		HttpClient userClient = HttpClientBuilder.create().build();
		HttpResponse userResponse = userClient.execute(userAccountRequest);
		int userStatusCode = userResponse.getStatusLine().getStatusCode();
		String USERJSONString = IOUtils.toString(userResponse.getEntity().getContent());
		System.out.println("StatusCodeForUserAccountInfo : " + userStatusCode);
		PrintWriter jsonObjectWriter = new PrintWriter(userFileAccountString);
		jsonObjectWriter.write(USERJSONString);
		jsonObjectWriter.close();	
		createCSVForUserData(userFileAccountString);
	}
	
	private static void getTweetsInJSON(OAuthConsumer OAuthConsumerKeyandSecret, String twitterAccountName, String twitterAccountURL) throws OAuthCommunicationException,OAuthExpectationFailedException, OAuthMessageSignerException
	{		
		HttpGet request = new HttpGet(twitterAccountURL);		
		OAuthConsumerKeyandSecret.sign(request);
        HttpClient client = HttpClientBuilder.create().build();
        
        try {
			HttpResponse response = client.execute(request);			
			int statusCode = response.getStatusLine().getStatusCode();
			String JSONString = IOUtils.toString(response.getEntity().getContent());
			String modifiedJSONString = JSONString.replaceAll("\"text\"", "\"actualTweet\"");

			System.out.println("Writing Status Code For Retrieving Tweets from " + twitterAccountName + " : " + statusCode);
			PrintWriter jsonObjectWriter = new PrintWriter("../TrustMetricsTwitter/files/IndividualJSONFiles/"+twitterAccountName+"JSON");
			jsonObjectWriter.write(modifiedJSONString);
			jsonObjectWriter.close();			
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	private static void saveLastID(String twitterStreamKey, boolean firstTime, String fileName) {
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);		
		try {
			TwitterStream[] stream = mapper.readValue(new File("../TrustMetricsTwitter/files/IndividualJSONFiles/" + twitterStreamKey + "JSON"), TwitterStream[].class);	
			
			if (stream != null && stream.length > 0)
			{
				String line = null; 
				String oldText = "";
				//
				if (firstTime == false) {
					BufferedReader fileReader = new BufferedReader(new FileReader(fileName));
					while ((line = fileReader.readLine()) != null)
					{
						oldText += line + "\n";					
					}
					fileReader.close();
				
					String newText = oldText.replaceAll(twitterStreamKey + "\\s.*", twitterStreamKey + "  " + stream[0].getId());
					PrintWriter writer = new PrintWriter(fileName);
					writer.print(newText);
					writer.close();
				}
				else 
				{
					FileWriter writer = new FileWriter(fileName,true);
					String newText = twitterStreamKey + " " + stream[0].getId() + "\n";
					writer.append(newText);
					writer.close();
				}
			}
		} catch (com.fasterxml.jackson.core.JsonParseException e2) {			
			e2.printStackTrace();
		} catch (JsonMappingException e2) {
			e2.printStackTrace();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
			
	}

	private static void createCSVBasicTweetData(String twitterUserKey,int classifier) throws FileNotFoundException {
	       
		String FILE = "../TrustMetricsTwitter/files/IndividualJSONFiles/" + twitterUserKey + "JSON";
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);		
		try {
			TwitterStream[] stream = mapper.readValue(new File(FILE), TwitterStream[].class);				
			if (stream != null && stream.length > 0) 
			{		
				DataReader reader = new JsonReader(new File(FILE))
	            .addField("CreatedAt", "/array/object/created_at")
	            .addField("ID", "/array/object/id")
	            .addField("ActualTweet", "/array/object/actualTweet")
	            .addField("Source", "/array/object/source")
	            .addField("Truncated", "/array/object/truncated")
	            .addField("InReplyToStatusID", "/array/object/in_reply_to_status_id")
	            .addField("InReplyToUserID", "/array/object/in_reply_to_user_id")
	            .addField("InReplyToScreenName", "/array/object/in_reply_to_screen_name")	
	            .addField("UserID", "/array/object/user/object/id")	
	            .addField("UserScreenName", "/array/object/user/object/screen_name")	
	            .addField("GEO", "/array/object/geo")
	            .addField("Coordinates", "/array/object/coordinates")
	            .addField("PlaceID", "/array/object/place/object/id")
	            .addField("PlaceURL", "/array/object/place/object/url")
	            .addField("PlaceType", "/array/object/place/object/place_type")
	            .addField("PlaceName", "/array/object/place/object/name")
	            .addField("PlaceFullName", "/array/object/place/object/full_name")
	            .addField("PlaceCountryCode", "/array/object/place/object/country_code")
	            .addField("PlaceCountry", "/array/object/place/object/country")
	            .addField("Contributors", "/array/object/contributors")
	            .addField("RetweetCount", "/array/object/retweet_count")
	            .addField("FavouriteCount", "/array/object/favorite_count")
	            .addField("Hashtags", "/array/object/entities/object/hashtags/array/object/actualTweet")
	            .addField("Symbols", "/array/object/entities/object/symbols")
	            .addField("URL", "/array/object/entities/object/urls/array/object/url")
	            .addField("URLInTweetExpanded", "/array/object/entities/object/urls/array/object/expanded_url")
	            .addField("Indices", "/array/object/entities/object/urls/array/object/indices/array/text()")
	            .addField("MediaIndices", "/array/object/entities/object/media/array/object/indices/array/text()")
	            .addField("MediaURL", "/array/object/entities/object/media/array/object/media_url")
	            .addField("MediaURLShort", "/array/object/entities/object/media/array/object/url")
	            .addField("MediaType", "/array/object/entities/object/media/array/object/type")
	            .addField("UserMentionScreenName", "/array/object/entities/object/user_mentions/array/object/screen_name")
	            .addField("UserMentionName", "/array/object/entities/object/user_mentions/array/object/name")
	            .addField("UserMentionID", "/array/object/entities/object/user_mentions/array/object/id")
	            .addField("Favourited", "/array/object/favorited")
	            .addField("Retweeted", "/array/object/retweeted")
	            .addField("PossiblySensitive", "/array/object/possibly_sensitive")
	            .addField("Language", "/array/object/lang")
				.addRecordBreak("/array/object"); 			
				
			    ValidatingReader validatingReader = new ValidatingReader(reader)
	            .setExceptionOnFailure(false)
	            .setRecordStackTraceInMessage(false);
			    
				validatingReader.add(new FieldFilter("ID")
                .addRule(new IsNotNull()));
								
				TransformingReader finalReader = new TransformingReader(validatingReader);
				finalReader.add(new SetField("Classifier",classifier))
							.add (new Transformer(){public boolean transform(Record record) throws Throwable {
								// creates an 'TweetLength' field
								Field TweetLength = record.getField("TweetLength", true);
								Field actualTweet = record.getField("ActualTweet");
								TweetLength.setValue(actualTweet.toString().length() - 30); 		                
								return true;}});
								        				
				DataWriter writer = new  CSVWriter(new File("../TrustMetricsTwitter/files/IndividualCSVFiles/" + twitterUserKey + ".csv" ));	 
				JobTemplate.DEFAULT.transfer(finalReader, writer);	
				reader.close();
				writer.close();
				System.out.println("Successfully created CSV Files");
			}
		} catch (JsonMappingException e2) {
			e2.printStackTrace(); 
		}catch (IOException e2) {
			e2.printStackTrace();
		}
	}
	
	private static void createCSVForUserData(String fileName) throws UnsupportedEncodingException, MalformedURLException, IOException {
		
		DataReader reader = new JsonReader(new File(fileName))
		.addField("ID", "/array/object/id")
        .addField("Name", "/array/object/name")
        .addField("ScreenName", "/array/object/screen_name")
        .addField("Location", "/array/object/location")
        .addField("Description", "/array/object/description")
        .addField("URL", "/array/object/url")
        .addField("EntityURL", "/array/object/entities/object/url/object/urls/array/object/url")
        .addField("EntityExpandedURL", "/array/object/entities/object/url/object/urls/array/object/expanded_url")
        .addField("Indices", "/array/object/entities/object/url/object/urls/array/object/indices/array/text()")
        .addField("DescriptionURL", "/array/object/entities/object/description/object/urls/array/object/url")
        .addField("DescriptionExpandedURL", "/array/object/entities/object/description/object/urls/array/object/expanded_url")
        .addField("DescriptionIndicesURL", "/array/object/entities/object/description/object/urls/array/object/indices/array/text()")
        .addField("Protected", "/array/object/protected")
        .addField("FollowersCount", "/array/object/followers_count")
        .addField("FriendsCount", "/array/object/friends_count")
        .addField("ListedCount", "/array/object/listed_count")
        .addField("CreatedAt", "/array/object/created_at")
        .addField("FavouritesCount", "/array/object/favourites_count")
        .addField("UTCOffSet", "/array/object/utc_offset")
        .addField("TimeZone", "/array/object/time_zone")	    
        .addField("GeoEnabled", "/array/object/geo_enabled")
        .addField("Verified", "/array/object/verified")
        .addField("StatusesCount", "/array/object/statuses_count")
        .addField("Language", "/array/object/lang")
        .addField("ContributorsEnabled", "/array/object/contributors_enabled")  
        .addField("IsTranslator", "/array/object/is_translator")
        .addField("IsTranslationEnabled", "/array/object/is_translation_enabled")    
        .addField("ProfileBackgroundImageURL", "/array/object/profile_background_image_url")
        .addField("ProfileBackgroundTitle", "/array/object/profile_background_tile")
        .addField("ProfileImageURL", "/array/object/profile_image_url")
        .addField("ProfileBannerURL", "/array/object/profile_banner_url")
        .addField("ProfileUseBackgroundImage", "/array/object/profile_use_background_image")
        .addField("DefaultProfile", "/array/object/default_profile")
        .addField("FollowRequestSent", "/array/object/follow_request_sent")
        .addField("ShowAllInlineMedia", "/array/object/show_all_inline_media")
        .addRecordBreak("/array/object");  
		
		DataWriter writer = new  CSVWriter(new File(fileName + "CSV.csv"));	      
		JobTemplate.DEFAULT.transfer(reader, writer); 
		
		reader.close();
		writer.close();
	}
}