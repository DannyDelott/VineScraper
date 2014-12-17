import java.util.HashSet;

import twitter4j.Status;

public class TweetBuffer {

	private HashSet<Status> tweets;
	private boolean isProcessing;

	// constructor
	public TweetBuffer() {
		tweets = new HashSet<Status>();
		isProcessing = false;
	}

	// public methods
	public void addStatus(Status status) {
		tweets.add(status);
	}

	public void clearBuffer() {
		tweets.clear();
	}

	// getters
	public HashSet<Status> getTweets() {
		return tweets;
	}

	public boolean isProcessing() {
		return isProcessing;
	}

	// setters
	public void setProcessing(boolean isProcessing) {
		this.isProcessing = isProcessing;
	}
}