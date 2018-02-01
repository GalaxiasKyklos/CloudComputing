const fs = require('fs')
const util = require('util');
const AWS = require('aws-sdk')
const s3 = new AWS.S3()
const readFile = util.promisify(fs.readFile)

async function getBuckets(s3, params) {
  try {
    let data = await s3.listBuckets(params).promise()
    return data
  } catch (error) {
    console.error(error)
    return false
  }
}

async function createBucket(s3, params) {
  try {
    await s3.createBucket(params).promise()
  } catch (error) {
    if (error.code === 'BucketAlreadyOwnedByYou') {
      console.error('Bucket already exists')
    } else {
      console.error(error)
      return false
    }
  }
  return true
}

async function addFile(s3, params) {
  try {
    let data = await s3.putObject(params).promise()
    return data;
  } catch (error) {
    console.error(error)
    return false
  }
}

async function getFile(s3, params) {
  try {
    let data = await s3.getObject(params).promise()
    return data
  } catch (error) {
    console.error(error)
    return false
  }
}

async function getBinaryFileFS(name) {
  try {
    let data = await readFile(name)
    let base64Data = new Buffer(data, 'binary')
    return base64Data
  } catch (error) {
    console.error(error)
    return false
  }
}


async function mainFunction(s3) {
  // Step 3, getting all the buckets
  console.log((await getBuckets(s3, {})))

  // Step 4
  // Create a bucket and add a text file
  const bucketName = 'is699399-test0'
  let fileKey = 'file01.txt'
  let params = {
    Bucket: bucketName
  }
  let bucketCreated = await createBucket(s3, params)
  if (bucketCreated) {
    params = {
      Bucket: bucketName,
      Key: fileKey,
      Body: 'Hello World!'
    }

    let fileCreated = await addFile(s3, params)
    if (fileCreated) {
      console.log(`File created ETag: ${fileCreated.ETag}`)
      
      // Get the contents of the file
      params = {
        Bucket: bucketName,
        Key: fileKey
      }
      
      let fileData = await getFile(s3, params)
      if (fileData) {
        console.log(fileData)
        console.log(fileData.Body.toString())
      }
    }

    // Uploading a binary file
    fileKey = 'main.js'
    const filePath = './Task2-NODEJS-AWS-SDK/Steps-0-to-4/main.js'
    let binaryFile = await getBinaryFileFS(filePath)
    if (binaryFile) {
      params = {
        Bucket: bucketName,
        Key: fileKey,
        Body: binaryFile
      }
      
      let binaryFileCreated = await addFile(s3, params)
      
      if (binaryFileCreated) {
        console.log(`File created ETag: ${binaryFileCreated.ETag}`)
      }
    }
  }
}

mainFunction(s3)
