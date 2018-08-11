const http = require('http');
const url = require('url');
const fs = require('fs');


console.log('localhost:8080 started');

http.createServer((request, response) => {
    const path = url.parse(request.url, true).pathname; // url에서 path 추출

    const validPath = [
        '/css/index.css',
        
        '/js/index.js',
        '/js/vue.js',
        '/js/navermap.js',

        '/index.html',
        '/navermap.html',
        '/test.html',

        '/resource/bike-map.png',
        '/resource/hackathon1.png',
        '/resource/hackathon2.png',
        '/resource/title-theme.svg',
    ];

    if (request.method === 'GET') { // GET 요청이면
        if (path === '/') {
            response.writeHead(200, { 'Content-Type': 'text/html' }); // header 설정
            fs.readFile(__dirname + '/index.html', (err, data) => { // 파일 읽는 메소드
                if (err) {
                    return console.error(err);
                } else {
                    console.log('access : ' + __dirname + '/index.html');
                }
                response.end(data, 'utf-8');
            });
        } else if (validPath.includes(path)) {
            if(path.endsWith('css')){
                response.writeHead(200, { 'Content-Type': 'text/css' });
            } else if (path.endsWith('svg')){
                response.writeHead(200, { 'Content-Type': 'image/svg+xml' });
            } else {
                response.writeHead(200, { 'Content-Type': 'text/html' });
            }

            fs.readFile(__dirname + path, (err, data) => { // 파일 읽는 메소드
                if (err) {
                    return console.error(err);
                } else {
                    console.log('access : ' + __dirname + '/index.html');
                }
                response.end(data, 'utf-8');
            });
        } else {
            response.statusCode = 404; // 404 상태 코드
            console.log('404 : path : ' + path);
            response.end('404!!!! 주소가 없습니다');
        }

        // if (path === '/about') { // 주소가 /about이면
        //   response.writeHead(200,{'Content-Type':'text/html'}); // header 설정
        //   fs.readFile(__dirname + '/about.html', (err, data) => { // 파일 읽는 메소드
        //     if (err) {
        //       return console.error(err); // 에러 발생시 에러 기록하고 종료
        //     }
        //     response.end(data, 'utf-8'); // 브라우저로 전송
        //   });
        // } else if (path === '/') { // 주소가 /이면
        //   response.writeHead(200,{'Content-Type':'text/html'});
        //   fs.readFile(__dirname + '/main.html', (err, data) => {
        //     if (err) {
        //       return console.error(err);
        //     }
        //     response.end(data, 'utf-8');
        //   });
        // } else { // 매칭되는 주소가 없으면
        //   response.statusCode = 404; // 404 상태 코드
        //   response.end('주소가 없습니다');
        // }
    }
}).listen(8080);
