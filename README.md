# CS242_Info_Retrieval

## Movie Search System

### Overview

This project uses two different frameworks, Lucene and Hadoop, to comparatively search through a large database of movies and returns the closest matching result to the search 

### `LuceneSearch.java`

Parses the large amount of movie data using Lucene and returns the top results.

### `HadoopSearch.java`

Parses the large amount of data and creates several intermediate files before searching through the final result. The first iteration, using `IndexerH.java` counts all the words in a particular movie description, and the second iteration, `IndexerCreate.java`, creates the actual indexer that is used to determine the top results.
