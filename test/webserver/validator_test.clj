(ns webserver.validator-test
  (:require [speclj.core :refer :all]
            [webserver.validator :refer :all]))

(describe "Request line"
  (with request-line1 "GET http://www.example.com HTTP/1.1")
  (with request-line2 "POST http://8thlight.com HTTP/1.0")
  (with parsed-request-line1 (parse-request-line @request-line1))
  (with parsed-request-line2 (parse-request-line @request-line2))
  (it "is correctly formed"
    (should= (:method @parsed-request-line1) "GET")
    (should= (:uri @parsed-request-line1) "http://www.example.com")
    (should= (:version @parsed-request-line1) "HTTP/1.1")
    (should= (:method @parsed-request-line2) "POST")
    (should= (:uri @parsed-request-line2) "http://8thlight.com")
    (should= (:version @parsed-request-line2) "HTTP/1.0")
    (should (valid-request-line? (parse-request-line @request-line1)))
    )

  (it "is malformed"
    (should-not (valid-request-line?
                  (parse-request-line "GET http://www.example.com")))
    (should-not (valid-request-line?
                  (parse-request-line "GET http://www.example.com foo23")))
    (should-not (valid-request-line?
                  (parse-request-line "get http://www.example.com HTTP/1.1")))
    (should-not (valid-request-line?
                  (parse-request-line
                    "GET http://www.example.com HTTP/1.1 asdf")))
    (should-not (valid-request-line?
                  (parse-request-line
                    "hello GET http://www.example.com HTTP/1.1")))
    )

  )

(describe "Header"
  (with header (str "User-Agent: curl/7.16.3 libcurl/7.16.3\r\n"
                   "Host: www.example.com\r\n"
                   "Accept-Language: en, mi\r\n"
                   "Date: Mon, 10 Aug 2015 12:32:56 GMT\r\n"
                   "Content-Type: text/plain\r\n"
                   "Content-MD5: Q2hlY2sgSW50ZWdyaXR5IQ=="))
  (with parsed-header (parse-header @header))

  (it "is correctly formed"
    (should (valid-header? @parsed-header))
    (should= (:User-Agent @parsed-header) "curl/7.16.3 libcurl/7.16.3")
    (should= (:Host @parsed-header) "www.example.com")
    (should= (:Accept-Language @parsed-header) "en, mi")
    (should= (:Date @parsed-header) "Mon, 10 Aug 2015 12:32:56 GMT")
    (should= (:Content-Type @parsed-header) "text/plain")
    (should= (:Content-MD5 @parsed-header) "Q2hlY2sgSW50ZWdyaXR5IQ==")
    )

  (it "is malformed"
    (should-not (valid-header? (parse-header
                       (str @header "\r\n foo :bar"))))
    (should-not (valid-header? (parse-header
                       "Hello World: cannot have space in name")))
    )
  )

(describe "Entire request"
  (with request (str
                 "POST /path/script.cgi HTTP/1.0\r\n"
                 "From: frog@jmarshall.com\r\n"
                 "User-Agent: HTTPTool/1.0\r\n"
                 "Content-Type: application/x-www-form-urlencoded\r\n"
                 "Content-Length: 32\r\n"
                 "\r\n"
                 "home=Cosby&favorite+flavor=flies"
                 ))
  (with parsed-request (parse-request @request))
  (it "is long and correct"
    (should (valid-request? @parsed-request))
    (should= (:method (:request-line @parsed-request)) "POST")
    (should= (:From (:header @parsed-request)) "frog@jmarshall.com")
    (should= (:body @parsed-request) "home=Cosby&favorite+flavor=flies")
    )

  (it "is short and correct"
    (should (valid-request? (parse-request "GET http://google.com HTTP/1.1\r\n\r\n")))
    )

  (it "is short and malformed"
    (should-not (valid-request? (parse-request "foobar")))
    (should-not (valid-request? (parse-request "GET http://google.com\r\n\r\n")))
    )
  )

