import java.nio.*;
import java.io.*;
import java.util.*;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.util.Version;

public class Indexer {

	public static void main(String[] args) throws IOException, ParseException {

		/* Creating new index and writer. */
		// Starts creating the analyzer for different fields.
		Map<String,Analyzer> analyzerPerField = new HashMap<String,Analyzer>();
		// Creates plain analyzer for the title field.
		analyzerPerField.put("title", new TitleAnalyzer());
		// Combines the title analyzer with the general standard analyzer.
		PerFieldAnalyzerWrapper aWrapper = new PerFieldAnalyzerWrapper(new StandardAnalyzer(), analyzerPerField);
		Directory directory = new RAMDirectory();
		IndexWriterConfig config = new IndexWriterConfig(aWrapper);
		IndexWriter writer = new IndexWriter(directory, config);
		Document document = new Document();

		/* Starts to create documents. */
		File[] data = getFileNames();
		String text = "none";
		String temp;
		String[] lines;
		Scanner sc;
		// Iterates through each text file (aka each movie).
		for (int i = 0; i < data.length; i++) {
			temp = "";

			// Reads the text file for its content.
			sc = new Scanner(data[i]);
			if (sc.hasNextLine()) {
				text = sc.useDelimiter("\\A").next();
			}

			// Splits the text looking for fields.
			lines = text.split("\\r?\\n");
			// Stores the title of the movie (first line of file).
			document.add(new TextField("title", lines[0], Field.Store.YES));
			for (int j = 1; j < lines.length; j += 2) {
				// Finds when the plot or movie info happens.
				if (j + 1 >= lines.length || lines[j].split("\\s+").length > 10) {
					// Stores the final content paragraph.
					for (j = j; j < lines.length; j += 1) {
						temp += lines[j] + "\n";
					}
					document.add(new TextField("content", temp, Field.Store.YES));
					break;
				}
				// Stores the fields that are found.
				document.add(new TextField(lines[j].toLowerCase(), lines[j + 1], Field.Store.YES));
			}

			// Adds each document to the indexer.
			writer.addDocument(document);
			document = new Document();
		}
		writer.close();

		/* Create searcher. */
		IndexReader reader = DirectoryReader.open(directory);
		IndexSearcher searcher = new IndexSearcher(reader);
		// Creates parser for multiple fields and query words.
		String[] fields = {"title", "content", "written by"};
		MultiFieldQueryParser parser = new MultiFieldQueryParser(fields, aWrapper);
		Query query = parser.parse(String.join(" ", args));
		// Obtains the top 10 results and prints number of total hits.
		TopDocs results = searcher.search(query, 10);
		System.out.println("# of Hits: " + results.totalHits);

		/* Prints the content of the results. */
		ScoreDoc[] hits = results.scoreDocs;
		for(int i = 0; i < hits.length; i++) {
		    int docId = hits[i].doc;
		    Document d = searcher.doc(docId);
		    System.out.println((i + 1) + ". " + d.get("title"));
		    System.out.println(d.get("content"));
		}

	}

	/* Returns array of all file names. */
	public static File[] getFileNames() {
		File folder = new File("data/");
		return folder.listFiles();
	}

	/* Analyzer with StandardFilter and LowerCaseFilter. */
	public static class TitleAnalyzer extends Analyzer {

		@Override
		protected TokenStreamComponents createComponents(String fieldName) {

			StandardTokenizer tokenizer = new StandardTokenizer();

			TokenFilter filter = new StandardFilter(tokenizer);
			filter = new LowerCaseFilter(filter);

			return new TokenStreamComponents(tokenizer, filter);
		}

	}

}
