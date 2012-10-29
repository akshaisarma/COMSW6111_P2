// Author: Yuan Du (yd2234@columbia.edu)
// Date: Oct 27, 2012
// Function: return the number of documents match the query in the given site

// package columbia.edu.adb;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.*;

//Download and add this library to the build path.
import org.apache.commons.codec.binary.Base64;

public class BingSearch {
	String accountKey;

	public static void main(String[] args){
		String site = "cancer.org";
		String query = "application windows";
		String accountKey = "MWQrrA8YW+6ciAUTJh56VHz1vi/Mdqu0lSbzms3N7NY=";
		int number = new BingSearch().getDocNum(site, query, accountKey);
		System.out.println("number of docs is "+number);
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
	public int parseXML(String content){
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
	public int getDocNum(String site, String query, String accountKey){
		this.accountKey = accountKey; 
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
			number = parseXML(content);
		}catch(IOException e){
			e.printStackTrace();
		}
		return number;
	}

}
