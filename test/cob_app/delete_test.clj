(ns cob-app.delete-test
  (:require [speclj.core :refer :all]
            [cob-app.delete :refer :all]
            [cob-app.core :refer [initialize handle]]
            [webserver.mock-socket]))

(describe "DELETE requests"
  (before-all
    (.mkdir (java.io.File. "./tmp"))
    (spit "./tmp/file" "foobar")
    (initialize ["-d" "./tmp"]))

  (after-all
    (.delete (java.io.File. "./tmp")))

  (with socket (webserver.mock-socket/make ""))

  (it "deletes mock file"
    (should= "HTTP/1.1 200 OK\r\n"
             (do
                (handle
                  {:method "DELETE" :uri "/file" :version "HTTP/1.1"} @socket)
                (str (.getOutputStream @socket))))
    (should-not (.exists (java.io.File. "./tmp/file"))))


  (it "204s on non-existent file"
    (should= "HTTP/1.1 204 No Content\r\n"
             (do
               (handle
                 {:method "DELETE" :uri "/none" :version "HTTP/1.1"} @socket)
               (str (.getOutputStream @socket))))))

