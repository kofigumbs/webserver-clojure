(ns webserver.app-test
  (:require [speclj.core :refer :all]
            [webserver.app :as app]
            [webserver.response :as response]
            [webserver.headers :as headers]
            [webserver.mock-socket :as socket]))

(defn- stub-handler [socket _]
  (spit (.getOutputStream socket) "Hello world!") true)

(describe "Set directory dir"
  (it "properly sets public string"
    (app/initialize [])
    (should= (app/get-dir) app/default-dir)
    (app/initialize ["-d" "folder"])
    (should= (app/get-dir) "folder/")
    (app/initialize ["-d" "dir/"])
    (should= (app/get-dir) "dir/")
    (app/initialize [])))

(describe "Relay"
  (it "writes 400 on malformed request"
    (should=
      (response/make 400)
      (let [socket (socket/make "")]
        (app/relay stub-handler socket)
        (str (.getOutputStream socket)))))

  (it "uses handler for correctly formed request"
    (should=
      "Hello world!"
      (let [socket (socket/make "HEAD / HTTP/1.1\r\n\r\n")]
        (app/relay stub-handler socket)
        (str (.getOutputStream socket))))))
