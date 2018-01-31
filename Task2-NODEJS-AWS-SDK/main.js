const AWS = require('aws-sdk')
const s3 = new AWS.S3()

const params = {}
s3.listBuckets(params, (err, data) => {
  if (err) {
    console.log(err, err.stack)
  } else {
    console.log(data)
  }
})
