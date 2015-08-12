(ns webserver.dispatcher-test
  (:require [speclj.core :refer :all]
            [webserver.dispatcher :refer :all]
            [webserver.mock-socket]))

(describe "Set public dir"
  (it "properly sets public string"
    (set-dir "")
    (should (= @DIR ""))
    (set-dir "/tmp")
    (should (= @DIR "/tmp"))
    )
  )

(describe "Get plain text response"
  (with request "GET /file1 HTTP/1.1\r\n\r\n")
  (with socket (webserver.mock-socket/make @request))
  (it "gets file 1"
    (should= (str
                "HTTP/1.1 200 OK\r\n"
                "Content-Type: text/plain; charset=utf-8\r\n"
                "Content-Length: 14\r\n\r\n"
                "file1 contents\r\n")
             (do
                (dispatch @socket)
                (str (.getOutputStream @socket))))
    )
  )

