(ns cob-app.get-test
  (:require [speclj.core :refer :all]
            [cob-app.get]
            [cob-app.mock-socket :as socket]
            [webserver.response :as response]
            [clojure.java.io :as io]))

(describe "Root requests"
  (before (.mkdir (io/file "./tmp")) (spit "./tmp/file" "foobar"))
  (after  (doseq [file ["./tmp/file" "./tmp"]] (io/delete-file file)))
  (it "gets mock folder"
    (should=
      (str
        (response/make 200 {:Content-Type "text/html"})
        "<!DOCTYPE html><html>"
        "<body>"
        "<a href=\"/file\">file</a>"
        "</body>"
        "</html>")
      (socket/connect {:method "GET" :uri "/" :version "HTTP/1.1"}))))

(describe "Redirect url"
  (it "redirects with 302s to root"
    (should=
      (response/make 302 {:Location "http://localhost:5000/"})
      (socket/connect
        {:method "GET" :uri "/redirect" :version "HTTP/1.1"
         :Host "localhost:5000"}))))

(describe "Parameter url"
  (it "responds with single decoded parameters"
    (should=
      (str (response/make 200) "variable_1 = <,")
      (socket/connect
        {:method "GET"
         :uri "/parameters"
         :parameters "variable_1=%3C%2C"
         :version "HTTP/1.1"})))

  (it "responds with multiple decoded parameters"
    (should=
      (str (response/make 200) "x = <,\r\ny = *?\r\nz = hello")
      (socket/connect
        {:method "GET" :uri "/parameters" :version "HTTP/1.1"
         :parameters "x=%3C%2C&y=%2A%3F&z=hello"}))))

(describe "Logs url"
  (it "should reject without authorization"
    (should=
      (str (response/make 401) "Authentication required")
      (socket/connect
        {:method "GET" :uri "/logs" :version "HTTP/1.1"})))

  (it "should contain the logs otherwise"
    (socket/connect {:method "GET" :uri "/unique-url" :version "HTTP/1.1"})
    (let [r (socket/connect {:method "GET" :uri "/logs" :version "HTTP/1.1"
                             :Authorization cob-app.get/AUTHORIZATION})]
      (should-contain (response/make 200) r)
      (should-contain "GET /unique-url HTTP/1.1" r))))
