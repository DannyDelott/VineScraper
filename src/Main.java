import java.util.HashSet;

import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;

public class Main {
	private static final int NUM_VINES_TO_DOWNLOAD = -1;
	private static final int MIN_BUFFER_SIZE = 10;

	private static TwitterStream twitter;
	private static int numVinesScraped = 0;
	private static final String SAVE_DIRECTORY = "vines/";

	private static TweetBuffer buffer1, buffer2;
	private static int currentBufferId;
	private static HashSet<String> duplicateUrls;

	private static ProcessingListener finishListener = new ProcessingListener() {

		@Override
		public void onProcessFinished(int bufferId, HashSet<String> urls,
				int numScraped) {

			synchronized (this) {
				duplicateUrls = urls;
			}

			numVinesScraped += numScraped;
			if (numScraped != 0) {
				System.out.println("Scraped " + numScraped
						+ " vine(s) from buffer" + bufferId
						+ ".  Total vines is now " + numVinesScraped + ".");
			}

			switch (bufferId) {
			case 1:
				buffer1.clearBuffer();
				buffer1.setProcessing(false);
				break;
			case 2:
				buffer2.clearBuffer();
				buffer2.setProcessing(false);
				break;
			}

			// stop scraping when threshold is reached
			if (numVinesScraped >= NUM_VINES_TO_DOWNLOAD
					&& (NUM_VINES_TO_DOWNLOAD != -1)) {
				twitter.cleanUp();
				twitter.shutdown();
			}
		}
	};

	public static void main(String[] args) {

		// Connects to the Streaming API
		twitter = TwitterStreamBuilderUtil.getStream();

		// sets keyword to track
		FilterQuery fq = new FilterQuery();
		String keyword[] = { "http" };
		fq.track(keyword);

		// instantiates buffers
		buffer1 = new TweetBuffer(1, SAVE_DIRECTORY);
		buffer2 = new TweetBuffer(2, SAVE_DIRECTORY);
		currentBufferId = 1;
		duplicateUrls = new HashSet<String>();

		// instantiates listener to fill and process the buffers
		StatusListener listener = new StatusListener() {

			@Override
			public void onStatus(Status status) {

				if ((currentBufferId == 1) && !buffer1.isProcessing()) {
					buffer1.addStatus(status);
					return;
				}

				if ((currentBufferId == 2) && !buffer2.isProcessing()) {
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

	private static void processCurrentBuffer() {

		// Does nothing if the current buffer is already processing or
		// if it contains fewer elements than the minimum buffer size.
		if ((currentBufferId == 1 && (buffer1.isProcessing() || buffer1
				.getTweets().size() < MIN_BUFFER_SIZE))
				|| (currentBufferId == 2 && (buffer2.isProcessing() || buffer2
						.getTweets().size() < MIN_BUFFER_SIZE))) {
			return;
		}

		// Otherwise, begin processing the current buffer.
		try {
			switch (currentBufferId) {
			case 1:
				buffer1.setProcessing(true);
				currentBufferId = 2;

				Thread t1 = new Thread(new ProcessingThread(buffer1,
						duplicateUrls, finishListener));
				t1.start();
				break;

			case 2:
				buffer2.setProcessing(true);
				currentBufferId = 1;

				Thread t2 = new Thread(new ProcessingThread(buffer2,
						duplicateUrls, finishListener));
				t2.start();
				break;
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
}
