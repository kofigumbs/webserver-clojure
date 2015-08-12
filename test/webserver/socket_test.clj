(ns webserver.socket-test
  (:require [clojure.test :refer :all]
            [webserver.socket :refer :all]
            [webserver.mock-socket]))

(deftest input-stream-mock
  (testing "Basic string"
    (is (= "foobar" (get-request (webserver.mock-socket/make "foobar"))))
    (is (= "barfoo" (get-request (webserver.mock-socket/make "barfoo"))))
    )

  (testing "Something that looks like a request"
    (def request "GET http://example.com HTTP/1.1\r\n\r\n")
    (is (= request (get-request (webserver.mock-socket/make request))))
    )
  )

(deftest output-stream-mock
  (testing "Basic string"
    (def socket (webserver.mock-socket/make ""))
    (is (=
         "foobar"
         (do (respond socket "foobar")
             (.toString (.getOutputStream socket)))))
    )

  (testing "Somthing that looks like a response"
    (def request "GET http://example.com HTTP/1.1\r\n\r\n"))
    (def socket (webserver.mock-socket/make ""))
    (def response (str
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
    (is (=
         response
         (do (respond socket response)
             (str (.getOutputStream socket)))))
  )
