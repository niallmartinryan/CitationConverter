///*
import com.mongodb.BasicDBObject;
import com.mongodb.BulkWriteOperation;
import com.mongodb.BulkWriteResult;
import com.mongodb.Cursor;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.ParallelScanOptions;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.MimeMessage;

import java.util.LinkedHashSet;
import java.lang.ClassCastException;
//*/
import java.io.FileInputStream;
// import de.undercouch.citeproc.bibtex.BibTexConverter;
import org.jbibtex.*;
import de.undercouch.citeproc.bibtex.BibTeXConverter;
import de.undercouch.citeproc.CSL;
import org.jbibtex.BibTeXDatabase;
import org.jbibtex.*;
import org.jbibtex.Key;
import org.jbibtex.Value;
import org.jbibtex.BibTeXString;
import org.jbibtex.StringValue;

import java.math.BigInteger;

import de.undercouch.citeproc.output.Citation;
import java.util.List;
import java.io.IOException;
import de.undercouch.citeproc.bibtex.BibTeXItemDataProvider;
import de.undercouch.citeproc.csl.*;
import de.undercouch.citeproc.helper.json.JsonBuilder;
import de.undercouch.citeproc.SelectionMode;
import de.undercouch.citeproc.output.Bibliography;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.io.File;

import java.util.LinkedHashMap;
import java.util.Set;

import de.undercouch.citeproc.helper.json.*;
import de.undercouch.citeproc.helper.json.JsonObject;
import de.undercouch.citeproc.helper.json.StringJsonBuilder;
import de.undercouch.citeproc.helper.json.JsonBuilderFactory;
import de.undercouch.citeproc.ItemDataProvider;
import de.undercouch.citeproc.bibtex.BibTeXItemDataProvider;
import org.json.*;
import java.lang.StringBuilder;

import java.io.FileNotFoundException;


import java.lang.IllegalArgumentException;
import org.bson.codecs.configuration.CodecConfigurationException;

import javax.mail.internet.InternetAddress;


public class App{

  public static void main(String[] args){
    // Email Setup
    
    String bibFileName = "";
    if(args.length == 1){
      bibFileName = args[0];
    }else{
      System.out.println("need to add an argument bro");
      System.exit(1);
    }
    String recip = "ryann11@tcd.ie";
    String from = "tiphRyan@gmail.com";
    String host = "localhost";
    Properties props = System.getProperties();
    props.setProperty("mail.smtp.host", host);
    Session session = Session.getDefaultInstance(props);        


    // Mongo Setup    

    MongoClient mongoClient = new MongoClient();
    MongoDatabase database = mongoClient.getDatabase("9DBReal");

    MongoCollection<Document> coll = database.getCollection("articles");

    // grabbing the citation style names

    ArrayList<String> styles = getStyleNames();





    try{		
      BibTeXDatabase dbBib = new BibTeXConverter().loadDatabase(
          new FileInputStream(bibFileName + ".bib"));

      BibTeXItemDataProvider provider = new BibTeXItemDataProvider();
      provider.addDatabase(dbBib);

      BibTeXConverter converter = new BibTeXConverter();
      Map<String,CSLItemData> CSLmap = converter.toItemData(dbBib);
      
      //for(){
      
      //}


      System.out.println("Data is loaded intobibtex converter db");
      System.out.println("starting conversion");
      int count =0;
      for(Map.Entry entry : CSLmap.entrySet()){				
        System.out.println("count :" + count);
        CSLItemData datum = (CSLItemData) entry.getValue();
        BasicDBObject article = convertToDBObj(datum);
        //System.out.println("Inserting Document");
        coll.insertOne(new Document(article));
        System.out.println("Inserted");
        
          //try{
        CSL myProcessor = new CSL(new BibTeXItemDataProvider(), "ieee");
          //Bibliography myNewBib =  myProcessor.makeAdhocBibliography(SelectionMode.INCLUDE, datum );
        myProcessor.setOutputFormat("text");
        //Bibliography myNewBib = myProcessor.makeBibliography(SelectionMode.INCLUDE,datum); 
        //System.out.println("myNewBib" + myNewBib.makeString());
        //System.exit(1);  
        //Bibliography myNewBib = myProcessor.makeAdhocBibliography("ieee","text", datum);
          //System.out.println("myNewBib : " + myNewBib.makeString());  
        //} catch(IOException e){
        //  System.out.print(e);
        //}


        int test = 0;
        int max = 1000;
        long startTimeWhole = System.currentTimeMillis();
        //System.out.println("styles.length :: " + styles.length());
        for(String style : styles){
          //if(test>= max){
          //  break;
          //} 
          try{
            //System.out.println("Style : " + style);
           // CSLItemData datum = (CSLItemData) entry.getValue();
            
            //long startTimeBib = System.currentTimeMillis();
            String bib = myProcessor.makeAdhocBibliography(style, "text", datum).makeString();
            //long endTimeBib = System.currentTimeMillis();
            String pathToStyle = "modCSL/styles/" + style + ".csl"; 
            String altStyle = new String(Files.readAllBytes(Paths.get(pathToStyle)));
            String annotatedBib = myProcessor.makeAdhocBibliography(altStyle, "text", datum).makeString();
            BasicDBObject citation = new BasicDBObject();
            //System.out.println("AnnotatedBib == <" +  annotatedBib + ">");
            citation.append("style", style);
            citation.append("bib" , bib);
            citation.append("annotatedBib", annotatedBib);
            BasicDBObject citeToAdd = new BasicDBObject("citation", citation);
            BasicDBObject updateQuery = new BasicDBObject("$push", citeToAdd);
            //long startTimeUpdate = System.currentTimeMillis();
            coll.updateOne(article,updateQuery); 
            //long endTimeUpdate = System.currentTimeMillis();
            //long totalTimeUpdate = (endTimeUpdate - startTimeUpdate);
            //long totalTimeBib = (endTimeBib - startTimeBib);
            //System.out.println("totalTimeUpdate : " + totalTimeUpdate );
            //System.out.println("totalTimeBib : " + totalTimeBib);
            //System.out.println("COUNTER === " + test);
            //test++;
          } catch(java.lang.IllegalArgumentException | ClassCastException | CodecConfigurationException e){
            System.out.println("skipped one: ");
            System.out.println(e);
            test++;  
            continue;
          }
          //System.exit(1);
        }
        long endTime = System.currentTimeMillis();
        //long totalTime = startTimeWhole ;
        System.out.println("it took " +(endTime - startTimeWhole));
        //System.exit(1);
        //
        System.out.println("skipped count" + test);
        count++;
      }

    }catch( ParseException |IOException  e){
      e.printStackTrace();
    }
    // close database
    System.out.println("Finished");
    mongoClient.close();
  }

  public static String toHex(String arg) {
    return String.format("%040x", new BigInteger(1, arg.getBytes(/*YOUR_CHARSET?*/)));
  }
  public static void sendEmail(String to, String from, String subject,String text, Session session){
    try{ 
      MimeMessage message = new MimeMessage(session);
      message.setFrom(new InternetAddress(from));
      message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
      message.setSubject(subject);
      message.setText(text);
      Transport.send(message);
      System.out.println("message sent succ");
    } catch(MessagingException e){
      e.printStackTrace();
    }

  }	
  public static BasicDBObject convertToDBObj(CSLItemData data){
    BasicDBObject document = new BasicDBObject();
    StringBuilder sb = new StringBuilder();
    StringBuilder editorSb = new StringBuilder();
    CSLName [] authors = data.getAuthor();
    CSLName [] editors = data.getEditor();
    //if(authors.length==1){

    //}
    // TODO THIS SHOULD BE A FUNCTION!!!
    if(authors == null){
      document.append("author", null);
    }else{
      for (CSLName n : authors) { 
        if (sb.length() > 0) sb.append(" and ");
        sb.append(n.getFamily()+ ", " + n.getGiven() );
      }
      document.append("author", sb.toString());
    }
    if(editors == null){
      document.append("editor", null);
    } else{
      for (CSLName n : editors){
        if( editorSb.length() > 0) editorSb.append(" and ");
        editorSb.append(n.getFamily()+ ", " + n.getGiven());
      }
      document.append("editor", editorSb.toString());
    }
    //System.out.println("Author --- sb -- = " + sb.toString());
    document.append("type", data.getType().toString());
    document.append("title", data.getTitle());
    document.append("id" , data.getId());
    document.append("Accessed" , data.getAccessed().getLiteral());
    document.append("Annote" , data.getAnnote());
    document.append("ChapterNumber", data.getChapterNumber());
    document.append("CitationLabel", data.getCitationLabel());
    document.append("CitationNumber", data.getCitationNumber());
    document.append("CollectionNumber", data.getCollectionNumber());
    document.append("CollectionTitle", data.getCollectionTitle());
    document.append("Containter" , data.getContainer().getLiteral());
    document.append("EventDate" , data.getEventDate().getLiteral());
    document.append("getIssued" , data.getIssued().getLiteral());
    documnet.append("OriginalDate", data.getOriginalDate().getLiteral());
    document.append("Submitted" , data.getSubmitted().getLiteral());    
    System.out.println("title : " +data.getTitle());
    document.append("number", data.getNumber());
    document.append("numpages", data.getNumberOfPages());
    document.append("pages", data.getPage());
    document.append("publisher", data.getPublisher());
    document.append("volume", data.getVolume());
    document.append("year", data.getYearSuffix());
    document.append("journal",data.getJournalAbbreviation());
    document.append("chapter", data.getChapterNumber());		
    document.append("archiveLocation", data.getArchivePlace());
    document.append("annote", data.getAnnote());
    document.append("locator", data.getLocator());
    document.append("version", data.getVersion());
    document.append("references", data.getReferences());
    document.append("pmid" , data.getPMID());
    document.append("pmcid" , data.getPMCID());
    document.append("issue", data.getIssue());
    document.append("doi", data.getDOI());
    return document;
  }


  // Gets style names for bibtex to citation conversion
  public static ArrayList<String> getStyleNames(){
    File folder = new File(Resources.STYLES_LIBRARY_PATH_1);
    File folderInner = new File(Resources.STYLES_LIBRARY_PATH_2);
    File[] fileList2 = folderInner.listFiles();
    File[] fileList1 = folder.listFiles();
    ArrayList<String> styles = new ArrayList<String>();
    // Run through outer file Styles
    for (int i = 0; i < fileList1.length; i++) {
      String fileName = fileList1[i].getName();
      if (fileList1[i].isFile() &&
          fileName.substring(fileName.length() - 4).equals(Resources.FILE_ENDING_CSL)) {
        // System.out.println("File " + fileList1[i].getName());
        styles.add(fileName.substring(0, fileName.length() - 4));
      } else if (fileList1[i].isDirectory()) {
        // System.out.println("Directory " + fileList1[i].getName());
      }
    }
    // Run through inner file Styles
    
    //for (int i=0;i<fileList2.length;i++ ) {
    //  String fileName = fileList2[i].getName();
    //  if(fileList2[i].isFile()){
    //    styles.add(fileName);
    //  }
    //}
    return removeDubs(styles);
  }
  public static ArrayList<String> removeDubs(ArrayList<String> arr ){
    Set<String> s = new LinkedHashSet<>(arr);
    List<String> list = new ArrayList<String>(s);
    return arr;
  } 
  public static void printMapCSL(Map<String, CSLItemData> map){
    for(Map.Entry entry : map.entrySet()){
      System.out.println(entry.getKey() + ", " + entry.getValue());
    }
  }
}
