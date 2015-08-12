(ns webserver.dispatcher-test
  (:require [clojure.test :refer :all]
            [webserver.dispatcher :refer :all]
            [webserver.mock-socket]))

(deftest set-public-dir
  (testing "Public string properly set"
    (set-dir "")
    (is (= @DIR ""))
    (set-dir "/tmp")
    (is (= @DIR "/tmp"))
    )
  )

(deftest get-plain-text-response
  (testing "Get file 1"
    (def request "GET /file1 HTTP/1.1\r\n\r\n")
    (def socket (webserver.mock-socket/make request))
    (is (= (str
             "HTTP/1.1 200 OK\r\n"
             "Content-Type: text/plain; charset=utf-8\r\n"
             "Content-Length: 14\r\n\r\n"
             "file1 contents\r\n")
           (do
             (dispatch socket)
             (str (.getOutputStream socket)))))
    )
  )
