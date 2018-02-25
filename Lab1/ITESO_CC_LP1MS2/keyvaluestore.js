  var AWS = require('aws-sdk');
  AWS.config.loadFromPath('./config.json');

  var db = new AWS.DynamoDB();

  function keyvaluestore(table) {
    this.LRU = require("lru-cache");
    this.cache = this.LRU({
      max: 500
    });
    this.tableName = table;
  };

  /**
   * Initialize the tables
   * 
   */
  keyvaluestore.prototype.init = async function (whendone) {

    var tableName = this.tableName;
    var self = this;
    var params = {
      TableName: tableName /* required */
    };

    try {
      const table = await dynamodb.describeTable(params).promise();
      whendone(); //Call Callback function.
    } catch (e) {

    }
  };

  /**
   * Get result(s) by key
   * 
   * @param search
   * 
   * Callback returns a list of objects with keys "inx" and "value"
   */

  keyvaluestore.prototype.get = async function (search, callback) {
    var self = this;
    const { stemmer } = require('porter-stemmer');
    const stemmedword = stemmer(search).toLowerCase(); 
    if (self.cache.get(stemmedword)) {
      console.log('Cached value');
      callback(null, self.cache.get(stemmedword));
    } else {

      /*
       * 
       * La función QUERY debe generar un arreglo de objetos JSON son cada
       * una de los resultados obtenidos. (inx, value, key).
       * Al final este arreglo debe ser insertado al cache. Y llamar a callback
       * 
       * Ejemplo:
       *    var items = [];
       *    items.push({"inx": data.Items[0].inx.N, "value": data.Items[0].value.S, "key": data.Items[0].key});
       *    self.cache.set(search, items)
       *    callback(err, items);
       */
      var docClient = new AWS.DynamoDB.DocumentClient();

      var params = {
        TableName: this.tableName,
        KeyConditionExpression: "#k = :v_keyword",
        ExpressionAttributeNames: {
          "#k": "keyword"
        },
        ExpressionAttributeValues: {
          ":v_keyword": search
        }
      };

      try {
        const items = await docClient.query(params).promise();
        callback(false, items);
      } catch (err) {
        callback(err, null);
      }
    }
  };


  module.exports = keyvaluestore;