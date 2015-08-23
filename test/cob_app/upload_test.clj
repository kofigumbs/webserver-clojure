(ns cob-app.upload-test
  (:require [speclj.core :refer :all]
            [cob-app.upload :refer :all]
            [cob-app.core :refer [handle]]
            [webserver.mock-socket]))

(describe "Upload request"
  (before-all
    (.mkdir (java.io.File. "./tmp")))

  (after
    (clojure.java.io/delete-file "./tmp/foo.bar"))

  (after-all
    (clojure.java.io/delete-file "./tmp"))

  (with-all body "This is a testing content for the text file foo.bar")

  (for [method ["PUT" "POST"]]
    (it (format "stores basic text file (%s)" method)
      (should=
        "HTTP/1.1 200 OK\r\n\r\n"
        (webserver.mock-socket/connect
          handle
          {:method method
           :uri "/foo.bar"
           :version "HTTP/1.1"
           :Content-Length "51"
           :Content-Type "text/plain"}
          @body))
      (should (.exists (java.io.File. "./tmp/foo.bar")))))

  (for [method ["PUT" "POST"]]
    (it (format "doesn't fail on empty Content-Length and body (%s)" method)
      (should=
        "HTTP/1.1 200 OK\r\n\r\n"
        (webserver.mock-socket/connect
          handle
          {:method method
           :uri "/foo.bar"
           :version "HTTP/1.1"}
          @body))
      (should (.exists (java.io.File. "./tmp/foo.bar"))))))

(describe "POST"
  (it "405s on /text-file.txt"
     (should=
       "HTTP/1.1 405 Method Not Allowed\r\n\r\n"
       (webserver.mock-socket/connect
         handle
         {:method "POST"
          :uri "/text-file.txt"
          :version "HTTP/1.1"}))))

(describe "PUT"
  (it "405s on /file1"
     (should=
       "HTTP/1.1 405 Method Not Allowed\r\n\r\n"
       (webserver.mock-socket/connect
         handle
         {:method "PUT"
          :uri "/file1"
          :version "HTTP/1.1"}))))

