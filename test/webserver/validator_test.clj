(ns webserver.validator-test
  (:require [clojure.test :refer :all]
            [webserver.validator :refer :all]))

(deftest validate-request-line
  (testing "Correctly formed request lines"
    (def request-line1 "GET http://www.example.com HTTP/1.1")
    (def request-line2 "POST http://8thlight.com HTTP/1.0")
    (def parsed-request-line1 (parse-request-line request-line1))
    (def parsed-request-line2 (parse-request-line request-line2))

    (is (= (:method parsed-request-line1) "GET"))
    (is (= (:uri parsed-request-line1) "http://www.example.com"))
    (is (= (:version parsed-request-line1) "HTTP/1.1"))
    (is (= (:method parsed-request-line2) "POST"))
    (is (= (:uri parsed-request-line2) "http://8thlight.com"))
    (is (= (:version parsed-request-line2) "HTTP/1.0"))
    (is (valid-request-line? (parse-request-line request-line1)))
    )

  (testing "Malformed request line"
    (is (not (valid-request-line?
               (parse-request-line "GET http://www.example.com"))))
    (is (not (valid-request-line?
               (parse-request-line "GET http://www.example.com foo23"))))
    (is (not (valid-request-line?
               (parse-request-line "get http://www.example.com HTTP/1.1"))))
    (is (not (valid-request-line?
               (parse-request-line
                 "GET http://www.example.com HTTP/1.1 asdf"))))
    (is (not (valid-request-line?
               (parse-request-line
                 "hello GET http://www.example.com HTTP/1.1"))))
    )

  )

(deftest validate-header
  (def header (str "User-Agent: curl/7.16.3 libcurl/7.16.3\r\n"
                   "Host: www.example.com\r\n"
                   "Accept-Language: en, mi\r\n"
                   "Date: Mon, 10 Aug 2015 12:32:56 GMT\r\n"
                   "Content-Type: text/plain\r\n"
                   "Content-MD5: Q2hlY2sgSW50ZWdyaXR5IQ=="))
  (def parsed-header (parse-header header))

  (testing "Correctly formed header fields"
    (is (valid-header? parsed-header))
    (is (= (:User-Agent parsed-header) "curl/7.16.3 libcurl/7.16.3"))
    (is (= (:Host parsed-header) "www.example.com"))
    (is (= (:Accept-Language parsed-header) "en, mi"))
    (is (= (:Date parsed-header) "Mon, 10 Aug 2015 12:32:56 GMT"))
    (is (= (:Content-Type parsed-header) "text/plain"))
    (is (= (:Content-MD5 parsed-header) "Q2hlY2sgSW50ZWdyaXR5IQ=="))
    )

  (testing "Malformed header fields"
    (is (not (valid-header? (parse-header
                       (str header "\r\n foo :bar")))))
    (is (not (valid-header? (parse-header
                       "Hello World: cannot have space in name"))))
    )
  )

(deftest validate-entire-request
  (testing "Long properly formed request"
    (def request (str
                   "POST /path/script.cgi HTTP/1.0\r\n"
                   "From: frog@jmarshall.com\r\n"
                   "User-Agent: HTTPTool/1.0\r\n"
                   "Content-Type: application/x-www-form-urlencoded\r\n"
                   "Content-Length: 32\r\n"
                   "\r\n"
                   "home=Cosby&favorite+flavor=flies"
                   ))
    (def parsed-request (parse-request request))

    (is (valid-request? parsed-request))
    (is (= (:method (:request-line parsed-request)) "POST"))
    (is (= (:From (:header parsed-request)) "frog@jmarshall.com"))
    (is (= (:body parsed-request) "home=Cosby&favorite+flavor=flies"))
    )

  (testing "Short properly formed request"
    (is (valid-request? (parse-request "GET http://google.com HTTP/1.1\r\n\r\n")))
    )

  (testing "Malformed request"
    (is (not (valid-request? (parse-request "foobar"))))
    (is (not (valid-request? (parse-request "GET http://google.com\r\n\r\n"))))
    )
  )

