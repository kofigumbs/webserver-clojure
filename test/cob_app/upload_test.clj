(ns cob-app.upload-test
  (:require [speclj.core :refer :all]
            [cob-app.upload]
            [cob-app.mock-socket :as socket]
            [webserver.response :as response]
            [clojure.java.io :as io]))



(describe "POST"
  (it "405s on /text-file.txt"
     (should=
       (response/make 405)
       (socket/connect
         {:method "POST" :uri "/text-file.txt" :version "HTTP/1.1"}))))

(describe "PUT"
  (it "405s on /file1"
     (should=
       (response/make 405)
       (socket/connect
         {:method "PUT" :uri "/file1" :version "HTTP/1.1"}))))
