// Author: Yuan Du (yd2234@columbia.edu)
// Minor Author: Akshai Sarma (as4107@columbia.edu)
// Date: Oct 27, 2012
// Function: return the number of documents match the query in the given site

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.regex.*;
import org.apache.commons.codec.binary.Base64;

/* This class wraps the Bing searching functionality. */
public class BingSearch {
	String accountKey;

	public static void main(String[] args){
		String site = "cancer.org";
		String query = "application windows";
		String accountKey = "MWQrrA8YW+6ciAUTJh56VHz1vi/Mdqu0lSbzms3N7NY=";
		int number = new BingSearch(accountKey).getDocNum(site, query);
		System.out.println("number of docs is "+number);
	}

	public BingSearch (String key) {
		this.accountKey = key;
	}

	/*
	 * Return the xml output from Bing by the url
	 */
	public String searchBingResult(String bingUrl) throws IOException{

		byte[] accountKeyBytes = Base64.encodeBase64((accountKey + ":" + accountKey).getBytes());
		String accountKeyEnc = new String(accountKeyBytes);

		URL url = new URL(bingUrl);
		URLConnection urlConnection = url.openConnection();
		urlConnection.setRequestProperty("Authorization", "Basic " + accountKeyEnc);

		InputStream inputStream = (InputStream) urlConnection.getContent();
		byte[] contentRaw = new byte[urlConnection.getContentLength()];
		inputStream.read(contentRaw);
		String content = new String(contentRaw);

		//The content string is the xml/json output from Bing.
		return content;
	}

	/*
	 * Parse the xml output from Bing into the number of docs
	 */
	private int getTotal (String content){
		int number = 0;
		Pattern p = Pattern.compile(".*<d:WebTotal m:type=\"Edm.Int64\">([0-9]+)</d:WebTotal>.*");
		Matcher m = p.matcher(content);
		if (m.find()){
			// System.out.println(m.group(1));
			number = Integer.valueOf(m.group(1));
		}
		return number;
	}

	/*
	 * Get the number of documents match the query in the given site
	 */
	public int getDocNum(String site, String query){
		query = query.replace(" ", "+");
		int number = 0;
		// define the url for Bing search
		String bingUrl = "https://api.datamarket.azure.com/Data.ashx/Bing/SearchWeb/v1/Composite?Query=%27site%3a"
		+site+"%20"+query+"%27&$top=10&$format=Atom";
		// System.out.println(bingUrl);
		try{
			// search Bing
			String content = searchBingResult(bingUrl);
			// String content = "null";
			// System.out.println(content);
			number = getTotal(content);
		}catch(IOException e){
			e.printStackTrace();
		}
		return number;
	}

	/*
	 * Returns the top four urls of documents that match the query in the given site
	 */

	public ArrayList<String> getTopFour(String site, String query) {
		ArrayList<String> result = new ArrayList<String>();
		String bingQuery = "https://api.datamarket.azure.com/Data.ashx/Bing/SearchWeb/v1/Web?Query=%27site%3a"
							+ site + "%20" + query.replace(" ", "+") + "%27&$top=4&$format=Atom";
		try {
			String content = searchBingResult(bingQuery);
			Pattern p = Pattern.compile("<d:Url m:type=\"Edm.String\">(.+?)</d:Url>");
			Matcher m = p.matcher(content);
			while (m.find())
				result.add(m.group(1));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

}
