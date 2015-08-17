(ns webserver.socket-test
  (:require [speclj.core :refer :all]
            [webserver.socket :refer :all]
            [webserver.mock-socket]))

(describe "Input stream mocking"
  (it "returns empty string for invalid request"
    (should= "" (get-request (webserver.mock-socket/make "foobar")))
    )

  (with request "GET http://example.com HTTP/1.1\r\n\r\n")
  (it "works on something that looks like a request"
    (should= @request (get-request (webserver.mock-socket/make @request)))
    )
  )

(describe "Output stream mocking"
  (with response (str
                   "HTTP/1.1 200 OK\r\n"
                   "Date: Mon, 27 Jul 2009 12:28:53 GMT\r\n"
                   "Server: Apache\r\n"
                   "Last-Modified: Wed, 22 Jul 2009 19:15:56 GMT\r\n"
                   "ETag: \"34aa387-d-1568eb00\"\r\n"
                   "Accept-Ranges: bytes\r\n"
                   "Content-Length: 51\r\n"
                   "Vary: Accept-Encoding\r\n"
                   "Content-Type: text/plain\r\n"
                   "\r\n"
                   "Hello World! My payload includes a trailing CRLF.\r\n"
                   ))
  (with request "GET http://example.com HTTP/1.1\r\n\r\n")
  (with socket (webserver.mock-socket/make ""))
  (it "works with something that looks like a response"
    (should=
         @response
         (do (respond @socket @response)
             (str (.getOutputStream @socket)))))
  )
