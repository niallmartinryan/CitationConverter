///*
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.WriteResult;


//*/
import de.undercouch.citeproc.bibtex.BibTeXConverter;
public class App{
	
	public static void main(String[] args){
		MongoClient mongoClient = new MongoClient();
		DB database = mongoClient.getDB("testCitations");

		DBCollection coll = database.getCollection("art");

		

		




		// close database
		mongoClient.close();

	}
}
