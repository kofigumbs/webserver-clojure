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

(describe "Get existing files"
  (before
    (.mkdir (java.io.File. "./tmp"))
    (spit "./tmp/file" "foobar")
    (set-dir "./tmp"))
  (after
    (.delete (java.io.File. "./tmp/file"))
    (.delete (java.io.File. "./tmp")))
  (with socket1 (webserver.mock-socket/make "GET /file HTTP/1.1\r\n\r\n"))

  (it "gets mock file"
    (should= (str
                "HTTP/1.1 200 OK\r\n"
                "Content-Type: application/octet-stream\r\n\r\n"
                "foobar")
             (do
                (dispatch @socket1)
                (str (.getOutputStream @socket1))))
    )

  (with socket2 (webserver.mock-socket/make "GET / HTTP/1.1\r\n\r\n"))
  (it "gets mock folder"
    (should= (str
                "HTTP/1.1 200 OK\r\n"
                "Content-Type: text/html\r\n\r\n"
                "<!DOCTYPE html><html>"
                "<title>Directory listing</title>"
                "<body>"
                "<h2>Directory listing</h2>"
                "<hr>"
                "<ul>"
                "<li><a href=\"file\">file</a>"
                "</ul>"
                "<hr>"
                "</body>"
                "</html>")
             (do
               (dispatch @socket2)
               (str (.getOutputStream @socket2))))
    )

  (with socket3 (webserver.mock-socket/make "GET /none HTTP/1.1\r\n\r\n"))
  (it "404s on non-existent file"
    (should= "HTTP/1.1 404 Not Found\r\n"
             (do
               (dispatch @socket3)
               (str (.getOutputStream @socket3))))
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
