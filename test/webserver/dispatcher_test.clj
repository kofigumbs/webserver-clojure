(ns webserver.dispatcher-test
  (:require [speclj.core :refer :all]
            [webserver.dispatcher :refer :all]
            [webserver.mock-socket]
            [clojure.data.codec.base64]))

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

(describe "GET requests"
  (before-all
    (.mkdir (java.io.File. "./tmp"))
    (spit "./tmp/file" "foobar")
    (spit "./tmp/base64_image"
          "R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7")
    (clojure.data.codec.base64/encoding-transfer
      (clojure.java.io/input-stream "./tmp/base64_image")
      (clojure.java.io/output-stream "./tmp/image.gif"))
    (.delete (java.io.File. "./tmp/base64_image"))
    (set-dir "./tmp"))
  (after-all
    (.delete (java.io.File. "./tmp/file"))
    (.delete (java.io.File. "./tmp/image.gif"))
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
                "<li><a href=\"image.gif\">image.gif</a>"
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

  (with socket4 (webserver.mock-socket/make "GET /none.gif HTTP/1.1\r\n\r\n"))
  (it "404s on non-existent image"
    (should= "HTTP/1.1 404 Not Found\r\n"
             (do
               (dispatch @socket4)
               (str (.getOutputStream @socket4))))
    )

  (with socket5 (webserver.mock-socket/make "GET /image.gif HTTP/1.1\r\n\r\n"))
  (it "responds with image file and headers"
    (should
      (do
        (dispatch @socket5)
        (and (.startsWith (str (.getOutputStream @socket5))
                          (str
                            "HTTP/1.1 200 OK\r\n"
                            "Content-Type: image/gif\r\n\r\n")
                          )
             (.endsWith (str (.getOutputStream @socket5))
                        (slurp "./tmp/image.gif"))
             )))
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
