package jvcAPI;

import java.io.BufferedReader;
import java.io.FilterInputStream;
import java.io.InputStream;

import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Scanner;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

public class Main {

  static HashMap<Integer, String> resultMap;

  public static void main(String[] args) {
    resultMap = new HashMap<Integer, String>();
    int result;
    if (args.length > 0) {
      search(args[0]);
      result = resultMap.entrySet().size();
      if (result == 0)
        System.out.println("No result.");
      else if (result == 1) {
        for (Integer id : resultMap.keySet()) {
          getResume(id);
          getDetail(id);
        }
      } else {
        System.out.println("Result: " + result);
        for (Integer i : resultMap.keySet())
          System.out.println(i + " : " + resultMap.get(i));
        Scanner scan = new Scanner(System.in);
        System.out.println("Enter the ID: ");
        int id = scan.nextInt();
        getResume(new Integer(id));
        getDetail(new Integer(id));
        scan.close();
      }
    }
  }

  /**
   *
   *
   * @param pattern
   */
  private static void search(String pattern) {
    InputStream is   = searchStream(pattern);
    NodeList list  = getNodes(is);
    if (list.size() > 2) {
      Node wii       = getWiiNode(list);
      NodeList links = getWiiLinks(wii);
      for(int i=0; i<links.size(); i++)
        extractIDData(links.elementAt(i));
    }
  }

  /**
   * Get the stream of a search request.
   *
   * @param pattern : pattern to search.
   * @return InputStream result of the search request.
   */
  private static InputStream searchStream(String pattern) {
    String gameName = pattern.replace(" ", "%20");
    String searchURL = "http://ws.jeuxvideo.com/search/" + gameName;
    return connect(searchURL);
  }

  private static void getResume(Integer gameID) {
    InputStream is = getResumeStream(gameID);
    NodeList list = getNodes(is);
    String resume = list.elementAt(0).getText().split("http://")[0];
    System.out.println("Synopsis:\n" + resume);
  }

  private static void getDetail(Integer gameID) {
    InputStream is = getDetailsStream(gameID);
    try {
      BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
      String rawData = "";
      while (reader.ready())
        rawData += reader.readLine() + "\n";
      String title       = rawData.split("<titre>")[1].split("</titre>")[0].split("]]>")[0].split("<!\\[CDATA\\[")[1];
      String releaseDate = rawData.split("<date_sortie>")[1].split("</date_sortie>")[0].split("]]>")[0].split("<!\\[CDATA\\[")[1];
      String editor      = rawData.split("<editeur>")[1].split("</editeur>")[0].split("]]>")[0].split("<!\\[CDATA\\[")[1];
      String developer   = rawData.split("<developpeur>")[1].split("</developpeur>")[0].split("]]>")[0].split("<!\\[CDATA\\[")[1];
      String type        = rawData.split("<type>")[1].split("</type>")[0].split("]]>")[0].split("<!\\[CDATA\\[")[1];
      System.out.println("Title: "        + title);
      System.out.println("Release date: " + releaseDate);
      System.out.println("Editor: "       + editor);
      System.out.println("Developer: "    + developer);
      System.out.println("Type: "         + type);
    } catch (Exception e) {
      e.printStackTrace();
    }
    //NodeList list = getNodes(is);
    //Node detailNode = list.elementAt(0);
    //System.out.println(detailNode.toHtml(true));
    //System.out.println(detailNode.toPlainTextString());
  }

  private static InputStream getDetailsStream(Integer gameID) {
    String detailURL = "http://ws.jeuxvideo.com/01.jeux/" + gameID + ".xml";
    return connect(detailURL);
  }

  /**
   * Construct a request to get game details.
   *
   * @param gameID : Unique ID of a game, see {@link #search} to get the id from
   * the name of the game.
   * @return InputStream result of the details request.
   */
  private static InputStream getResumeStream(Integer gameID) {
    String detailURL = "http://ws.jeuxvideo.com/01.jeux/details/" + gameID + ".xml";
    return connect(detailURL);
  }

  /**
   * Connect and authentify to the URL and return the response.
   *
   * @param url : url to connect to, {@link #search} or {@link #getDetail details}
   * @return InputStream result of the request;
   */
  private static InputStream connect(String url) {
    String login = "appandr:e32!cdf";
    FilterInputStream fis = null;

    try {
      String encodedLogin = Base64Converter.encode(login
          .getBytes("UTF-8"));
      URL location = new URL(url);
      URLConnection connection = location.openConnection();
      connection.setRequestProperty("Authorization",
          String.format("Basic %s", encodedLogin));
      fis = (FilterInputStream) connection.getContent();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return (InputStream) fis;
  }

  /**
   * Get the XML data and extract the HTML part and return a list of HTML nodes.
   *
   * @param pattern
   * @return
   */
  private static NodeList getNodes(InputStream is) {
    SAXReader reader = new SAXReader();
    Document doc;
    NodeList list = null;

    try {
      doc = reader.read(is);
      Element root = doc.getRootElement();
      String html = root.getStringValue();
      Parser parser = new Parser();
      parser.setInputHTML(html);
      list = parser.parse(null);
    } catch (DocumentException e) {
      e.printStackTrace();
    } catch (ParserException e) {
      e.printStackTrace();
    }

    return list;
  }

  /**
   *
   *
   * @param list
   * @return
   */
  private static Node getWiiNode(NodeList list) {
    Node ret = null;
    if (list.size() > 2) {
      Node resultNode = list.elementAt(2);
      for (int i = 0; i < resultNode.getChildren().size(); i++) {
        Node node = resultNode.getChildren().elementAt(i);
        if (node.getText().contains("class=\"m_wii\""))
          ret = resultNode.getChildren().elementAt(i);
      }
    } else
      System.out.println("No result found.");
    return ret;
  }

  /**
   *
   *
   * @param wiiNode
   * @return
   */
  private static NodeList getWiiLinks(Node wiiNode) {
    NodeList list = new NodeList();
    Node wiiLinks  = wiiNode.getNextSibling().getNextSibling();
    for (int i=1; i<wiiLinks.getChildren().size(); i=i+2) {
      Node node = wiiLinks.getChildren().elementAt(i);
      list.add(node);
    }
    return list;
  }

  /**
   * Extract the id and the name of a game from the li node.
   *
   * @param linksNode : li node
   */
  private static void extractIDData(Node linksNode) {
    String links = linksNode.getChildren().elementAt(0).getText();
    String html  = linksNode.toHtml();
    String[] b   = html.split("xml\">");
    String name  = b[1].split("</a>")[0];

    String id = links.split("jeux/")[1];
    id = id.split(".xml")[0];

    resultMap.put(new Integer(id), name);
  }
}
