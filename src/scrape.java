import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class scrape 
{
	public static void main(String[] args) 
	{
		// TODO Auto-generated method stub
		try 
		{
			System.out.println("Enter player ID: ");
			Scanner scan = new Scanner(System.in);
			String id = scan.next();			
			int numOfMatches = 10;

			String url = "https://www.dotabuff.com/players/"+id+"/matches";
			Document site = Jsoup.connect(url).get();
			
			
			displayMatches(site, numOfMatches);			
			ArrayList<Long> matchIDs = collectMatches(site, numOfMatches);
			
			System.out.println("Enter the number of the match you want to save");
			int select = scan.nextInt();
			select = select - 1;
			scan.close();
			
			saveMatch(matchIDs, select);
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	
	public static void displayMatches(Document site, int numOfMatches)
	{
		Elements playerElement = site.getElementsByClass("header-content-title").select("h1");
		Elements heroElement = site.getElementsByClass("cell-large").select("a");
		
		String player = playerElement.first().text();
		int find = player.indexOf("M");
		player = player.substring(0, find);
		System.out.println("Displaying most recent " + numOfMatches + " matches for " + player);
		
		//loop to display most recent matches
		for(int i = 0; i < numOfMatches; i++)
		{
			String hero = heroElement.get(i).text();
			System.out.println(i+1 + ") " + hero);
		}		
	}
	
	public static ArrayList<Long> collectMatches(Document site, int numOfMatches)
	{
		//collect winning match IDs		
		Elements win = site.getElementsByClass("won").select("a[href]");
		Elements loss = site.getElementsByClass("lost").select("a[href]");
		ArrayList<Long> matchList = new ArrayList<Long>();
		
		//winning IDs loop
		for(int i = 0; i < numOfMatches; i++)
		{
			Element wLoop = win.get(i);
			String wHref = wLoop.toString();
			int findFirstW = wHref.indexOf("/", 22);
			int findLastW = wHref.indexOf('"', 21);
			long matchID = Long.parseLong(wHref.substring(findFirstW+1, findLastW));
			matchList.add(matchID);
		}
		
		//losing IDs loop
		for(int i = 0; i < numOfMatches; i++)
		{
			Element lLoop = loss.get(i);
			String lHref = lLoop.toString();
			int findFirstL = lHref.indexOf("/", 23);
			int findLastL = lHref.indexOf('"', 22);
			long matchID = Long.parseLong(lHref.substring(findFirstL+1, findLastL));
			matchList.add(matchID);
		}
		
		Collections.sort(matchList);
		Collections.reverse(matchList);
		ArrayList<Long> matchIDs = new ArrayList<Long>(numOfMatches);
		
		//get correct matches
		for(int i = 0; i < numOfMatches; i++)
		{
			long id = matchList.get(i);
			matchIDs.add(id);
		}
		return matchIDs;			
	}
	
	public static void saveMatch(ArrayList<Long> matchIDs, int select) throws IOException
	{		
		long match = matchIDs.get(select);
		String url = "https://www.dotabuff.com/matches/"+match;
		Document matchInfo = Jsoup.connect(url).get();
		Elements collectHeroes = matchInfo.getElementsByClass("image-hero image-icon image-overlay");
		String [][] radiant = new String [5][10];
		
		//loop to add heroes to array
		for(int r = 0; r < 5; r++)
		{
			Element heroElement = collectHeroes.get(r);
			String hero = heroElement.toString();
			int find1 = hero.indexOf('"', 76) + 1;
			int find2 = hero.indexOf('"', 77);
			hero = hero.substring(find1, find2);
			radiant [r][0] = hero;
			System.out.println(radiant[r][0]);				
		}
		
		
		
	}
	
}
