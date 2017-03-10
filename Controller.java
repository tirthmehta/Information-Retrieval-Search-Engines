import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.*;

import com.opencsv.CSVWriter;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;

import edu.uci.ics.crawler4j.crawler.CrawlController;

import edu.uci.ics.crawler4j.fetcher.PageFetcher;

import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;

import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class Controller {

	public static void main(String[] args) throws Exception {

		// TODO Auto-generated method stub

		String crawlStorageFolder = "data/crawl";

		int numberOfCrawlers = 16;

		CrawlConfig config = new CrawlConfig();

		config.setCrawlStorageFolder(crawlStorageFolder);
		config.setIncludeBinaryContentInCrawling(true);

		// SETTING THE INNER CONFIGURATIONS

		config.setMaxDepthOfCrawling(16);

		config.setMaxPagesToFetch(20000);

		

		PageFetcher pageFetcher = new PageFetcher(config);

		RobotstxtConfig robotstxtConfig = new RobotstxtConfig();

		RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);

		CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

		String givenurl = "http://www.usc.edu/";

		controller.addSeed(givenurl);

		controller.start(MyCrawler.class, numberOfCrawlers);
		
		PrintWriter pw1 = new PrintWriter(new File("fetch_latimes.csv"));
		PrintWriter pw2 = new PrintWriter(new File("visit_latimes.csv"));
        

       
        System.out.println("done!");
		CrawlStat c=new CrawlStat();
		ArrayList<String[]> a=c.get();
		System.out.println("size "+a.size());
		ArrayList<String[]> a2=c.get2();
		System.out.println("size "+a2.size());
		
		for(String x[]:a)
		{
			StringBuilder sb = new StringBuilder();
			sb.append(x[0]);
	        sb.append(',');
	        sb.append(x[1]);
	        sb.append('\n');	
			
			pw1.write(sb.toString());
		}
		pw1.close();
		for(String x[]:a2)
		{
			StringBuilder sb = new StringBuilder();
			sb.append(x[0]);
	        sb.append(',');
	        sb.append(x[1]);
	        
	        sb.append(',');
	        sb.append(x[2]);
	        sb.append(',');
	       
	        sb.append(x[3]);
	        
	        sb.append('\n');	
			
			pw2.write(sb.toString());
		}
		pw2.close();

	}

}