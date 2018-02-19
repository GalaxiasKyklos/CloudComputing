package mx.iteso.desi.cloud.lp1;

import java.io.IOException;

import mx.iteso.desi.cloud.keyvalue.IKeyValueStorage;
import mx.iteso.desi.cloud.keyvalue.KeyValueStoreFactory;
import mx.iteso.desi.cloud.keyvalue.ParseTriples;
import mx.iteso.desi.cloud.keyvalue.PorterStemmer;
import mx.iteso.desi.cloud.keyvalue.Triple;

public class IndexImages {
  ParseTriples parser;
  IKeyValueStorage imageStore, titleStore;

  public IndexImages(IKeyValueStorage imageStore, IKeyValueStorage titleStore) {
    this.imageStore = imageStore;
    this.titleStore = titleStore;
  }

  public void run(String imageFileName, String titleFileName) throws IOException {
    ParseTriples parseImages = new ParseTriples("/Users/saul/Downloads/" + imageFileName);
    Triple triple;
    while ((triple = parseImages.getNextTriple()) != null) {
      String predicade = triple.getPredicate();
      if (predicade.equals("http://xmlns.com/foaf/0.1/depiction")) {
        String key = triple.getSubject();
        String value = triple.getObject();
        this.imageStore.addToSet(key, value);
      }
    }
    parseImages.close();

    ParseTriples parseTitles = new ParseTriples("/Users/saul/Downloads/" + titleFileName);
    while ((triple = parseTitles.getNextTriple()) != null) {
      String predicade = triple.getPredicate();
      String value = triple.getSubject();
      if (this.imageStore.exists(value)) {
        if (predicade.equals("http://www.w3.org/2000/01/rdf-schema#label")) {
          String key = triple.getObject();
          key = key.replace("[^A-Za-z ]", "");
          // CamelCase-spliting fuckery:
          // https://stackoverflow.com/questions/7593969/regex-to-split-camelcase-or-titlecase-advanced
          String[] keys = key.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])");
          for (String keyWord : keys) {
            keyWord = keyWord.trim();
            for (String finalKey : keyWord.split(" ")) {
              finalKey = PorterStemmer.stem(finalKey.toLowerCase());
              if (!finalKey.equals("Invalid term") && !finalKey.equals("No term entered")) {
                this.titleStore.addToSet(finalKey, value);
              }
            }
          }
        }
      }
    }
    parseTitles.close();
  }

  public void close() {
    this.imageStore.close();
    this.titleStore.close();
  }

  public static void main(String args[]) {
    System.out.println("*** Alumno: Saúl Ponce (Exp: is699399)");
    try {

      IKeyValueStorage imageStore = KeyValueStoreFactory.getNewKeyValueStore(Config.storeType, "images");
      IKeyValueStorage titleStore = KeyValueStoreFactory.getNewKeyValueStore(Config.storeType, "terms");

      IndexImages indexer = new IndexImages(imageStore, titleStore);
      indexer.run(Config.imageFileName, Config.titleFileName);
      indexer.close();
      System.out.println("Indexing completed");

    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Failed to complete the indexing pass -- exiting");
    }
  }
}
