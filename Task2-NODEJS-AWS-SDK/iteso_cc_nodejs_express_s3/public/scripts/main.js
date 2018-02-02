const domReady = (callback) => {
  document.readyState === "interactive" ||
    document.readyState === "complete" ? callback() :
    document.addEventListener("DOMContentLoaded", callback)
}

domReady(async () => {

  const submitBucket = document.getElementById('submit-bucket')
  if (submitBucket) {
    submitBucket.addEventListener('click', () => {
      let bucketName = document.getElementById('new-bucket')
      if (bucketName) {
        bucketName = bucketName.value
        const request = new XMLHttpRequest()
        request.onload = (data) => {
          if (request.status !== 201) {
            alert('There was an error creating the bucket')
          } else {
            window.location.reload()
          }
        }
        request.open('Post', '/')
        request.setRequestHeader('Content-Type', 'application/json')
        request.send(JSON.stringify({
          params: {
            Bucket: bucketName
          }
        }))
      }
    }, false)
  }
  const submitObject = document.getElementById('submit-object')
  if (submitObject) {
    submitObject.addEventListener('click', () => {
      let newFile = document.getElementById('new-object')
      if (newFile) {
        newFile = newFile.files[0]
        const formData = new FormData()
        formData.append('newFile', newFile)
        const request = new XMLHttpRequest()
        request.onload = (data) => {
          if (request.status !== 201) {
            alert('There was an error uploading the file')
          } else {
            window.location.reload()
          }
        }
        request.open('Post', window.location.pathname)
        request.send(formData)
      }
    })
  }
})