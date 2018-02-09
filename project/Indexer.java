import java.nio.*;
import java.io.*;
import java.util.Arrays;
import java.util.Scanner;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.search.ScoreDoc;

public class Indexer {

	public static void main(String[] args) throws IOException, ParseException {

		/* Creating new index and writer. */
		StandardAnalyzer standardAnalyzer = new StandardAnalyzer();
		Directory directory = new RAMDirectory();
		IndexWriterConfig config = new IndexWriterConfig(standardAnalyzer);
		IndexWriter writer = new IndexWriter(directory, config);
		Document document = new Document();

		/* Starts to create documents. */
		File[] data = getFileNames();
		String text = "none";
		Scanner sc;
		for (int i = 0; i < data.length; i++) {
			sc = new Scanner(data[i]);
			if (sc.hasNextLine()) {
				text = sc.useDelimiter("\\A").next();
			}
			document.add(new TextField("content", text, Field.Store.YES));
			writer.addDocument(document);
			document = new Document();
		}
		writer.close();

		/* Create searcher. */
		IndexReader reader = DirectoryReader.open(directory);
		IndexSearcher searcher = new IndexSearcher(reader);
		QueryParser parser = new QueryParser("content", standardAnalyzer);
		System.out.println(Arrays.toString(args));
		Query query = parser.parse(args[0]);
		TopDocs results = searcher.search(query, 5);
		System.out.println("# of Hits: " + results.totalHits);

		ScoreDoc[] hits = results.scoreDocs;
		for(int i = 0; i < hits.length; i++) {
		    int docId = hits[i].doc;
		    Document d = searcher.doc(docId);
		    System.out.println((i + 1) + ". " + d.get("content"));
		}
	}

	/* Returns array of all file names. */
	public static File[] getFileNames() {
		File folder = new File("data/");
		return folder.listFiles();
	}

}
