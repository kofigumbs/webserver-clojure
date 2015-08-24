(ns cob-app.options-test
  (:require [speclj.core :refer :all]
            [cob-app.options]
            [cob-app.core :as core]
            [webserver.mock-socket :as socket]))

(describe "OPTIONS request"
  (it "shows all valid options"
    (should=
      (str
        "HTTP/1.1 200 OK\r\n"
        "Allow: GET,HEAD,POST,OPTIONS,PUT\r\n\r\n")
      (socket/connect
        core/handle
        {:method "OPTIONS" :uri "/method_options" :version "HTTP/1.1"}))))

