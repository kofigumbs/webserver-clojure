(ns webserver.core-test
  (:require [speclj.core :refer :all]
            [webserver.core :refer :all]
            [webserver.mock-socket]))

(describe "Command line argument"
  (it "assigns correct port"
    (should= 80 (extract-port ["-p" "80"]))
    (should= 8080 (extract-port ["-p" "8080"]))
    (should= DEFAULT_PORT (extract-port ["-p" "asdf"]))
    (should= DEFAULT_PORT (extract-port []))))

(describe "Request parsing"
  (it "is short and correct"
    (should=
      {:method "GET" :uri "http://yahoo.com" :version "HTTP/1.1"}
      (extract-headers
        (webserver.mock-socket/make "GET http://yahoo.com HTTP/1.1\r\n\r\n")))
    (should (valid-headers {:method "GET" :uri "/" :version "HTTP/1.1"})))

  (it "is short and malformed"
    (should-not
      (valid-headers (extract-headers (webserver.mock-socket/make "foobar"))))
    (should-not
      (valid-headers
        (extract-headers
          (webserver.mock-socket/make "GET http://yahoo.com\r\n\r\n")))))

  (with request (str
                  "POST /path/script.cgi HTTP/1.0\r\n"
                  "From: frog@jmarshall.com\r\n"
                  "User-Agent: HTTPTool/1.0\r\n"
                  "Content-Type: application/x-www-form-urlencoded\r\n"
                  "Content-Length: 32\r\n\r\n"
                  "home=Cosby&favorite+flavor=flies"))

  (it "is long and correct"
    (should=
      {:method "POST" :uri "/path/script.cgi" :version "HTTP/1.0"
       :From "frog@jmarshall.com"
       :User-Agent "HTTPTool/1.0"
       :Content-Type "application/x-www-form-urlencoded"
       :Content-Length "32"}
      (extract-headers (webserver.mock-socket/make @request)))))

(describe "400 response"
  (with socket (webserver.mock-socket/make ""))
  (it "writes straight to socket"
    (should=
      "HTTP/1.1 400 Bad Request\r\n"
      (do
        (respond-400 @socket)
        (str (.getOutputStream @socket))))))

