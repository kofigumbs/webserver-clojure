(ns webserver.headers-test
  (:require [speclj.core :refer :all]
            [webserver.headers :as headers]
            [webserver.mock-socket :as socket]))

(describe "Request parsing"
  (it "is short and correct"
    (should=
      {:method "GET" :uri "http://yahoo.com" :version "HTTP/1.1"}
      (headers/extract
        (socket/make "GET http://yahoo.com HTTP/1.1\r\n\r\n")))
    (should
      (headers/valid?
        {:method "GET" :uri "/" :version "HTTP/1.1"})))

  (it "is short and malformed"
    (should-not
      (headers/valid?
        (headers/extract
          (socket/make "foobar"))))
    (should-not
      (headers/valid?
        (headers/extract
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
      (headers/extract (socket/make @request)))))
