import java.util.HashSet;

import twitter4j.Status;

public class ProcessingBundle {

	private HashSet<Status> tweets;
	private int buffer;
	private String saveDirectory;

	// constructor
	public ProcessingBundle(HashSet<Status> tweets, int buffer,
			String saveDirectory) {
		this.tweets = tweets;
		this.buffer = buffer;
		this.saveDirectory = saveDirectory;
	}

	// getters
	public HashSet<Status> getTweets() {
		return tweets;
	}

	public int getBuffer() {
		return buffer;
	}

	public String getSaveDirectory() {
		return saveDirectory;
	}

}
