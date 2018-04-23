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
import org.bson.types.ObjectId;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.MimeMessage;
import de.undercouch.citeproc.helper.json.StringJsonBuilder;
import de.undercouch.citeproc.helper.json.JsonBuilderFactory;

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
import com.mongodb.client.FindIterable; 
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


public class newApp{
  public static int skip =0;
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
    MongoDatabase database = mongoClient.getDatabase("CSLDB");

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

      //
      //
      //}  

      /*long timeCreateCSL = System.currentTimeMillis();
        ArrayList<CSL> myProcessors = new ArrayList<CSL>();
        ArrayList<CSL> myAltProcessors = new ArrayList<CSL>();
        int mycount =0;
        for(String style :styles){
        if(mycount >=1000){
        break;
        }
        CSL myProcess = new CSL(provider, style);
        myProcess.setOutputFormat("text");
        myProcessors.add(myProcess);
        System.out.println("count :: " + mycount++);
        }
        for(String altStyle : styles){
        if(mycount >= 2000){
        break;
        }
        String pathToStyle = "modCSL/styles/" + altStyle + ".csl";
        String altStyleObj = new String(Files.readAllBytes(Paths.get(pathToStyle)));
        CSL myProcess = new CSL( provider, altStyleObj);
        myProcess.setOutputFormat("text");
        myAltProcessors.add(myProcess);
        System.out.println("count2 ::" + mycount++);
        }
        long timeEndCSL = System.currentTimeMillis();
        System.out.println("TIME TO CREATE CSLS ARRAYS ::: = " + (timeEndCSL - timeCreateCSL));
        */
      //CSL myProcessor = new CSL(new BibTeXItemDataProvider(), "ieee");

      //} catch(IOException e){
      //  System.out.println(e);
      //}

      System.out.println("Data is loaded intobibtex converter db");
      System.out.println("starting conversion");
      //  System.exit(1);

      //   }
      int count =0;
      // TWO OPTIONS.. USE THE IDS.. or keep the objects as you make them.
      // which was what you were doing..
      //ArrayList<ObjectId>  ids =  new ArrayList<ObjectId>();
      //ArrayList<BasicDBObject> objects = new ArrayList<BasicDBObject>();
      boolean check = true;
      int styleCount =0;
      for(String style : styles){
        CSL myProcess = new CSL(provider, style);
        myProcess.setOutputFormat("text"); 
        String pathToStyle = "modCSL/styles/" + style + ".csl";
        String altStyleObj = new String(Files.readAllBytes(Paths.get(pathToStyle))); 
        CSL myAltProcess = new CSL(provider, altStyleObj);
        myAltProcess.setOutputFormat("text");

        int entryCount =0;
        skip =0;
        for(Map.Entry entry : CSLmap.entrySet()){
          // after the first style I wont have to check if the entry exists    
           
          CSLItemData datum = (CSLItemData) entry.getValue();
          if(check == true){
            System.out.println("inside of the check");
            // insert the new doc and get the id of that doc and put it into
            // the array "ids"
            
            // query the step of the db
            BasicDBObject searchQuery = new BasicDBObject().append("skip", skip);
            FindIterable<Document> docs = coll.find(searchQuery);
            System.out.println("docs :" + docs);
            boolean found = false;
            for(Document doc : docs){
               found =true;
            //    ObjectId id = new ObjectId(doc.get("_id").toString());
            //    ids.add(id);
            }
            if(found == false){
              System.out.println("The docs are null bois");
              BasicDBObject article = convertToDBObj(datum);
              ObjectId id = new ObjectId();
              article.append("_id", id);
              coll.insertOne(new Document(article));
              //ids.add(id);
            }
            //FindIterable<Document> docs =coll.find(article);
            
            //for(Document doc : docs){
            //  System.out.println("ARTICLE :: " + article.getObjectId("_id"));
            //  System.out.println("DOC STRING ::"+doc.get("_id"));
            //  System.out.println("DOC TITLE :: " + doc.getString("title"));
            //}
            //int cursorSize = cursor.size();
            //System.out.println("cursorSize :: "+ cursorSize);
              // if we find more than 1 document here.. we have done something
              // wrong
            //if(cursor.size()!=1){
            //  System.out.println("We done goofed");
            //} 
            //System.out.println("Document :: " + cursor.get(0));
            //String myId =  cursor.get(0).
            //ids.put();
            
            //System.out.println("myObj :: " + myObj);
          } 
            // you are going to have to find it with an id or something
            // then do the processing of the style for the datum and add it
            // into the db for that particular entry
            try{                                                                                          
                  
                  System.out.println("Creating Bibs");              
                  String bib = niallMakeBib( myProcess, datum).makeString().replace("null", " ").replace("\n", " ");                                                                                                                                     
                  String annotatedBib = niallMakeBib(myAltProcess, datum).makeString().replace("null", " ").replace("\n", " ");
                  BasicDBObject citation = new BasicDBObject();                                          
                  citation.append("style", style);                                                   
                  citation.append("bib" , bib);                                                          
                  citation.append("annotatedBib", annotatedBib);                                           
                  BasicDBObject citeToAdd = new BasicDBObject("citation", citation);
                  BasicDBObject updateQuery = new BasicDBObject("$push", citeToAdd);                      
                  //coll.updateOne(article,updateQuery);
                  //ObjectId updateId = ids.get(entryCount);
                  //System.out.println("updateId : " + updateId);
                  BasicDBObject searchQuery = new BasicDBObject().append("skip", entryCount);
                  coll.updateOne(searchQuery, updateQuery);
                  System.out.println("UpdateSuccesful");                  
  //  } catch(java.lang.IllegalArgumentException | ClassCastException | CodecConfigurationException e){     
   //  System.out.println("skipped one: ");                                                               
  //  e.printStackTrace(); 
            // use this for updating          //
      //   coll.update({"_id": ObjectID("65..424")}, push thingy})
            
            //String id = ids.get(entryCount);
        } catch(java.lang.IllegalArgumentException | ClassCastException | CodecConfigurationException e){                              
            System.out.println("skipped one: ");                      
            e.printStackTrace();                    
            //test++;
            entryCount++;            
            continue;                          
          
          }
          //System.exit(1);
          entryCount++;          
        }

        check = false;
        styleCount++;
      }
}
      catch( ParseException |IOException  e){   
          e.printStackTrace();                          
           }                                                  
           // close database;                                                              
           System.out.println("Finished");                                              
           mongoClient.close();                                                          
           }


      /* for(Map.Entry entry : CSLmap.entrySet()){				
         System.out.println("count :" + count);
         CSLItemData datum = (CSLItemData) entry.getValue();
         BasicDBObject article = convertToDBObj(datum);
      //System.out.println("Inserting Document");
      coll.insertOne(new Document(article));
      System.out.println("Inserted");
      */ 
      //try{
      //Bibliography myNewBib =  myProcessor.makeAdhocBibliography(SelectionMode.INCLUDE, datum );
      //myProcessor.setOutputFormat("text");
      //Bibliography myNewBib = myProcessor.makeBibliography(SelectionMode.INCLUDE,datum); 
      //System.out.println("myNewBib" + myNewBib.makeString());
      //System.exit(1);  
      //Bibliography myNewBib = myProcessor.makeAdhocBibliography("ieee","text", datum);
      //System.out.println("myNewBib : " + myNewBib.makeString());  
      //} catch(IOException e){
      //  System.out.print(e);
      //}

      /*
         int test = 0;
         int max = 1000;
         long startTimeWhole = System.currentTimeMillis();
      //System.out.println("styles.length :: " + styles.length());
      String style ="";
      for(int i=0 ;i < myProcessors.size(); i++){
      style = styles.get(i);
      try{

      String bib = niallMakeBib( myProcessor, datum).makeString().replace("null", " ").replace("/n", " ");

      String pathToStyle = "modCSL/styles/" + style + ".csl"; 
      String altStyle = new String(Files.readAllBytes(Paths.get(pathToStyle)));

      String annotatedBib = niallMakeBib(myAltProcessor, datum).makeString().replace("null", " ").replace("/n", " ");
      BasicDBObject citation = new BasicDBObject();
      citation.append("style", style);
      citation.append("bib" , bib);
      citation.append("annotatedBib", annotatedBib);
      BasicDBObject citeToAdd = new BasicDBObject("citation", citation);
      BasicDBObject updateQuery = new BasicDBObject("$push", citeToAdd);
      coll.updateOne(article,updateQuery); 
      */
//} catch(java.lang.IllegalArgumentException | ClassCastException | CodecConfigurationException e){
//  System.out.println("skipped one: ");
//  e.printStackTrace();
//  test++;  
//  continue;
//}
//System.exit(1);
//      }
//long endTime = System.currentTimeMillis();
//long totalTime = startTimeWhole ;
//System.out.println("it took " +(endTime - startTimeWhole)/1000.0);
//System.exit(1);
//
//System.out.println("skipped count" + test);
//count++;
//      }

//}catch( ParseException |IOException  e){
//  e.printStackTrace();
//}
// close database
//System.out.println("Finished");
//mongoClient.close();
//}

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
public static Bibliography niallMakeBib(CSL csl,
    CSLItemData... item) throws IOException{
  //ItemDataProvider provider = new ListItemDataProvider(item);
  String [] ids = new String[item.length];
  for (int i = 0; i< item.length; i++){
    ids[i] = item[i].getId();
  }
  csl.registerCitationItems(ids);

  // May need to reset the CSL before I pass the bibliography back.. as they
  // may stack..
  return csl.makeBibliography();
}

/*public static void updateNullEntires(CSLItemData item) throws IllegalAccessException{
  for(Field f : item.getClass().getDeclaredFields()){
  Object myObj =  f.get(item);
  if( myObj == null && myObj instanceof String){
  myObj = " ";
  }
  }
}  
*/

public static BasicDBObject convertToDBObj(CSLItemData data){
  
  StringJsonBuilder myBuilder = new StringJsonBuilder(new StringJsonBuilderFactory());      
  Object obj =  data.toJson(myBuilder);
  //System.out.println("obj = " + obj.toString());
  BasicDBObject document =  BasicDBObject.parse(obj.toString());
  
  /*
  
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
    document.append("author", sb.toString().replace("/n", " ").replace("/r", " "));
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
  if(data.getTitle()==null){
    
  }
  else{
    document.append("title", data.getTitle().replace("/n", " ").replace("/r", " "));
  }

  document.append("type", data.getType().toString());                                     
  //document.append("title", data.getTitle());                                              
  document.append("id" , data.getId());                                                   
  //System.out.println("data.getAccessed.getRaw :::" + data.getAccessed().getRaw());
  //System.out.println("data.getAccessed.getLiteral ::: " + data.getAccessed().getLiteral());
  StringJsonBuilder myBuilder = new StringJsonBuilder(new StringJsonBuilderFactory());
  JSONObject obj = date.toJson(myBuilder); 
  CSLDate date = data.getAccessed();
  if(date == null){
    document.append("Accessed", null);
  }
  else{
    document.append("Accessed", date.getLiteral());
  }
  //document.append("Accessed" , data.getAccessed().getLiteral());                          
  document.append("Annote" , data.getAnnote());                                           
  document.append("ChapterNumber", data.getChapterNumber());                              
  document.append("CitationLabel", data.getCitationLabel());                              
  document.append("CitationNumber", data.getCitationNumber());                            
  document.append("CollectionNumber", data.getCollectionNumber());                        
  document.append("CollectionTitle", data.getCollectionTitle());                          
  date = data.getContainer();
  if(date == null){
    document.append("Container", null);
  }
  else{
    document.append("Container", date.getLiteral());
  }
  //document.append("Containter" , data.getContainer().getLiteral());                       
  date = data.getContainer();
  if(date == null){
    document.append("EventDate" , null);
  }
  else{
    document.append("EventDate" , date.getLiteral());
  }
  //document.append("EventDate" , data.getEventDate().getLiteral());                        
  date = data.getIssued();
  if(date == null){
    document.append("Issued", null);
  }
  else{
    document.append("Issued", date.getLiteral());
  }
  //document.append("getIssued" , data.getIssued().getLiteral());                           
  date = data.getOriginalDate();
  if(date == null){
      document.append("OriginalDate", null);
  }
  else{
    document.append("OriginalDate", date.getLiteral());
  }
  //document.append("OriginalDate", data.getOriginalDate().getLiteral());                   
  date = data.getSubmitted();
  if(date == null){
    document.append("Submitted", null);
  }
  else{
    document.append("Submitted", date.getLiteral());
  }
  //document.append("Submitted" , data.getSubmitted().getLiteral());
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
  
  */
  document.append("skip", skip);
  skip++; 
  //System.exit(1);
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
/*
   public static void printMapCSL(Map<String, CSLItemData> map){
   for(Map.Entry entry : map.entrySet()){
   System.out.println(entry.getKey() + ", " + entry.getValue());
   }
   }
   }*/

}
