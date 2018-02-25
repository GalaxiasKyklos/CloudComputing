package mx.iteso.desi.cloud.keyvalue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.BatchWriteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.TableWriteItems;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.WriteRequest;
import com.amazonaws.services.dynamodbv2.util.TableUtils;

import mx.iteso.desi.cloud.lp1.Config;

public class DynamoDBStorage extends BasicKeyValueStore {

    private String dbName;
    private DynamoDB dynamoDB;
    private AmazonDynamoDB client;
    // Simple autoincrement counter to make sure we have unique entries
    private int inx;
    private Set<Item> items;

    // private Set<String> attributesToGet = new HashSet<String>();

    public DynamoDBStorage(String dbName) {
        this.dbName = dbName;
        this.init();
    }

    @Override
    public Set<String> get(String search) {
        Set<String> result = new HashSet<>();
        Table table = dynamoDB.getTable(this.dbName);
        System.out.println("Query for item");

        QuerySpec spec = new QuerySpec().withKeyConditionExpression("keyword = :v_keyword")
                .withValueMap(new ValueMap().withString(":v_keyword", search));

        ItemCollection<QueryOutcome> items = table.query(spec);

        Iterator<Item> iterator = items.iterator();
        Item item = null;
        while (iterator.hasNext()) {
            item = iterator.next();
            result.add(item.getString("value"));
        }
        return result;
    }

    @Override
    public boolean exists(String search) {
        if (this.items.isEmpty()) {
            Table table = dynamoDB.getTable(this.dbName);

            QuerySpec spec = new QuerySpec().withKeyConditionExpression("keyword = :v_keyword")
                    .withValueMap(new ValueMap().withString(":v_keyword", search));

            ItemCollection<QueryOutcome> items = table.query(spec);

            return items.getAccumulatedItemCount() > 0;
        } else {
            return this.items.parallelStream().anyMatch(item -> item.getString("keyword").equals(search));
        }
    }

    @Override
    public Set<String> getPrefix(String search) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addToSet(String keyword, String value) {
        this.put(keyword, value);
    }

    @Override
    public void put(String keyword, String value) {
        try {
            Item item = new Item().withPrimaryKey(new PrimaryKey("keyword", keyword, "inx", this.inx++))
                    .withString("value", value);
            this.items.add(item);
        } catch (Exception e) {
            System.err.println("Create items failed.");
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void close() {
        this.addAllItems();
        System.out.println("**CLOSING**");
        this.dynamoDB.shutdown();
        this.client.shutdown();
    }

    @Override
    public boolean supportsPrefixes() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void sync() {
    }

    @Override
    public boolean isCompressible() {
        return false;
    }

    @Override
    public boolean supportsMoreThan256Attributes() {
        return true;
    }

    private void init() {
        try {
            this.client = AmazonDynamoDBClientBuilder.standard().withEndpointConfiguration(
                    new AwsClientBuilder.EndpointConfiguration(Config.amazonEndpoint, Config.amazonRegion.getName()))
                    .build();
            this.dynamoDB = new DynamoDB(client);
            this.inx = 0;
            this.items = new HashSet<>();

            List<AttributeDefinition> attributes = new ArrayList<>();
            attributes.add(new AttributeDefinition("keyword", "S"));
            attributes.add(new AttributeDefinition("inx", "N"));

            List<KeySchemaElement> keySchemas = new ArrayList<>();
            keySchemas.add(new KeySchemaElement("keyword", "HASH"));
            keySchemas.add(new KeySchemaElement("inx", "RANGE"));

            ProvisionedThroughput throughput = new ProvisionedThroughput(1l, 1l);

            CreateTableRequest createTableRequest = new CreateTableRequest(attributes, this.dbName, keySchemas,
                    throughput);
            TableUtils.createTableIfNotExists(this.client, createTableRequest);
            TableUtils.waitUntilActive(this.client, this.dbName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addAllItems() {
        if (!this.items.isEmpty()) {
            System.out.println(this.items.size());
            try {
                System.out.println("**PUTTING ITEMS**");
                Set<Item> subset;
                int cont = 0;
                while (cont < this.items.size()) {
                    subset = this.items.stream().skip(cont).limit(25).collect(Collectors.toSet());
                    cont += 25;
                    TableWriteItems tableItems = new TableWriteItems(this.dbName).withItemsToPut(subset);

                    System.out.println("Making the request. " + cont);
                    BatchWriteItemOutcome outcome = this.dynamoDB.batchWriteItem(tableItems);

                    do {
                        // Check for unprocessed keys which could happen if you exceed
                        // provisioned throughput
                        Map<String, List<WriteRequest>> unprocessedItems = outcome.getUnprocessedItems();

                        if (outcome.getUnprocessedItems().size() == 0) {
                            System.out.println("No unprocessed items found");
                        } else {
                            System.out.println("Writting unprocessed items");
                            outcome = this.dynamoDB.batchWriteItemUnprocessed(unprocessedItems);
                        }
                    } while (outcome.getUnprocessedItems().size() > 0);
                }
            } catch (Exception e) {
                System.err.println("Failed to retrieve items: ");
                e.printStackTrace(System.err);
                System.err.println(this.inx);
            }
        }
    }
}
