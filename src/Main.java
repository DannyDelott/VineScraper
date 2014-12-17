import java.util.HashSet;

import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;

public class Main {

	private static TwitterStream twitter;

	private static TweetBuffer buffer1;
	private static TweetBuffer buffer2;
	private static int currentBuffer;
	private static HashSet<String> urls;

	public static void main(String[] args) {

		// Connects to the Streaming API
		twitter = TwitterStreamBuilderUtil.getStream();

		// sets keyword to track
		FilterQuery fq = new FilterQuery();
		String keyword[] = { "http" };
		fq.track(keyword);

		// instantiates buffers
		buffer1 = new TweetBuffer();
		buffer2 = new TweetBuffer();
		currentBuffer = 1;
		urls = new HashSet<String>();

		// instantiates listener to fill and process the buffers
		StatusListener listener = new StatusListener() {

			@Override
			public void onStatus(Status status) {

				if ((currentBuffer == 1) && !buffer1.isProcessing()) {
					buffer1.addStatus(status);
					return;
				}

				if ((currentBuffer == 2) && !buffer2.isProcessing()) {
					buffer2.addStatus(status);
					return;
				}
			}

			@Override
			public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
				processCurrentBuffer(); // see Step 4
			}

			@Override
			public void onDeletionNotice(StatusDeletionNotice deletionNotice) {
			}

			@Override
			public void onException(Exception ex) {
				ex.printStackTrace();
			}

			@Override
			public void onScrubGeo(long arg0, long arg1) {
			}

			@Override
			public void onStallWarning(StallWarning arg0) {
				System.out.println(arg0.getMessage());
			}
		};

		// starts scraping Vine videos
		twitter.addListener(listener);
		twitter.filter(fq);
	}
}
