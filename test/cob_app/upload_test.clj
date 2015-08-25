(ns cob-app.upload-test
  (:require [speclj.core :refer :all]
            [cob-app.upload]
            [cob-app.core :as core]
            [webserver.mock-socket :as socket]
            [webserver.response :as response]
            [clojure.java.io :as io]))

(describe "Upload request"
  (before-all
    (.mkdir (io/file "./tmp")))

  (after
    (io/delete-file "./tmp/foo.bar"))

  (after-all
    (io/delete-file "./tmp"))

  (with-all body "This is a testing content for the text file foo.bar")

  (for [method ["PUT" "POST"]]
    (it (format "stores basic text file (%s)" method)
      (should=
        (response/make 200)
        (socket/connect
          core/handle
          {:method method
           :uri "/foo.bar"
           :version "HTTP/1.1"
           :Content-Length "51"
           :Content-Type "text/plain"}
          @body))
      (should (.exists (io/file "./tmp/foo.bar")))))

  (for [method ["PUT" "POST"]]
    (it (format "doesn't fail on empty Content-Length and body (%s)" method)
      (should=
        (response/make 200)
        (socket/connect
          core/handle
          {:method method
           :uri "/foo.bar"
           :version "HTTP/1.1"}
          @body))
      (should (.exists (java.io.File. "./tmp/foo.bar"))))))

(describe "POST"
  (it "405s on /text-file.txt"
     (should=
       (response/make 405)
       (socket/connect
         core/handle
         {:method "POST"
          :uri "/text-file.txt"
          :version "HTTP/1.1"}))))

(describe "PUT"
  (it "405s on /file1"
     (should=
       (response/make 405)
       (socket/connect
         core/handle
         {:method "PUT"
          :uri "/file1"
          :version "HTTP/1.1"}))))

