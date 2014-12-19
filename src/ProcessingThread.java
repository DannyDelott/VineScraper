import java.io.IOException;
import java.util.HashSet;

import twitter4j.Status;

public class ProcessingThread implements Runnable {

	private HashSet<Status> tweets;
	private int buffer;
	private HashSet<String> urls;
	private int numVinesScraped = 0;
	private String saveDirectory;
	private ProcessingListener listener;

	// constructor
	public ProcessingThread(ProcessingBundle bundle, HashSet<String> urls,
			ProcessingListener listener) {

		this.tweets = new HashSet<Status>(bundle.getTweets());
		this.buffer = bundle.getBuffer();
		this.saveDirectory = bundle.getSaveDirectory();
		this.urls = urls;
		this.listener = listener;
	}

	// interface
	public interface ProcessingListener {
		public void onProcessFinished(int buffer, HashSet<String> urls,
				int scraped);
	}

	@Override
	public void run() {

		String url, downloadUrl;
		String html = null;

		for (Status status : tweets) {

			// gets the Vine URL (eg: https://vine.co/v/OW0ei1Uauxv)
			url = VineUtil.findVineUrl(status);
			if (url == null) {
				continue;
			}

			// checks if Vine URL is duplicate
			if (urls.contains(url)) {
				continue;
			} else {
				synchronized (this) {
					urls.add(url);
				}
			}

			// gets HTML from Vine URL
			try {
				html = VineUtil.sendGet(url);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (html == null) {
				continue;
			}

			// parses out the download URL
			downloadUrl = VineUtil.parseDownloadUrl(html);
			if (downloadUrl == null) {
				continue;
			}

			// downloads the .mp4 video
			if (VineUtil.downloadVine(saveDirectory, status.getId(),
					downloadUrl)) {
				synchronized (this) {
					numVinesScraped++;
				}
			}
		}

		// invokes finished listener
		listener.onProcessFinished(buffer, urls, numVinesScraped);
	}

	// getters
	public int getNumVinesScraped() {
		return numVinesScraped;
	}

	public HashSet<String> getUrls() {
		return urls;
	}
}
