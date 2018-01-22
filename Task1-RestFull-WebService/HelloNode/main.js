const http = require('http')

const server = http.createServer((req, res) => {
  res.writeHead(200, {
    ContentType: 'text/plain'
  })
  res.end('Hello World\n')
})

server.listen(8000)

console.log('Server running at http://localhost:8000/')
