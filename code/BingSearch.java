// package columbia.edu.adb;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

//Download and add this library to the build path.
import org.apache.commons.codec.binary.Base64;

public class BingSearch {

	public static void main(String[] args){
		String site = "afifa.com";
		String query = "premiership";
		int number = getDocNum(site, query);
		System.out.println(number);
	}
	/*
	 * Return the xml output from Bing by the url
	 */ 
	public static String searchBingResult(String bingUrl) throws IOException{
		String accountKey = "MWQrrA8YW+6ciAUTJh56VHz1vi/Mdqu0lSbzms3N7NY=";
		
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
	public static int parseXML(String content){
		int number = 10;
		return number;
	}
	/*
	 * Get the number of documents match the query in the given site
	 */
	public static int getDocNum(String site, String query){
		int number = 0;
		// define the url for Bing search
		String bingUrl = "https://api.datamarket.azure.com/Data.ashx/Bing/SearchWeb/v1/Composite?Query=%27site%3"
		+site+"%20"+query+"%27&$top=10&$format=Atom";
		try{
			// search Bing
			String content = searchBingResult(bingUrl);
			System.out.println(content);
			number = parseXML(content);
		}catch(IOException e){
			e.printStackTrace();
		}
		return number;
	}

}
