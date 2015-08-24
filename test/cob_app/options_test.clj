(ns cob-app.options-test
  (:require [speclj.core :refer :all]
            [cob-app.options]
            [cob-app.core :as core]
            [webserver.mock-socket :as socket]
            [webserver.response :as response]))

(describe "OPTIONS request"
  (it "shows all valid options"
    (should=
      (response/make 200 {:Allow "GET,HEAD,POST,OPTIONS,PUT"})
      (socket/connect
        core/handle
        {:method "OPTIONS" :uri "/method_options" :version "HTTP/1.1"}))))

