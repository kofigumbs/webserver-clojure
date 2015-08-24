(ns cob-app.delete-test
  (:require [speclj.core :refer :all]
            [cob-app.delete]
            [cob-app.core :as core]
            [webserver.mock-socket :as socket]
            [clojure.java.io :as io]))

(describe "DELETE requests"
  (before-all
    (.mkdir (io/file "./tmp"))
    (spit "./tmp/file" "foobar"))

  (after-all
    (.delete (io/file "./tmp")))

  (it "deletes mock file"
    (should= "HTTP/1.1 200 OK\r\n"
             (socket/connect
                core/handle
                {:method "DELETE" :uri "/file" :version "HTTP/1.1"}))
    (should-not (.exists (io/file "./tmp/file"))))


  (it "204s on non-existent file"
    (should= "HTTP/1.1 204 No Content\r\n"
             (socket/connect
               core/handle
               {:method "DELETE" :uri "/none" :version "HTTP/1.1"}))))

