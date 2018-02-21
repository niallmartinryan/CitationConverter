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
//*/
import de.undercouch.citeproc.bibtex.BibTeXConverter;
public class App{
	
	public static void main(String[] args){
		MongoClient mongoClient = new MongoClient();
		MongoDatabase database = mongoClient.getDatabase("testCitations");

		MongoCollection<Document> coll = database.getCollection("art");

		//System.out.println(coll.find());
		

		




		// close database
		mongoClient.close();

	}
}
