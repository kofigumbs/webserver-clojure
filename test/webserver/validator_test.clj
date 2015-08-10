(ns webserver.validator-test
  (:require [clojure.test :refer :all]
            [webserver.validator :refer :all]))

(deftest validate-request-line
  (testing "Correctly formed request lines"
    (def example1 "GET http://www.example.com HTTP/1.1")
    (def example2 "POST http://8thlight.com HTTP/1.0")
    (def table1 (parse-request-line example1))
    (def table2 (parse-request-line example2))

    (is (= (:method table1) "GET"))
    (is (= (:uri table1) "http://www.example.com"))
    (is (= (:version table1) "HTTP/1.1"))
    (is (= (:method table2) "POST"))
    (is (= (:uri table2) "http://8thlight.com"))
    (is (= (:version table2) "HTTP/1.0"))
    (is (valid? (parse-request-line example1)))
    )

  (testing "Malformed request line"
    (is (not (valid? (parse-request-line "GET http://www.example.com"))))
    (is (not (valid? (parse-request-line
                 "GET http://www.example.com foo23"))))
    (is (not (valid? (parse-request-line
                 "get http://www.example.com HTTP/1.1"))))
    (is (not (valid? (parse-request-line
                 "GET http://www.example.com HTTP/1.1 asdf"))))
    (is (not (valid? (parse-request-line
                 "hello GET http://www.example.com HTTP/1.1"))))
    )

  )

(deftest validate-header-fields
  (def headers ["User-Agent: curl/7.16.3 libcurl/7.16.3"
                "Host: www.example.com"
                "Accept-Language: en, mi"
                "Date: Mon, 10 Aug 2015 12:32:56 GMT"
                "Content-Type: text/plain"
                "Content-MD5: Q2hlY2sgSW50ZWdyaXR5IQ=="
                ])
  (def example (apply str (interpose "\r\n" headers)))
  (def table (parse-header-fields example))

  (testing "Correctly formed header fields"
    (is (valid? table))
    (is (= (:User-Agent table) "curl/7.16.3 libcurl/7.16.3"))
    (is (= (:Host table) "www.example.com"))
    (is (= (:Accept-Language table) "en, mi"))
    (is (= (:Date table) "Mon, 10 Aug 2015 12:32:56 GMT"))
    (is (= (:Content-Type table) "text/plain"))
    (is (= (:Content-MD5 table) "Q2hlY2sgSW50ZWdyaXR5IQ=="))
    )

  (testing "Malformed header fields"
    (is (not (valid? (parse-header-fields
                       (str example "\r\n foo :bar")))))
    (is (not (valid? (parse-header-fields
                       "Hello World: cannot have space in name"))))
    )
  )

