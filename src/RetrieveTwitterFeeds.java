
import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.codehaus.jackson.JsonParseException;
import org.json.CDL;
import org.json.JSONArray;
import org.json.JSONException;
import org.apache.log4j.Logger;

import antlr.TokenStream;



//import org.codehaus.jackson.map.ObjectMapper;
//import org.codehaus.jackson.map.DeserializationConfig.Feature;
//import sun.org.mozilla.javascript.internal.json.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.northconcepts.datapipeline.core.DataReader;
import com.northconcepts.datapipeline.core.DataWriter;
import com.northconcepts.datapipeline.core.StreamWriter;
import com.northconcepts.datapipeline.csv.CSVWriter;
import com.northconcepts.datapipeline.job.JobTemplate;
import com.northconcepts.datapipeline.json.JsonReader;
import com.northconcepts.datapipeline.transform.BasicFieldTransformer;
import com.northconcepts.datapipeline.transform.TransformingReader;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
//import org.codehaus.jackson.jaxrs.JacksonJaxbJsonProvider;

//import org.codehaus.jackson.map.JsonMappingException;

 
public class RetrieveTwitterFeeds 
{
	  static String AccessToken = "22440636-aAsWewFH4XsVldICls5Bpy1LTgiaIuXIxWqBRWgtb";
	  static String AccessSecret = "xuhcY9h77v6fpZuIXNQj6MDH1BjjJbpA7RcjZZXg1eSbd";
	  static String ConsumerKey = "jXIycnRHrIFZQ6FbDdLZ0raaS";
	  static String ConsumerSecret = "6sRLrmEtbaoGAlNyXYFo1Rgb9FeuDYbn7hccHZsDfBAXyNfyXA";
 
	/**
	 * @param args
	 */
	
	public static void main(String[] args) throws Exception
	{
		OAuthConsumer consumer = new CommonsHttpOAuthConsumer(
                ConsumerKey,
                ConsumerSecret);
        consumer.setTokenWithSecret(AccessToken, AccessSecret);
       // getTweets(consumer,"https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=guardian&count=1");
        createCSV();
        //createTwitterObjects(); 
	}

	private static void createCSV() {
		 String url = "http://www.google.com/finance/info?client=ig&q=msft,orcl,adbe";
		 
	        BufferedReader input;
			try {
				input = new BufferedReader(new InputStreamReader(new URL(url).openStream(), "UTF-8"));
				  // remove preceding slashes from stream
		        input.readLine();
		        input.read();
		        input.read();
		        DataReader reader = new JsonReader(input)
	            .addField("symbol", "//array/object/t")
	            .addField("exchange", "//array/object/e")
	            .addField("price", "//array/object/l")
	            .addField("change", "//array/object/c")
	            .addRecordBreak("//array/object");
	         
	        reader = new TransformingReader(reader)
	            .add(new BasicFieldTransformer("price").stringToDouble())
	            .add(new BasicFieldTransformer("change").stringToDouble());
	            
	         
	        DataWriter writer = new  CSVWriter(new OutputStreamWriter(System.out));
	 
	        JobTemplate.DEFAULT.transfer(reader, writer);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	       
	      
	         
	 
	        
		
	}

	public static void getTweets(OAuthConsumer OAuthConsumerKeyandSecret, String requestString) throws OAuthCommunicationException,OAuthExpectationFailedException, OAuthMessageSignerException
	{
		HttpGet request = new HttpGet(requestString);
		OAuthConsumerKeyandSecret.sign(request);
 
        HttpClient client = HttpClientBuilder.create().build();
        try {
			HttpResponse response = client.execute(request);
			int statusCode = response.getStatusLine().getStatusCode();
			String JSONString = IOUtils.toString(response.getEntity().getContent());
			
			Writer jsonObjectWriter = new BufferedWriter(new FileWriter("../TrustMetricsTwitter/files/JSONData.txt",true));
			jsonObjectWriter.append(JSONString);
			jsonObjectWriter.close();
			
			
		    // String csv = CDL.rowToString(JSONString);
		   //  FileUtils.writeStringToFile(file, csv);
		//	JSONArray array = CDL.toJSONArray(JSONString);
		   //  System.out.println(array);
	        //System.out.println(statusCode + ":" + response.getStatusLine().getReasonPhrase());
	        //System.out.println(JSONString);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}/* catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	*/	
	}
	
	private static void createTwitterObjects() {
		// TODO Auto-generated method stub
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
		
		List<TwitterStream> publisheDataList;
		
		try {
			TwitterStream[] stream = mapper.readValue(new File("../TrustMetricsTwitter/files/JSONData.txt"), TwitterStream[].class);
			for (int i=0; i<stream.length ; i++){
			System.out.println(stream[i]);}
		} catch (com.fasterxml.jackson.core.JsonParseException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (JsonMappingException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
	    //SimpleModule module = new SimpleModule(JacksonList.class.getName());
	   // module.setMixInAnnotation(Object.class, Mixins.class);

	   // mapper.registerModule(module);

	  /*  try {
			testWriteReadObject(mapper, new TwitterStream("foo"), TwitterStream.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	    ArrayList<TwitterStream> ldv = new ArrayList<TwitterStream>();
	    ldv.add(new TwitterStream("name1"));
	    ldv.add(new TwitterStream("name2"));

	    try {
			testWriteReadObject(mapper, ldv, ArrayList.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		//try {
			//TwitterStream twitterStream = mapper.readValue(new File("../TrustMetricsTwitter/files/JSONData.txt"), TwitterStream.class);
			
		/*	try {
				publisheDataList = mapper.convertValue(new FileReader("../TrustMetricsTwitter/files/JSONData.txt"), mapper.getTypeFactory().constructCollectionType(List.class, TwitterStream.class));
				System.out.println(publisheDataList);
			} catch (IllegalArgumentException | FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
	}
	
	// Look at tests to see if they are necessary uncomment below 
	/*private static void testWriteReadObject(ObjectMapper mapper, Object toTest, Class<?> objectClass)
	        throws JsonProcessingException, IOException, JsonParseException, JsonMappingException {
	    String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(toTest);
	    System.out.println(json);
	    Object o = mapper.readValue(json, objectClass);
	    if (o.equals(toTest)) 
	        System.out.println("Objects are equal");
	    else
	        System.err.println("Objects are NOT equal");
	}*/
}