(ns webserver.get-test
  (:require [speclj.core :refer :all]
            [webserver.app]
            [webserver.get]
            [webserver.response :as response]
            [webserver.mock-socket :as socket]
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
    (io/delete-file "./tmp/file")
    (io/delete-file "./tmp/image.gif")
    (io/delete-file "./tmp"))

  (it "gets mock file"
    (should=
      (str
        (response/make 200 {:Content-Type "application/octet-stream"})
        "foobar")
      (socket/connect {:method "GET" :uri "/file" :version "HTTP/1.1"})))

  (it "404s on non-existent file"
    (should=
      (response/make 404)
      (socket/connect {:method "GET" :uri "/none" :version "HTTP/1.1"})))

  (it "404s on non-existent image"
    (should=
      (response/make 404)
      (socket/connect {:method "GET" :uri "/none.gif" :version "HTTP/1.1"})))

  (it "responds with image file and headers"
    (should=
      (str
        (response/make 200 {:Content-Type "image/gif"})
        (slurp "./tmp/image.gif"))
      (socket/connect {:method "GET" :uri "/image.gif" :version "HTTP/1.1"}))))

(describe "Partial content requests"
  (before-all
    (.mkdir (io/file "./tmp"))
    (spit "./tmp/file" "foobar"))

  (after-all
    (io/delete-file "./tmp/file")
    (io/delete-file "./tmp"))

  (for [[range-field contents]
        [["bytes=0-2" "foo"]
         ["bytes=4-5" "ar"]
         ["bytes=1-" "oobar"]
         ["bytes=-1" "r"]]]
    (it (str "reads " range-field " from file")
     (should=
       (str
         (response/make 206 {:Content-Type "application/octet-stream"})
         contents)
       (socket/connect
         {:method "GET" :uri "/file" :version "HTTP/1.1"
          :Range range-field})))))
