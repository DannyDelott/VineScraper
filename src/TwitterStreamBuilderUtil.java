import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterStreamBuilderUtil {

	public static TwitterStream getStream() {

		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true);
		cb.setOAuthConsumerKey("*************************");
		cb.setOAuthConsumerSecret("**************************************************");
		cb.setOAuthAccessToken("**************************************************");
		cb.setOAuthAccessTokenSecret("*********************************************");

		return new TwitterStreamFactory(cb.build()).getInstance();
	}

}
