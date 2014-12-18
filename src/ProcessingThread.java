import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;

import org.apache.commons.io.FileUtils;

import twitter4j.Status;
import twitter4j.URLEntity;

public class ProcessingThread implements Runnable {

	private HashSet<Status> tweets;
	private int buffer;
	private HashSet<String> urls;
	private int numVinesScraped;
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
			url = getVineUrl(status);
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
				html = sendGet(url);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (html == null) {
				continue;
			}

			// parses out the download URL
			downloadUrl = parseDownloadUrl(html);
			if (downloadUrl == null) {
				continue;
			}

			// downloads the .mp4 video
			try {
				File f = new File(saveDirectory + status.getId() + ".mp4");
				FileUtils.copyURLToFile(new URL(downloadUrl), f);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			// increases total quantity of vines collected
			synchronized (this) {
				numVinesScraped++;
			}
		}

		// invokes finished listener
		listener.onProcessFinished(buffer, urls, numVinesScraped);
	}

	// private methods
	private String getVineUrl(Status status) {

		String vineUrl;

		for (URLEntity url : status.getURLEntities()) {
			if (url.getExpandedURL().contains("vine.co/v/")) {
				vineUrl = url.getExpandedURL();
				return vineUrl;
			}
		}

		return null;
	}

	private static String parseDownloadUrl(String html) {

		// if 404 or the url suffix is missing from the html
		if (html == null || html.contains("Page not found")
				|| html.contains("<title>Vine</title>")) {
			return null;
		}

		// parses out the .mp4 file path
		try {
			String[] split = html.split("\"twitter:player:stream\" content=\"");
			split = split[1].split("\\?versionId");
			return split[0];

		} catch (ArrayIndexOutOfBoundsException e1) {
			e1.printStackTrace();
		}

		return null;
	}

	private static String sendGet(String u) throws IOException {
		String url = u;
		String USER_AGENT = "Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2049.0 Safari/537.36";
		URL obj = new URL(url);

		try {
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			// handles the response code 301 redirect from vine URLs
			String redirect = con.getHeaderField("Location");
			if (redirect != null) {
				obj = new URL(redirect);
				con = (HttpURLConnection) obj.openConnection();
			}

			con.setRequestMethod("GET");
			con.setRequestProperty("User-Agent", USER_AGENT);

			int responseCode = con.getResponseCode();
			if (responseCode > 200) {
				return null;
			}

			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			return response.toString();

		} catch (Exception e) {
			return null;
		}
	}

	// getters
	public int getNumVinesScraped() {
		return numVinesScraped;
	}

	public HashSet<String> getUrls() {
		return urls;
	}
}
