var express = require('express');
var router = express.Router();
var aws = require('aws-sdk');
var s3 = new aws.S3();

var fileUpload = require('express-fileupload');
router.use(fileUpload());


router.get('/', function (req, res) {
    s3.listBuckets({}, function (err, data) {
        if (err) {
            throw err;
        }
        res.render('listBuckets', {
            buckets: data.Buckets
        });
    });
});

router.get('/:bucket/', async (req, res) => {
    try {
        const params = {
            Bucket: req.params.bucket
        }
        let objects = await s3.listObjectsV2(params).promise()
        res.render('listObjects', {
            objects: objects.Contents,
            bucket: req.params.bucket
        })
    } catch (error) {
        res.status(500).send()
    }
});

router.get('/:bucket/:key', async (req, res) => {
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


router.post('/', async (req, res) => {
    try {
        const params = req.body.params
        console.log(params)
        await s3.createBucket(params).promise()
        res.status(201).send()
    } catch (error) {
        res.status(304).send()
    }
});

router.post('/:bucket', async (req, res) => {
    try {
        const bucketName = req.params.bucket
        const fileKey = req.files.newFile.name
        const binaryFile = req.files.newFile.data
        params = {
            Bucket: bucketName,
            Key: fileKey,
            Body: binaryFile
        }
        const binaryFileCreated = await s3.putObject(params).promise()
        if (binaryFileCreated) {
            res.status(201).send()
        } else {
            res.status(304).send()
        }
    } catch (error) {
        res.status(304).send()
    }
});

module.exports = router;