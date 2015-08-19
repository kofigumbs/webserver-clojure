(ns cob-app.put-test
  (:require [speclj.core :refer :all]
            [cob-app.put :refer :all]
            [cob-app.core :refer [initialize handle]]
            [webserver.mock-socket]))

(describe "PUT requests"
  (before-all
    (.mkdir (java.io.File. "./tmp"))
    (initialize ["-d" "./tmp"]))

  (after-all
    (clojure.java.io/delete-file "./tmp/foo.txt")
    (clojure.java.io/delete-file "./tmp"))

  (with socket (webserver.mock-socket/make
                 "This is a testing content for the text file foo.txt"))

  (describe "PUT requests"
    (it "stores basic text file"
      (should=
        "HTTP/1.1 200 OK\r\n\r\n"
        (do
          (handle {:method "PUT"
                   :uri "/foo.txt"
                   :version "HTTP/1.1"
                   :Content-Length "51"
                   :Content-Type "text/plain"} @socket)
          (str (.getOutputStream @socket))))
      (should (.exists (java.io.File. "./tmp/foo.txt")))
      )
    )
  )
