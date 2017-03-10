import java.io.File;

import java.io.FileNotFoundException;

import java.io.FileWriter;

import java.io.IOException;

import java.io.PrintWriter;

import java.util.regex.Pattern;

import edu.uci.ics.crawler4j.crawler.CrawlController;

import edu.uci.ics.crawler4j.crawler.Page;

import edu.uci.ics.crawler4j.crawler.WebCrawler;

import edu.uci.ics.crawler4j.fetcher.PageFetchResult;

import edu.uci.ics.crawler4j.fetcher.PageFetcher;

import edu.uci.ics.crawler4j.parser.HtmlParseData;

import edu.uci.ics.crawler4j.url.WebURL;

import java.util.*;

import com.opencsv.CSVWriter;

public class MyCrawler extends WebCrawler {
	
	static CrawlStat cs=new CrawlStat();

	static CSVWriter writer, writer2, writer3;

	static int linkcounter = 0;

	static int filesize = 0;

	static boolean check = true;

	static String content = null;

	static int filefetcher = 0, filefetchersucceded = 0, filefailed = 0, failaborted = 0, inwebsite = 0, outwebsite = 0;

	static int OK = 0, Moved = 0, Unauthorized = 0, Forbidden = 0, NotFound = 0;

	static HashMap<String, Integer> hmap = new HashMap<>();

	static int lessthan1 = 0, oneto10 = 0, tento100 = 0, hundredto1 = 0, morethanone = 0;

	static int htmlval = 0, gif = 0, jpeg = 0, png = 0, pdf = 0;
	
	static int grandtotalextracted=0;

	public MyCrawler() throws Exception

	{

		//String csvFile1 = "fetch_latimes.csv";

		//writer = new CSVWriter(new FileWriter(csvFile1));

		//String entry1[] = { "URL", "HTTP STATUS-CODE" };

		//writer.writeNext(entry1);

		//String csvFile2 = "visit_latimes.csv";

		//writer2 = new CSVWriter(new FileWriter(csvFile2));

		//String entry2[] = { "URL", "SIZE OF FILE", "NUMBER OF OUTLINKS", "CONTENT-TYPE" };

		//writer2.writeNext(entry2);

		//String csvFile3 = "urls_latimes.csv";

	//	writer3 = new CSVWriter(new FileWriter(csvFile3));

		//String entry3[] = { "URL", "Indicator" };

		//writer3.writeNext(entry3);

	}

	private final static Pattern FILTERS = Pattern.compile(".*(\\.(html|doc|pdf|jpg"

		+ "|png|jpeg|gif|bmp))$");
	//private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg"
		//	 + "|png|mp3|mp3|zip|gz))$");





	@Override

	public boolean shouldVisit(Page page, WebURL url) {

		// TODO Auto-generated method stub

		String href = url.getURL().toLowerCase();

		if (href.startsWith("http://www.usc.edu/"))
			check = true;
		else
			check = false;
		if (!hmap.containsKey(url.toString().toLowerCase()))

		{
			if (check == true)
				inwebsite++;
			else
				outwebsite++;

			hmap.put(url.toString().toLowerCase(), 1);
		}
		//String arr[] = new String[2];
		//arr[0] = url.toString();
		/*
		if (check == true)
			arr[1] = "OK";
		else
			arr[1] = "N_OK";
		//writer3.writeNext(arr);
		 * 
		 */

		return FILTERS.matcher(href).matches() && (href.startsWith("http://www.usc.edu/")) ;

	}

	@Override

	public void visit(Page page) {

		// TODO Auto-generated method stub

		String url = page.getWebURL().getURL();

		// System.out.println("URL: " + url);

		if (page.getParseData() instanceof HtmlParseData) {

			HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();

			String text = htmlParseData.getText();

			String html = htmlParseData.getHtml();

			Set<WebURL> links = htmlParseData.getOutgoingUrls();
			
			
			grandtotalextracted+=links.size();
			
			linkcounter = links.size();
			
			filesize = html.getBytes().length;

			content = page.getContentType();
			System.out.println("checking "+page.getWebURL().getURL()+" linkcounter "+linkcounter+" content "+content+" statuscode "+page.getStatusCode());
			ArrayList<String> entries2 = new ArrayList<>();
			String web=page.getWebURL().toString();
			if(page.getWebURL().toString().contains(","))
				web=page.getWebURL().toString().replace(",", "-");
			
			entries2.add(web);

			entries2.add(filesize + "");

			entries2.add(linkcounter + "");

			entries2.add(content);
			
			String arr[] = new String[entries2.size()];

			for (int i = 0; i < entries2.size(); i++)
				arr[i] = entries2.get(i);
			cs.add2(arr);
			//writer2.writeNext(arr);
			
			float kb = filesize / 1024;

			float mb = kb / 1024;

			if(filesize<1024)
		    	   lessthan1++;
		       else if(filesize>=1024 && filesize<10*1024)
		    	   oneto10++;
		       else if(filesize>=10*1024 && filesize<100*1024)
		    	   tento100++;
		       else if(filesize>=100*1024 && filesize<1024*1000)
		    	   hundredto1++;
		       else if(filesize>=1024*1000)
		    	   morethanone++;

			System.out.println("<1 " + lessthan1 + " 1to10 " + oneto10 + " 10to100 " + tento100 + " 100to1 " + hundredto1
					+ " >1 " + morethanone);

			

				String splitter[] = content.toString().split(";");

				String checker = splitter[0];

				System.out.println("checker " + checker);

				if (checker.equals("text/html"))

					htmlval++;

				else if (checker.equals("image/gif"))

					gif++;

				else if (checker.equals("image/jpeg"))

					jpeg++;

				else if (checker.equals("image/png"))

					png++;

				else if (checker.equals("application/pdf"))

					pdf++;

				System.out.println("html " + htmlval + " jpeg " + jpeg + " gif " + gif + " png " + png + " pdf " + pdf);

			
		
		}

	}

	protected void handlePageStatusCode(WebURL webUrl, int statusCode, String statusDescription) {
		String web=webUrl.toString();
		if(webUrl.toString().contains(","))
			web=webUrl.toString().replace(",", "-");
		
		String entries[] = { web, statusCode + "" };
		cs.add(entries);
		//writer.writeNext(entries);
		
		filefetcher++;

		

		

		if (statusCode >= 200 && statusCode < 300)

		{

			filefetchersucceded++;

			// System.out.println("succeed "+filefetchersucceded);

			

		}
		if(statusCode>=300 && statusCode<400)
			failaborted++;
		if(statusCode==404)
			filefailed++;

		if (statusCode == 200)

			OK++;

		else if (statusCode == 301)

			Moved++;

		else if (statusCode == 401)

			Unauthorized++;

		else if (statusCode == 403)

			Forbidden++;

		else if (statusCode == 404)

			NotFound++;

		System.out.println("hmap size " + hmap.size());

		System.out.println("inwebsite " + inwebsite);

		System.out.println("outwebsite " + outwebsite);

		System.out.println("OK " + OK);

		System.out.println("MOVED " + Moved);

		System.out.println("UN " + Unauthorized);

		System.out.println("FORB " + Forbidden);

		System.out.println("NotFound " + NotFound);
		
		System.out.println("GRAND TOTAL "+grandtotalextracted);
		
		System.out.println("fetches_general "+filefetcher+" fetches_succeded "+filefetchersucceded+" aborted "+failaborted+" failed "+filefailed+" ");

		

	}

}