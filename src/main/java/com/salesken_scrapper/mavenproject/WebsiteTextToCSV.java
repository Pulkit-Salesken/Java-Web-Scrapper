package com.salesken_scrapper.mavenproject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebsiteTextToCSV {

	static File csvFile = new File("website_text.csv");
	
	static FileWriter writer;
	public static void initializeWriter() {
	
		try {
			writer = new FileWriter(csvFile);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	

	static Set<String> map = new HashSet<>();

	public static ArrayList<String> getData(String pageUrl) throws IOException {

		if (map.contains(pageUrl.toLowerCase().trim()))
			return null;

		try {
			
			System.out.println("THIS IS URL: " + pageUrl);

			map.add(pageUrl.toLowerCase().trim());

			if (pageUrl.isEmpty())
				return null;

			Document doc = Jsoup.connect(pageUrl).get();
			
			Elements divs = new Elements();
			
			ArrayList<Elements> allElements = new ArrayList<>();

			Elements allDivs = doc.getElementsByTag("div");
			for(Element div : allDivs) {
				if(div.childrenSize() == 0) {
					divs.add(div);
				}
			}
			

			allElements.add(divs);
			allElements.add(doc.getElementsByTag("h1"));
			allElements.add(doc.getElementsByTag("h2"));
			allElements.add(doc.getElementsByTag("h3"));
			allElements.add(doc.getElementsByTag("h4"));
			allElements.add(doc.getElementsByTag("h5"));
			allElements.add(doc.getElementsByTag("h6"));
			allElements.add(doc.getElementsByTag("p"));
			allElements.add(doc.getElementsByTag("span"));
		
//
			Elements links = doc.select("a");

			for (Elements elements : allElements) {
				for (Element el : elements) {
					writer.append(el.text());
					writer.append("\n");
//		    		System.out.println(el.text());
				}
			}

			ArrayList<String> urlList = new ArrayList<>();
			System.out.println("Done writing homepage to csv.....");
			for (Element link : links) {
				if (link.attr("abs:href").contains("www.salesken.ai")
						|| link.attr("abs:href").contains("//app.salesken.ai"))
					urlList.add(link.attr("abs:href"));

			}

			return urlList;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void getUrls(ArrayList<String> urls) throws IOException {
		if (urls.isEmpty())
			return;

		for (String url : urls) {
			if (url != null) {
				ArrayList<String> urlList = getData(url);
				if (urlList != null) {
					getUrls(urlList);
				}
			}
		}
	}

	public static void main(String[] args) throws IOException {

		initializeWriter();
		
		
		String homeUrl = "https://www.salesken.ai/";

		ArrayList<String> data = getData(homeUrl);

		System.out.println("First step complete!!!!!");

		Set<String> set = new HashSet<>(data);

		ArrayList<String> urls = new ArrayList<>(set);

		getUrls(urls);
		
		writer.close();
	}
}
