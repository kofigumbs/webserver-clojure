(ns cob-app.delete-test
  (:require [speclj.core :refer :all]
            [cob-app.delete]
            [cob-app.mock-socket :as socket]
            [webserver.response :as response]
            [clojure.java.io :as io]))

(describe "DELETE requests"
  (before-all
    (.mkdir (io/file "./tmp"))
    (spit "./tmp/file" "foobar"))

  (after-all
    (.delete (io/file "./tmp")))

  (it "deletes mock file"
    (should= (response/make 200)
             (socket/connect
                {:method "DELETE" :uri "/file" :version "HTTP/1.1"}))
    (should-not (.exists (io/file "./tmp/file"))))


  (it "204s on non-existent file"
    (should= (response/make 204)
             (socket/connect
               {:method "DELETE" :uri "/none" :version "HTTP/1.1"}))))

