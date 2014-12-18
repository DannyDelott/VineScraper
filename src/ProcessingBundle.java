import java.util.HashSet;

import twitter4j.Status;

public class ProcessingBundle {

	private HashSet<Status> tweets;
	private int buffer;
	private int numVinesScraped;
	private String savedDirectory;

	// constructor
	public ProcessingBundle(HashSet<Status> tweets, int buffer,
			int numVinesScraped, String saveDirectory) {
		this.tweets = tweets;
		this.buffer = buffer;
		this.numVinesScraped = numVinesScraped;
		this.savedDirectory = saveDirectory;
	}

	// getters
	public HashSet<Status> getTweets() {
		return tweets;
	}

	public int getBuffer() {
		return buffer;
	}

	public int getNumVinesScraped() {
		return numVinesScraped;
	}

	public String getSaveDirectory() {
		return savedDirectory;
	}

}
