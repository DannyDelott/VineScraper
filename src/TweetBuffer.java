import java.util.HashSet;

import twitter4j.Status;

public class TweetBuffer {

	private int id;
	private String saveDirectory;

	private HashSet<Status> tweets;
	private boolean isProcessing;

	// constructor
	public TweetBuffer(int id, String saveDirectory) {
		this.id = id;
		this.saveDirectory = saveDirectory;

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

	public int getId() {
		return id;
	}

	public String getSaveDirectory() {
		return saveDirectory;
	}

	// setters
	public void setProcessing(boolean isProcessing) {
		this.isProcessing = isProcessing;
	}
}