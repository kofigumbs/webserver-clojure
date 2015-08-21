(ns cob-app.options-test
  (:require [speclj.core :refer :all]
            [cob-app.options :refer :all]
            [cob-app.core :refer [initialize handle]]
            [webserver.mock-socket]))

(describe "OPTIONS request"
  (with socket (webserver.mock-socket/make ""))

  (it "shows all valid options"
    (should= (str
               "HTTP/1.1 200 OK\r\n"
               "Allow: GET,HEAD,POST,OPTIONS,PUT\r\n\r\n")
             (do
               (handle
                  {:method "OPTIONS" :uri "/" :version "HTTP/1.1"} @socket)
                (str (.getOutputStream @socket))))
    ))

