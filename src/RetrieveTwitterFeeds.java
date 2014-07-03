
import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
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
import com.northconcepts.datapipeline.core.Messages;
import com.northconcepts.datapipeline.core.StreamWriter;
import com.northconcepts.datapipeline.csv.CSVReader;
import com.northconcepts.datapipeline.csv.CSVWriter;
import com.northconcepts.datapipeline.job.JobTemplate;
import com.northconcepts.datapipeline.json.JsonReader;
import com.northconcepts.datapipeline.transform.BasicFieldTransformer;
import com.northconcepts.datapipeline.transform.SetField;
import com.northconcepts.datapipeline.transform.TransformingReader;
import com.northconcepts.datapipeline.core.Field;
import com.northconcepts.datapipeline.core.Record;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;

public class RetrieveTwitterFeeds 
{
	  static String AccessToken = "22440636-aAsWewFH4XsVldICls5Bpy1LTgiaIuXIxWqBRWgtb";
	  static String AccessSecret = "xuhcY9h77v6fpZuIXNQj6MDH1BjjJbpA7RcjZZXg1eSbd";
	  static String ConsumerKey = "jXIycnRHrIFZQ6FbDdLZ0raaS";
	  static String ConsumerSecret = "6sRLrmEtbaoGAlNyXYFo1Rgb9FeuDYbn7hccHZsDfBAXyNfyXA";
	  static Hashtable<String, String> twitterAccountDetails = new Hashtable<String, String>();
	  public static final Logger log = DataEndpoint.log; 
	
	public static void main(String[] args) throws Exception
	{
		OAuthConsumer consumer = new CommonsHttpOAuthConsumer(ConsumerKey, ConsumerSecret);
        consumer.setTokenWithSecret(AccessToken, AccessSecret);

        File file = new File("../TrustMetricsTwitter/files/LastTweetID.txt");
    	String[][] arrayLine = new String[5][2];
        
        if (file.length() > 0) {
        	BufferedReader br = new BufferedReader(new FileReader("../TrustMetricsTwitter/files/LastTweetID.txt"));        
        	String[] word = new String[10]; 
        	String line;
        	String wholeString = ""; 
        	while ((line = br.readLine()) != null) {
        		wholeString += line + " ";
        	}
        
        	word = wholeString.split("[\\p{Punct}\\s]+");
        	
        	int count = 0;
        	for (int i=0; i<5; i++){
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
        	twitterAccountDetails.put("Swansea","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=Swansea_Alumni&include_entities=true&include_rts=true&count=2");
        	twitterAccountDetails.put("Guardian","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=guardian&count=2&include_entities=true&include_rts=true");
        	twitterAccountDetails.put("CNN","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=CNN&count=2&include_entities=true&include_rts=true");
        	twitterAccountDetails.put("BBCWorld","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=BBCWorld&count=2&include_entities=true&include_rts=true");
        	twitterAccountDetails.put("FinancialTimes","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=FinancialTimes&count=2&include_entities=true&include_rts=true");
        }
        else 
        {	
        	System.out.println("Inside one arrayline");
        	firstTime = false; 
        	twitterAccountDetails.put("Swansea","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=Swansea_Alumni&include_entities=true&include_rts=true&count=2&since_id="+arrayLine[0][1]);
        	twitterAccountDetails.put("Guardian","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=guardian&count=2&include_entities=true&include_rts=true&since_id="+arrayLine[1][1]);
        	twitterAccountDetails.put("CNN","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=CNN&count=2&include_entities=true&include_rts=true&since_id="+arrayLine[2][1]);
        	twitterAccountDetails.put("BBCWorld","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=BBCWorld&count=2&include_entities=true&include_rts=true&since_id="+arrayLine[3][1]);
        	twitterAccountDetails.put("FinancialTimes","https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=FinancialTimes&count=2&include_entities=true&include_rts=true&since_id="+arrayLine[4][1]);
        }
        
        System.out.println(twitterAccountDetails);
        
        for (String key : twitterAccountDetails.keySet()){
        	getTweetsInJSON(consumer, key, twitterAccountDetails.get(key));
        	saveLastID(key,firstTime); 
        }
        
        for (String key : twitterAccountDetails.keySet()){
        	createCSVTwitter(key);       	
        }
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
			System.out.println("Writing Status Code For Retreiving Tweets from " + twitterAccountName + " : " + statusCode);
			PrintWriter jsonObjectWriter = new PrintWriter("../TrustMetricsTwitter/files/"+twitterAccountName+"JSON");
			jsonObjectWriter.write(JSONString);
			jsonObjectWriter.close();			
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	private static void saveLastID(String twitterStreamKey, boolean firstTime) {
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);		
		try {
			TwitterStream[] stream = mapper.readValue(new File("../TrustMetricsTwitter/files/" + twitterStreamKey + "JSON"), TwitterStream[].class);	
			
			if (stream != null && stream.length > 0)
			{
				String line = null; 
				String oldText = "";
				//
				if (firstTime == false) {
					BufferedReader fileReader = new BufferedReader(new FileReader("../TrustMetricsTwitter/files/LastTweetID.txt"));
					while ((line = fileReader.readLine()) != null)
					{
						oldText += line + "\n";					
					}
					fileReader.close();
				
					String newText = oldText.replaceAll(twitterStreamKey + "\\s.*", twitterStreamKey + "  " + stream[0].getId());
					System.out.println("INSIDE THIS");
					PrintWriter writer = new PrintWriter("../TrustMetricsTwitter/files/LastTweetID.txt");
					writer.print(newText);
					writer.close();
				}
				else 
				{
					FileWriter writer = new FileWriter("../TrustMetricsTwitter/files/LastTweetID.txt",true);
					String newText = twitterStreamKey + " " + stream[0].getId() + "\n";
					System.out.println("INSIDE ELSE");
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

	private static void createCSVTwitter(String twitterUserKey) throws FileNotFoundException {
	       
		String FILE = "../TrustMetricsTwitter/files/" + twitterUserKey + "JSON";
				
		DataReader reader = new JsonReader(new File(FILE))
	            .addField("CreatedAt", "//array/object/created_at")
	            .addField("ID", "//array/object/id")
	            .addField("IDString", "//array/object/id_str")
	           //.addField("Source", "//array/object/text")
	            .addField("Source", "//array/object/source")
	            .addField("Truncated", "//array/object/truncated")
	            .addField("InReplyToStatusID", "//array/object/in_reply_to_status_id")
	            .addField("InReplyToStatusIDString", "//array/object/in_reply_to_status_id_str")
	            .addField("InReplyToUserID", "//array/object/in_reply_to_user_id")
	            .addField("InReplyToUserIDString", "//array/object/in_reply_to_user_id_str")
	            .addField("InReplyToScreenName", "//array/object/in_reply_to_screen_name")
	            .addField("USERDATA", "//array/object/user/id")
	            .addField("GEO", "//array/object/geo")
	            .addField("Coordinates", "//array/object/coordinates")
	            .addField("Place", "//array/object/place")
	            .addField("Contributors", "//array/object/contributors")
	            .addField("RetweetCount", "//array/object/retweet_count")
	            .addField("FavouriteCount", "//array/object/favorite_count")
	            .addField("Favourited", "//array/object/favorited")
	            .addField("Retweeted", "//array/object/retweeted")
	            .addField("PossiblySensitive", "//array/object/possibly_sensitive")
	            .addField("Language", "//array/object/lang")
	            .addRecordBreak("//array/object");
		        
	       DataWriter writer = new  CSVWriter(new File("../TrustMetricsTwitter/files/CSVData.csv"));	        
	       System.out.println(writer);
	       JobTemplate.DEFAULT.transfer(reader, writer);					
	}
	
	public static void transformData(){
		 DataReader readerModifier = new CSVReader(new File("../TrustMetricsTwitter/files/CSVData.csv"))
         .setFieldNamesInFirstRow(true);
     
     TransformingReader transformingReader = new TransformingReader(readerModifier);
     
     // Since CSV fields are strings, they need to be parsed before subtraction
     transformingReader.add(new SetField("Id", 
             "number"));
    // DataWriter writerModify = new  CSVWriter(new File("../TrustMetricsTwitter/files/CSVData.csv"));
     JobTemplate.DEFAULT.transfer(transformingReader, new StreamWriter(System.out));
     log.info(Messages.getCurrent());
	}
}