(ns cob-app.get-test
  (:require [speclj.core :refer :all]
            [cob-app.get :refer :all]
            [cob-app.core :refer [initialize handle]]
            [webserver.mock-socket]
            [clojure.data.codec.base64]))

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
    (initialize ["-d" "./tmp"]))

  (after-all
    (.delete (java.io.File. "./tmp/file"))
    (.delete (java.io.File. "./tmp/image.gif"))
    (.delete (java.io.File. "./tmp")))

  (it "gets mock file"
    (should=
      (str
        "HTTP/1.1 200 OK\r\n"
        "Content-Type: application/octet-stream\r\n\r\n"
        "foobar")
      (webserver.mock-socket/connect
        handle
        {:method "GET" :uri "/file" :version "HTTP/1.1"})))

  (it "gets mock folder"
    (should=
      (str
        "HTTP/1.1 200 OK\r\n"
        "Content-Type: text/html\r\n\r\n"
        "<!DOCTYPE html><html>"
        "<body>"
        "<a href=\"/file\">file</a>"
        "<a href=\"/image.gif\">image.gif</a>"
        "</body>"
        "</html>")
      (webserver.mock-socket/connect
        handle
        {:method "GET" :uri "/" :version "HTTP/1.1"})))

  (it "404s on non-existent file"
    (should= "HTTP/1.1 404 Not Found\r\n"
             (webserver.mock-socket/connect
               handle
               {:method "GET" :uri "/none" :version "HTTP/1.1"})))

  (it "404s on non-existent image"
    (should= "HTTP/1.1 404 Not Found\r\n"
             (webserver.mock-socket/connect
               handle
               {:method "GET" :uri "/none.gif" :version "HTTP/1.1"})))

  (it "responds with image file and headers"
    (should=
      (str
        "HTTP/1.1 200 OK\r\n"
        "Content-Type: image/gif\r\n\r\n"
        (slurp "./tmp/image.gif"))
      (webserver.mock-socket/connect
        handle
        {:method "GET" :uri "/image.gif" :version "HTTP/1.1"}))))

(describe "Redirect url"
  (it "redirects with 302s to root"
    (should=
      (str
        "HTTP/1.1 302 Found\r\n"
        "Location: http://localhost:5000/\r\n\r\n")
      (webserver.mock-socket/connect
        handle
        {:method "GET"
         :uri "/redirect"
         :version "HTTP/1.1"
         :Host "localhost:5000"}))))

