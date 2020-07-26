import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.simple.parser.JSONParser;
import org.apache.commons.io.FileUtils;

public class JSONPointSystem {

  private static String readAll(Reader rd) throws IOException {
    StringBuilder sb = new StringBuilder();
    int cp;
    while ((cp = rd.read()) != -1) {
      sb.append((char) cp);
    }
    return sb.toString();
  }

  public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
    InputStream is = new URL(url).openStream();
    try {
      BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
      String jsonText = readAll(rd);
      JSONObject json = new JSONObject(jsonText);
      return json;
    } finally {
      is.close();
    }
  }
  
  public static LinkedHashMap<Integer, Integer> sortByValue(LinkedHashMap<Integer, Integer> hm) 
  { 
      // Create a list from elements of HashMap 
      List<Map.Entry<Integer, Integer> > list = 
             new LinkedList<Map.Entry<Integer, Integer> >(hm.entrySet()); 

      // Sort the list 
      Collections.sort(list, new Comparator<Map.Entry<Integer, Integer> >() { 
          public int compare(Map.Entry<Integer, Integer> o1,  
                             Map.Entry<Integer, Integer> o2) 
          { 
              return (o1.getValue()).compareTo(o2.getValue()); 
          } 
      }); 
        
      // put data from sorted list to hashmap  
      LinkedHashMap<Integer, Integer> temp = new LinkedHashMap<Integer, Integer>(); 
      for (Map.Entry<Integer, Integer> aa : list) { 
          temp.put(aa.getKey(), aa.getValue()); 
      } 
      return temp; 
  } 

  public static Object getByIndex(LinkedHashMap<Integer, Integer> hMap, int index){
	   return hMap.keySet().toArray()[index];
	}
  
  public static void main(String[] args) throws IOException, JSONException {
	ArrayList<String[]> al = new ArrayList<String[]>();
	
	File myObj = new File("D:/twitter/outputFile.txt");
	Scanner myReader = new Scanner(myObj);
	
	LinkedHashMap<Integer, Integer> map = new LinkedHashMap<Integer, Integer>();
	
	while (myReader.hasNextLine())
	{
		String data = myReader.nextLine();
		String cleanData = myReader.nextLine().replaceAll("\\(.*\\)", "").replaceAll("\\s", "+");
		System.out.println(cleanData);
		String answer = data.substring(data.indexOf("(")+1,data.indexOf(")"));
		int pointValue = 0;
		if (!(cleanData.equals("")) && !(cleanData.chars().allMatch(Character::isDigit)))
		{
			if (answer.equals("news") || answer.equals("tweet"))
			{
				pointValue = 1;
			}
			
			else if (answer.equals("name") || answer.equals("city") || answer.equals("state"))
			{
				pointValue = 3;
			}
			
			else
			{
				pointValue = 2;
			}
			
			String url = "";
			
			if (answer.equals("name"))
			{
				url = "https://api.themoviedb.org/3/search/person?api_key=dae6cd32450211d689ce9fc4fec840a2&query=" + cleanData;
				JSONObject json = readJsonFromUrl(url);
				JSONArray ja = json.getJSONArray("results");
				if (ja.length() > 0)
				{
					JSONObject json2 = ja.getJSONObject(0);
					JSONArray ja2 = json2.getJSONArray("known_for");
					for (int i = 0; i < ja2.length(); i++)
					{
						JSONObject jo = ja2.getJSONObject(i);
						if(!(jo.isNull("id")))
						{
							Integer in = (Integer) jo.get("id");
							if (map.containsKey(in))
							{
								map.put(in, map.get(in) + 1);
							}
							else
							{
								map.put(in, 1);
							}
						}
					}
				}
			}
			else
			{
				url = "https://api.themoviedb.org/3/search/movie?api_key=dae6cd32450211d689ce9fc4fec840a2&query=" + cleanData;
				JSONObject json = readJsonFromUrl(url);
				JSONArray ja = json.getJSONArray("results");
				for (int i = 0; i < ja.length(); i++)
				{
					JSONObject jo = ja.getJSONObject(i);
					if(!(jo.isNull("id")))
					{
						Integer in = (Integer) jo.get("id");
						if (map.containsKey(in))
						{
							map.put(in, map.get(in) + 1);
						}
						else
						{
							map.put(in, 1);
						}
					}
				}
			}
		}
	}
	
	map = sortByValue(map);
	
	for (int i = 0; i < 20; i++)
	{
		String id = Integer.toString((Integer)getByIndex(map, map.size()-i-1));
		String url = "https://api.themoviedb.org/3/movie/" + id + "?api_key=dae6cd32450211d689ce9fc4fec840a2";
		JSONObject json = readJsonFromUrl(url);
		String[] vals = new String[3];
		String s = "http://image.tmdb.org/t/p/w185";
    	vals[0] = json.getString("original_title");
    	vals[1] = json.getString("overview");
    	if (!(json.isNull("poster_path")))
    	{
    		vals[2] = s + json.getString("poster_path");
    	}
    	else
    	{
    		vals[2] = "";
    	}
    	al.add(vals);
	}
	
	for (int i = 0; i < al.size(); i++)
	{
		String[] s = al.get(i);
		for (int j = 0; j < s.length; j++)
		{
			System.out.print(s[j] + "\t");
		}
		System.out.println("\n");
	}
	
	
	
	
	/*String url = "https://api.themoviedb.org/3/search/movie?api_key=dae6cd32450211d689ce9fc4fec840a2&query=";
    JSONObject json = readJsonFromUrl(url);
    JSONArray ja = json.getJSONArray("results");
    for (int i = 0; i < ja.length(); i++)
    {
    	JSONObject jo = ja.getJSONObject(i);
    	String[] vals = new String[3];
    	String s = "http://image.tmdb.org/t/p/w185";
    	vals[0] = jo.getString("title");
    	vals[1] = jo.getString("overview");
    	if (!(jo.isNull("poster_path")))
    	{
    		vals[2] = s + jo.getString("poster_path");
    	}
    	else
    	{
    		vals[2] = "";
    	}
    	al.add(vals);
    }
    
    
    File htmlTemplateFile = new File("D:/SpringBoot/gs-spring-boot-master/complete/src/main/webapp/WEB-INF/jsp/hello.jsp");
    String htmlString = FileUtils.readFileToString(htmlTemplateFile, Charset.defaultCharset());
    String longString = "";
    int rows = al.size()/5;
    int remain = al.size()%5;
    int counter = 0;
    for (int i = 0; i < rows; i++)
    {
    	
    	for (int j = 0; j < 5; j++)
    	{
    		longString=longString+"<div class = 'col'><div class = 'text-center'>";
    		String[] s = al.get(counter);
    		for (int k = 0; k < s.length; k++)
    		{
    			if (k == 0)
    			{
    				longString+="<h2>" + s[k] + "</h2></div>";
    			}
    			else if (k == s.length-1)
    			{
    				longString+="<div style = 'margin:auto; width:10%;'><img src = '" + s[k] + "'></div>";
    			}

    			else
    			{
    				longString+="<div class = 'text-center' style = 'padding:20px;'><h4>" + s[k] + "</h4></div>";
    			}
    		}
    		longString+="</div></div>";
    		counter++;
    	}
    }
    
    
    longString = longString + "<div class = 'row'>";
    for (int i = 0; i < remain; i++)
    {
		longString=longString+"<div class = 'col'><div class = 'text-center'>";
		String[] s = al.get(counter);
		for (int k = 0; k < s.length; k++)
		{
			if (k == 0)
			{
				longString+="<h2>" + s[k] + "</h2></div>";
			}
			else if (k == s.length-1)
			{
				longString+="<div style = 'display:block; margin:auto;'><img src = '" + s[k] + "'></div>";
			}

			else
			{
				longString+="<div class = 'text-center'><h5>" + s[k] + "</h5></div>";
			}
		}
		longString+="</div></div>";
		counter++;
    }
    
	longString+="</div>";
	
	
    htmlString = htmlString.replace("$content", longString);
    File newHtmlFile = new File("D:/SpringBoot/gs-spring-boot-master/complete/src/main/webapp/WEB-INF/jsp/hello.jsp");
    FileUtils.writeStringToFile(newHtmlFile, htmlString, Charset.defaultCharset());
    System.out.println("Done...");*/
	
  }
}