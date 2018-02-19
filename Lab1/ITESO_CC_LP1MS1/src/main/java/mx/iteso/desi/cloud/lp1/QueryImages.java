package mx.iteso.desi.cloud.lp1;

import java.util.Set;
import java.util.Iterator;
import java.util.HashSet;
import mx.iteso.desi.cloud.keyvalue.KeyValueStoreFactory;
import mx.iteso.desi.cloud.keyvalue.PorterStemmer;
import mx.iteso.desi.cloud.keyvalue.IKeyValueStorage;

public class QueryImages {
  IKeyValueStorage imageStore;
  IKeyValueStorage titleStore;

  public QueryImages(IKeyValueStorage imageStore, IKeyValueStorage titleStore) {
    this.imageStore = imageStore;
    this.titleStore = titleStore;
  }

  public Set<String> query(String word) {
    Set<String> values = titleStore.get(PorterStemmer.stem(word.toLowerCase()));
    Set<String> urls = new HashSet<>();
    for (String v : values) {
      urls.addAll(imageStore.get(v));
    }
    return urls;
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

      QueryImages myQuery = new QueryImages(imageStore, titleStore);

      for (int i = 0; i < args.length; i++) {
        System.out.println(args[i] + ":");
        Set<String> result = myQuery.query(args[i]);
        Iterator<String> iter = result.iterator();
        while (iter.hasNext())
          System.out.println("  - " + iter.next());
      }

      myQuery.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
