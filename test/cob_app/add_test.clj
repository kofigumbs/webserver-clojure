(ns cob-app.add-test
  (:require [speclj.core :refer :all]
            [cob-app.add :refer :all]
            [cob-app.core :refer [initialize handle]]
            [webserver.mock-socket]))

(describe "Upload requests"

    (before-all
      (.mkdir (java.io.File. "./tmp"))
      (initialize ["-d" "./tmp"]))

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

