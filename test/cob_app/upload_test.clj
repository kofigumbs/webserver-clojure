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
<<<<<<< 4a9cd3b59d4e3bdfe5256fc8f6dc5feb4859ab13
          core/handle
          {:method method :uri "/foo.bar" :version "HTTP/1.1"}
=======
          {:method method
           :uri "/foo.bar"
           :version "HTTP/1.1"}
>>>>>>> Extracted socket dispatching from webserver.core
          @body))
      (should (.exists (java.io.File. "./tmp/foo.bar"))))))

(describe "POST"
  (it "405s on /text-file.txt"
     (should=
       (response/make 405)
       (socket/connect
<<<<<<< 4a9cd3b59d4e3bdfe5256fc8f6dc5feb4859ab13
         core/handle
         {:method "POST" :uri "/text-file.txt" :version "HTTP/1.1"}))))
=======
         {:method "POST"
          :uri "/text-file.txt"
          :version "HTTP/1.1"}))))
>>>>>>> Extracted socket dispatching from webserver.core

(describe "PUT"
  (it "405s on /file1"
     (should=
       (response/make 405)
       (socket/connect
<<<<<<< 4a9cd3b59d4e3bdfe5256fc8f6dc5feb4859ab13
         core/handle
         {:method "PUT" :uri "/file1" :version "HTTP/1.1"}))))

(describe "PATCH request"
  (before-all
    (.mkdir (io/file "./tmp"))
    (spit "./tmp/foo.bar" "foobar"))

  (it "409s without ETag"
    (should=
      (response/make 409)
      (socket/connect
        core/handle
        {:method "PATCH" :uri "/foo.bar" :version "HTTP/1.1"})))

  (it "412s with wrong ETag"
    (should=
      (response/make 412)
      (socket/connect
        core/handle
        {:method "PATCH"
         :uri "/foo.bar"
         :version "HTTP/1.1"
         :If-Match "123456789abcdefghijklm"})))

  (it "204s and updates with proper ETag"
    (should=
      (response/make 204)
      (socket/connect
        core/handle
        {:method "PATCH"
         :uri "/foo.bar"
         :version "HTTP/1.1"
         :If-Match "8843d7f92416211de9ebb963ff4ce28125932878"}
        "barfoo"))
    (should= (slurp "./tmp/foo.bar") "barfoo"))

  (after-all
    (io/delete-file "./tmp/foo.bar")
    (io/delete-file "./tmp")))
=======
         {:method "PUT"
          :uri "/file1"
          :version "HTTP/1.1"}))))
>>>>>>> Extracted socket dispatching from webserver.core

