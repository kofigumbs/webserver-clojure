(ns webserver.socket-test
  (:require [clojure.test :refer :all]
            [webserver.socket :refer :all]))

(defn mock-socket [input]
  (proxy [java.net.Socket]
    []
    (getInputStream [] (new java.io.ByteArrayInputStream (.getBytes input))))
  )

(deftest input-stream-mock
  (testing "Basic string"
    (is (= "foobar" (get-request (mock-socket "foobar"))))
    (is (= "barfoo" (get-request (mock-socket "barfoo"))))
    )

  (testing "Something that looks like a request"
    (def request "GET http://example.com HTTP/1.1\r\n\r\n")
    (is (= request (get-request (mock-socket request))))
    )
  )
