(ns webserver.validator-test
  (:require [clojure.test :refer :all]
            [webserver.validator :refer :all]))

(deftest parse-request-line
  (def example1 "GET http://www.example.com HTTP/1.1")
  (def example2 "POST http://8thlight.com HTTP/1.0")

  (testing "Correctly formed request lines"
    (is (let [table (parse example1)]
          (and
            (= (:method table) "GET")
            (= (:uri table) "http://www.example.com")
            (= (:version table) "HTTP/1.1")
            )))
    (is (let [table (parse example2)]
          (and
            (= (:method table) "POST")
            (= (:uri table) "http://8thlight.com")
            (= (:version table) "HTTP/1.0")
            )))
    )

  (testing "Malformed request line"
    (is (valid? (parse example1)))
    (is (not (valid? (parse "GET http://www.example.com"))))
    (is (not (valid? (parse "GET http://www.example.com foo23"))))
    (is (not (valid? (parse "get http://www.example.com HTTP/1.1"))))
    (is (not (valid? (parse "GET http://www.example.com HTTP/1.1 asdf"))))
    (is (not (valid? (parse "hello GET http://www.example.com HTTP/1.1"))))
    )
  )

