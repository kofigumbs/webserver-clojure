(ns webserver.dispatcher-test
  (:require [speclj.core :refer :all]
            [webserver.dispatcher :refer :all]
            [webserver.mock-socket]))

(describe "Set public dir"
  (it "properly sets public string"
    (set-dir "")
    (should (= @DIR "/"))
    (set-dir "tmp")
    (should (= @DIR "tmp/"))
    (set-dir "./")
    (should (= @DIR "./"))
    )
  )

(describe "Get plain text response"
  (before
    (.mkdir (java.io.File. "./tmp"))
    (spit "./tmp/file" "foobar")
    (set-dir "./tmp"))
  (after
    (.delete (java.io.File. "./tmp/file"))
    (.delete (java.io.File. "./tmp")))
  (with socket (webserver.mock-socket/make "GET /file HTTP/1.1\r\n\r\n"))
  (it "gets mock file"
    (should= (str
                "HTTP/1.1 200 OK\r\n"
                "Content-Type: text/plain; charset=utf-8\r\n"
                "Content-Length: 14\r\n\r\n"
                "foobar")
             (do
                (dispatch @socket)
                (str (.getOutputStream @socket))))
    )
  )

(describe "Nonsense request"
  (with socket (webserver.mock-socket/make "foobar"))
  (it "responds with 400"
    (should= "HTTP/1.1 400 Bad Request\r\n"
             (do
               (dispatch @socket)
               (str (.getOutputStream @socket))))
    )
  )
