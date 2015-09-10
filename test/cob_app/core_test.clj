(ns cob-app.core-test
  (:require [speclj.core :refer :all]
            [cob-app.core :as core]
            [cob-app.mock-socket :as socket]
            [webserver.response :as response]))

(describe "Default response"
  (it "501s on nonsense request"
    (should=
      (response/make 501)
      (socket/connect
        {:method "FOOBAR" :uri "/" :version "HTTP/1.1"}))))

(describe "Log"
  (it "should update with every request"
    (socket/connect {:method "FOOBAR" :uri "/asdf" :version "HTTP/1.1"})
    (should-contain "FOOBAR /asdf HTTP/1.1\r\n" (core/get-log))))
