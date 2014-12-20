import java.util.HashSet;

// interface
public interface ProcessingListener {
	public void onProcessFinished(int bufferId, HashSet<String> urls, int numScraped);
}