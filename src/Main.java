import twitter4j.FilterQuery;
import twitter4j.TwitterStream;

public class Main {

	private static TwitterStream twitter;

	public static void main(String[] args) {

		// Connects to the Streaming API
		twitter = TwitterStreamBuilderUtil.getStream();

		// sets keyword to track
		FilterQuery fq = new FilterQuery();
		String keyword[] = { "http" };
		fq.track(keyword);
	}

}
