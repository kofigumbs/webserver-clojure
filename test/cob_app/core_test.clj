(ns cob-app.core-test
  (:require [speclj.core :refer :all]
            [cob-app.core :refer :all]
            [webserver.mock-socket]
            [clojure.data.codec.base64]))

(describe "Set directory dir"
  (it "properly sets public string"
    (initialize [])
    (should= @DIR DEFAULT_DIR)
    (initialize ["-d" "tmp"])
    (should= @DIR "tmp/")
    (initialize ["-d" "dir/"])
    (should= @DIR "dir/")))

(describe "GET requests"
  (before (initialize ["-d" "./tmp"]))

  (before-all
    (.mkdir (java.io.File. "./tmp"))
    (spit "./tmp/file" "foobar")
    (spit "./tmp/base64_image"
          "R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7")
    (clojure.data.codec.base64/encoding-transfer
      (clojure.java.io/input-stream "./tmp/base64_image")
      (clojure.java.io/output-stream "./tmp/image.gif"))
    (.delete (java.io.File. "./tmp/base64_image")))

  (after-all
    (.delete (java.io.File. "./tmp/file"))
    (.delete (java.io.File. "./tmp/image.gif"))
    (.delete (java.io.File. "./tmp")))

  (with socket (webserver.mock-socket/make ""))

  (it "gets mock file"
    (should= (str
                "HTTP/1.1 200 OK\r\n"
                "Content-Type: application/octet-stream\r\n\r\n"
                "foobar")
             (do
                (handle
                  {:method "GET" :uri "/file" :version "HTTP/1.1"} @socket)
                (str (.getOutputStream @socket)))))

  (it "gets mock folder"
    (should= (str
                "HTTP/1.1 200 OK\r\n"
                "Content-Type: text/html\r\n\r\n"
                "<!DOCTYPE html><html>"
                "<body>"
                "<a href=\"/file\">file</a>"
                "<a href=\"/image.gif\">image.gif</a>"
                "</body>"
                "</html>")
             (do
               (handle
                 {:method "GET" :uri "/" :version "HTTP/1.1"} @socket)
               (str (.getOutputStream @socket)))))

  (it "404s on non-existent file"
    (should= "HTTP/1.1 404 Not Found\r\n"
             (do
               (handle
                 {:method "GET" :uri "/none" :version "HTTP/1.1"} @socket)
               (str (.getOutputStream @socket)))))

  (it "404s on non-existent image"
    (should= "HTTP/1.1 404 Not Found\r\n"
             (do
               (handle
                 {:method "GET" :uri "/none.gif" :version "HTTP/1.1"} @socket)
               (str (.getOutputStream @socket))))
    )

  (it "responds with image file and headers"
    (should (do
              (handle
                {:method "GET" :uri "/image.gif" :version "HTTP/1.1"} @socket)
              (and
                (.startsWith
                  (str (.getOutputStream @socket))
                  "HTTP/1.1 200 OK\r\nContent-Type: image/gif\r\n\r\n")
                (.endsWith
                  (str (.getOutputStream @socket))
                  (slurp "./tmp/image.gif")))))))

