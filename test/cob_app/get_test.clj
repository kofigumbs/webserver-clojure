(ns cob-app.get-test
  (:require [speclj.core :refer :all]
            [cob-app.get]
            [cob-app.core :as core]
            [webserver.mock-socket :as socket]
            [webserver.response :as response]
            [clojure.java.io :as io]
            [clojure.data.codec.base64 :as b64]))

(describe "GET requests"
  (before-all
    (.mkdir (io/file "./tmp"))
    (spit "./tmp/file" "foobar")
    (spit "./tmp/base64_image"
          "R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7")
    (b64/encoding-transfer
      (io/input-stream "./tmp/base64_image")
      (io/output-stream "./tmp/image.gif"))
    (.delete (io/file "./tmp/base64_image")))

  (after-all
    (.delete (io/file "./tmp/file"))
    (.delete (io/file "./tmp/image.gif"))
    (.delete (io/file "./tmp")))

  (it "gets mock file"
    (should=
      (str
        (response/make
          200
          {:Content-Type "application/octet-stream"})
        "foobar")
      (socket/connect
        {:method "GET" :uri "/file" :version "HTTP/1.1"})))

  (it "gets mock folder"
    (should=
      (str
        (response/make
          200
          {:Content-Type "text/html"})
        "<!DOCTYPE html><html>"
        "<body>"
        "<a href=\"/file\">file</a>"
        "<a href=\"/image.gif\">image.gif</a>"
        "</body>"
        "</html>")
      (socket/connect
        {:method "GET" :uri "/" :version "HTTP/1.1"})))

  (it "404s on non-existent file"
    (should= (response/make 404)
             (socket/connect
               {:method "GET" :uri "/none" :version "HTTP/1.1"})))

  (it "404s on non-existent image"
    (should= (response/make 404)
             (socket/connect
               {:method "GET" :uri "/none.gif" :version "HTTP/1.1"})))

  (it "responds with image file and headers"
    (should=
      (str
        (response/make 200 {:Content-Type "image/gif"})
        (slurp "./tmp/image.gif"))
      (socket/connect
        {:method "GET" :uri "/image.gif" :version "HTTP/1.1"}))))

(describe "Redirect url"
  (it "redirects with 302s to root"
    (should=
      (response/make 302 {:Location "http://localhost:5000/"})
      (socket/connect
        {:method "GET"
         :uri "/redirect"
         :version "HTTP/1.1"
         :Host "localhost:5000"}))))

(describe "Parameter url"
  (it "responds with single decoded parameters"
    (should=
      (str (response/make 200) "variable_1 = <,")
      (socket/connect
        {:method "GET"
         :uri "/parameters"
         :parameters "variable_1=%3C%2C"
         :version "HTTP/1.1"})))

  (it "responds with multiple decoded parameters"
    (should=
      (str (response/make 200) "x = <,\r\ny = *?\r\nz = hello")
      (socket/connect
        {:method "GET"
         :uri "/parameters"
         :parameters "x=%3C%2C&y=%2A%3F&z=hello"
         :version "HTTP/1.1"}))))

(describe "Logs url"
  (before
    (reset! core/LOG []))

  (it "should reject without authorization"
    (should=
      (str (response/make 401) "Authentication required")
      (socket/connect
        core/handle
        {:method "GET"
         :uri "/logs"
         :version "HTTP/1.1"})))

  (it "should contain the logs otherwise"
    (should=
      (str (response/make 200) "GET /logs HTTP/1.1\r\n")
      (socket/connect
        core/handle
        {:method "GET"
         :uri "/logs"
         :version "HTTP/1.1"
         :Authorization cob-app.get/AUTHORIZATION}))))

