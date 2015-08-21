(ns cob-app.add-test
  (:require [speclj.core :refer :all]
            [cob-app.add :refer :all]
            [cob-app.core :refer [initialize handle]]
            [webserver.mock-socket]))

(describe "Both PUT and POST requests"
  (before-all
    (.mkdir (java.io.File. "./tmp"))
    (initialize ["-d" "./tmp"]))

  (after
    (clojure.java.io/delete-file "./tmp/foo.bar"))

  (after-all
    (clojure.java.io/delete-file "./tmp"))

  (with socket (webserver.mock-socket/make
                 "This is a testing content for the text file foo.bar"))

  (it "stores basic text file (PUT)"
    (should=
      "HTTP/1.1 200 OK\r\n\r\n"
      (do
        (handle {:method "PUT"
                 :uri "/foo.bar"
                 :version "HTTP/1.1"
                 :Content-Length "51"
                 :Content-Type "text/plain"} @socket)
        (str (.getOutputStream @socket))))
    (should (.exists (java.io.File. "./tmp/foo.bar")))
    )

 (it "stores basic text file (POST)"
    (should=
      "HTTP/1.1 200 OK\r\n\r\n"
      (do
        (handle {:method "POST"
                 :uri "/foo.bar"
                 :version "HTTP/1.1"
                 :Content-Length "51"
                 :Content-Type "text/plain"} @socket)
        (str (.getOutputStream @socket))))
    (should (.exists (java.io.File. "./tmp/foo.bar")))
    )

 (it "Doesn't fail on empty Content-Length and body (POST)"
    (should=
      "HTTP/1.1 200 OK\r\n\r\n"
      (do
        (handle {:method "POST"
                 :uri "/foo.bar"
                 :version "HTTP/1.1"} @socket)
        (str (.getOutputStream @socket))))
    (should (.exists (java.io.File. "./tmp/foo.bar")))
    )
  )

