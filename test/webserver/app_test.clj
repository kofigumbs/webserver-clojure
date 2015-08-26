(ns webserver.app-test
  (:require [speclj.core :refer :all]
            [webserver.app :as app]
            [webserver.response :as response]
            [webserver.headers :as headers]
            [webserver.mock-socket :as socket]))

(defn- stub-handler [socket _]
  (spit (.getOutputStream socket) "Hello world!"))

(describe "Relay"
  (it "writes 400 on malformed request"
    (should=
      (response/make 400)
      (let [socket (socket/make "")]
        (app/relay socket)
        (str (.getOutputStream socket)))))

  (it "uses handler for correctly formed request"
    (should=
      "Hello world!"
      (let [socket (socket/make "HEAD / HTTP/1.1\r\n\r\n")]
        (app/initialize
          {:initializer (fn [_]) :valid-request-handler stub-handler} [])
        (app/relay socket)
        (str (.getOutputStream socket))))))

(describe "Initialize (with no app hooked up)"
  (it "doesn't fail"
    (should= 2 (app/initialize {:initializer inc} 1))))

