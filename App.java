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
import org.json.*;
import java.lang.StringBuilder;

import java.lang.IllegalArgumentException;
import org.bson.codecs.configuration.CodecConfigurationException;

public class App{

	public static void main(String[] args){
		MongoClient mongoClient = new MongoClient();
		MongoDatabase database = mongoClient.getDatabase("1000db");

		MongoCollection<Document> coll = database.getCollection("articles");

		//System.out.println(coll.find());

		// grabbing the citation style names
		ArrayList<String> styles = getStyleNames();

		try{
			BibTeXDatabase dbBib = new BibTeXConverter().loadDatabase(
					new FileInputStream("1000.bib"));

			BibTeXItemDataProvider provider = new BibTeXItemDataProvider();
			provider.addDatabase(dbBib);

			//CSL citeproc = new CSL(provider, "ieee");
			//citeproc.setOutputFormat("text");

			BibTeXConverter converter = new BibTeXConverter();
			Map<String,CSLItemData> CSLmap = converter.toItemData(dbBib);
			//Map<Key, BibTeXEntry> map = dbBib.getEntries(); 
			//printMapCSL(map);
			//coll.remove({});
			//ArrayList<org.jbibtex.BibTeXObject> entryMap =(ArrayList<org.jbibtex.BibTeXObject>) dbBib.getObjects();

			//for(org.jbibtex.BibTeXObject entry : entryMap){
					
		//	}
			//System.exit(1);
		System.out.println("Data is loaded intobibtex converter db");
		System.out.println("starting conversion");
		int count =0;
        //for(int retries =0;;retries++){	
			for(Map.Entry entry : CSLmap.entrySet()){				
			    System.out.println("count :" + count);
                CSLItemData datum = (CSLItemData) entry.getValue();
				BasicDBObject article = convertToDBObj(datum);
				System.out.println("Inserting Document");
                coll.insertOne(new Document(article));
                System.out.println("Inserted");
				//System.exit(1);
                try{

				int test = 0;
				int max = 1000; 
				for(String style : styles){
					if(test>= max){
						break;
					} 
					//System.out.println("count == "+ test);
                    //System.out.println("style = "+ style);
					// TODO
                    try{
                        String bib = CSL.makeAdhocBibliography(style, "text",
						(CSLItemData) entry.getValue()).makeString();
                    					
					BasicDBObject citation = new BasicDBObject();
					citation.append("style", style);
					citation.append("bib" , bib);
					BasicDBObject citeToAdd = new BasicDBObject("citation", citation);
					BasicDBObject updateQuery = new BasicDBObject("$push", citeToAdd);
					coll.updateOne(article,updateQuery); 
					//JsonObject jObj = (JsonObject) entry.getValue();
					//MapJsonBuilderFactory thingy = new MapJsonBuilderFactory();
					//JsonBuilder myBuilder = thingy.createJsonBuilder();	
					
					test++;
                    } catch(java.lang.IllegalArgumentException e){
                        continue;
                    }

				}
				}
				catch(ClassCastException | CodecConfigurationException e){
					//if(retries < 20){
						System.out.println("skipped one: ");
						System.out.println(e);
                    //}else{
					//	System.out.println(e);
					//}
				}
                count++;
				//System.exit(1);
			}
		//}
			
		}catch(ParseException | IOException | CodecConfigurationException e){
			System.out.print(e);	
		}

		// close database
		System.out.println("Finished");
		mongoClient.close();

	}

	public static String toHex(String arg) {
		    return String.format("%040x", new BigInteger(1, arg.getBytes(/*YOUR_CHARSET?*/)));
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
            for (CSLName n : authors) { 
				    if (sb.length() > 0) sb.append(" and ");
					sb.append(n.getFamily()+ ", " + n.getGiven() );
					//System.out.println("Author = Given :" + n.getGiven() + "----Family :" + n.getFamily());
			}
            for (CSLName n : authors){
                if( editorSb.length() > 0) editorSb.append(" and ");
                editorSb.append(n.getFamily()+ ", " + n.getGiven());
            }
			//System.out.println("Author --- sb -- = " + sb.toString());
            document.append("type", data.getType().toString());
			document.append("author", sb.toString());
			document.append("title", data.getTitle());
			System.out.println("title : " +data.getTitle());
			document.append("number", data.getNumber());
			document.append("numpages", data.getNumberOfPages());
			document.append("pages", data.getPage());
			document.append("publisher", data.getPublisher());
			document.append("volume", data.getVolume());
			document.append("year", data.getYearSuffix());
			document.append("journal",data.getJournalAbbreviation());
			document.append("editor", editorSb.toString());
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
		for (int i=0;i<fileList2.length;i++ ) {
			String fileName = fileList2[i].getName();
			if(fileList2[i].isFile()){
				styles.add(fileName);
			}
		}
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
