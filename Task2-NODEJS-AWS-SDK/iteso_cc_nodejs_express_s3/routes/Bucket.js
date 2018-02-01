var express = require('express');
var router = express.Router();
var aws = require('aws-sdk');
var s3 = new aws.S3();

var fileUpload = require('express-fileupload');
router.use(fileUpload());


router.get('/', function(req, res) {
  s3.listBuckets({},function(err,data) {
      if(err) {
          throw err;
      }
      console.log(data);
      res.render('listBuckets', { buckets: data.Buckets });
  });
});

router.get('/:bucket/', async function(req, res) {
    try {
        const params = {
            Bucket: req.params.bucket
        }
        let objects = await s3.listObjectsV2(params).promise()
        res.render('listObjects', { objects: objects.Contents, bucket: req.params.bucket })
    } catch (error) {
        res.status(500).send()
    }
});

router.get('/:bucket/:key', async function(req, res) {
    try {
        const params = {
            Bucket: req.params.bucket,
            Key: req.params.key
        }
        let data = await s3.getObject(params).promise()
        res.type(data.ContentType)
        res.send(new Buffer(data.Body, 'binary'))
    } catch (error) {
        res.status(500).send()
    }
});


router.post('/', function(req,res) {
    /*
     * @TODO - Programa la logica para crear un Bucket.
    */
});

router.post('/:bucket', function(req,res) {

    /*
     * @TODO - Programa la logica para crear un nuevo objeto.
     * TIPS:
     *  req.files contiene todo los archivos enviados mediante post.
     *  cada elemento de files contiene multiple información algunos campos
     *  importanets son:
     *      data -> Buffer con los datos del archivo.
     *      name -> Nombre del archivo original
     *      mimetype -> tipo de archivo.
     *  el conjunto files dentro del req es generado por el modulo 
     *  express-fileupload
     *  
    */
     
});

module.exports = router;
