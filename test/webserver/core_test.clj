(ns webserver.core-test
  (:require [speclj.core :refer :all]
            [webserver.core :as core]
            [webserver.response :as response]
            [webserver.mock-socket :as socket]))

(describe "Command line argument"
  (it "assigns correct port"
    (should= 80 (core/extract-port ["-p" "80"]))
    (should= 8080 (core/extract-port ["-p" "8080"]))
    (should= core/DEFAULT_PORT (core/extract-port ["-p" "asdf"]))
    (should= core/DEFAULT_PORT (core/extract-port []))))

(describe "Request parsing"
  (it "is short and correct"
    (should=
      {:method "GET" :uri "http://yahoo.com" :version "HTTP/1.1"}
      (core/extract-headers
        (socket/make "GET http://yahoo.com HTTP/1.1\r\n\r\n")))
    (should
      (core/valid-headers
        {:method "GET" :uri "/" :version "HTTP/1.1"})))

  (it "is short and malformed"
    (should-not
      (core/valid-headers
        (core/extract-headers
          (socket/make "foobar"))))
    (should-not
      (core/valid-headers
        (core/extract-headers
          (socket/make "GET http://yahoo.com\r\n\r\n")))))

  (with-all request (str
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
      (core/extract-headers (socket/make @request))))

  (it "treats GET parameters as map key"
    (should=
      {:method "GET"
       :uri "localhost:8000"
       :version "HTTP/1.1"
       :parameters "%3C%2C"}
      (core/extract-headers
        (socket/make "GET localhost:8000?%3C%2C HTTP/1.1\r\n\r\n")))))

(describe "400 response"
  (with socket (socket/make ""))
  (it "writes straight to socket"
    (should=
      (response/make 400)
      (do
        (core/respond-400 @socket)
        (str (.getOutputStream @socket))))))

