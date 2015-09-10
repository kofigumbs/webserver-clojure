(ns webserver.upload-test
  (:require [speclj.core :refer :all]
            [webserver.upload]
            [webserver.response :as response]
            [webserver.mock-socket :as socket]
            [clojure.java.io :as io]))

(describe "Upload request"
  (before-all (.mkdir (io/file "./tmp")))
  (after (io/delete-file "./tmp/foo.bar"))
  (after-all (io/delete-file "./tmp"))
  (with-all body "This is a testing content for the text file foo.bar")

  (for [method ["PUT" "POST"]]
    (it (format "stores basic text file (%s)" method)
      (should=
        (response/make 200)
        (socket/connect
          {:method method :uri "/foo.bar" :version "HTTP/1.1"
           :Content-Length "51"
           :Content-Type "text/plain"}
          @body))
      (should (.exists (io/file "./tmp/foo.bar")))))

  (for [method ["PUT" "POST"]]
    (it (format "doesn't fail on empty Content-Length and body (%s)" method)
      (should=
        (response/make 200)
        (socket/connect
          {:method method :uri "/foo.bar" :version "HTTP/1.1"}
          @body))
      (should (.exists (java.io.File. "./tmp/foo.bar"))))))

(describe "PATCH request"
  (before-all (.mkdir (io/file "./tmp")) (spit "./tmp/foo.bar" "foobar"))
  (after-all (io/delete-file "./tmp/foo.bar") (io/delete-file "./tmp"))

  (it "409s without ETag"
    (should=
      (response/make 409)
      (socket/connect
        {:method "PATCH" :uri "/foo.bar" :version "HTTP/1.1"})))

  (it "412s with wrong ETag"
    (should=
      (response/make 412)
      (socket/connect
        {:method "PATCH" :uri "/foo.bar" :version "HTTP/1.1"
         :If-Match "123456789abcdefghijklm"})))

  (it "204s and updates with proper ETag"
    (should=
      (response/make 204)
      (socket/connect
        {:method "PATCH" :uri "/foo.bar" :version "HTTP/1.1"
         :If-Match "8843d7f92416211de9ebb963ff4ce28125932878"}
        "barfoo"))
    (should= (slurp "./tmp/foo.bar") "barfoo")))
