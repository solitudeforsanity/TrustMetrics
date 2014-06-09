
import twitter4j.FilterQuery;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.TwitterException;
import twitter4j.*;

public class TrustMetricsTwitter {
    public static void main(String[] args) {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true);
        cb.setOAuthConsumerKey("jXIycnRHrIFZQ6FbDdLZ0raaS");
        cb.setOAuthConsumerSecret("6sRLrmEtbaoGAlNyXYFo1Rgb9FeuDYbn7hccHZsDfBAXyNfyXA");
        cb.setOAuthAccessToken("22440636-aAsWewFH4XsVldICls5Bpy1LTgiaIuXIxWqBRWgtb");
        cb.setOAuthAccessTokenSecret("xuhcY9h77v6fpZuIXNQj6MDH1BjjJbpA7RcjZZXg1eSbd");

        TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();

        StatusListener listener = new StatusListener() {

            @Override
            public void onException(Exception arg0) {
                arg0.printStackTrace();

            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice arg0) {
                System.out.println("Got a status deletion notice id:" + arg0);

            }

		    public void onScrubGeo(long arg0, long arg1) {
		         System.out.println("Got scrub_geo event userId:" + arg0 + " upToStatusId:" + arg1);

		    }
			
            @Override
            public void onStatus(Status status) {
                User user = status.getUser();
                
                // gets Username
                String username = status.getUser().getScreenName();
                System.out.println(username);
                String profileLocation = user.getLocation();
                System.out.println(profileLocation);
                long tweetId = status.getId(); 
                System.out.println(tweetId);
                String content = status.getText();
                System.out.println(content +"\n");

            }

            @Override
            public void onTrackLimitationNotice(int arg0) {
                System.out.println("Got track limitation notice:" + arg0);

            }
			
            @Override
            public void onStallWarning(StallWarning warning) {
                System.out.println("Warning:" + warning);

            }

        };
       FilterQuery fq = new FilterQuery();
    
       String keywords[] = {"England","Paris"};

        fq.track(keywords);

        twitterStream.addListener(listener);
        twitterStream.filter(fq);  

    }
}