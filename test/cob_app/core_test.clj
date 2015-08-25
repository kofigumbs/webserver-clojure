(ns cob-app.core-test
  (:require [speclj.core :refer :all]
            [cob-app.core :as core]
            [webserver.app :as app]
            [webserver.response :as response]
            [webserver.mock-socket :as socket]))

(describe "Set directory dir"
  (it "properly sets public string"
    (app/initialize [])
    (should= @core/DIR core/DEFAULT_DIR)
    (app/initialize ["-d" "tmp"])
    (should= @core/DIR "tmp/")
    (app/initialize ["-d" "dir/"])
    (should= @core/DIR "dir/")
    (app/initialize [])))

(describe "Default response"
  (it "501s on nonsense request"
    (should=
      (response/make 501)
      (socket/connect
        {:method "FOOBAR" :uri "/" :version "HTTP/1.1"}))))

(describe "Log"
  (before
    (reset! core/LOG []))

  (it "should update with every request"
    (should= [] @core/LOG)
    (should=
      ["FOOBAR / HTTP/1.1\r\n"]
      (do
        (socket/connect
          core/handle
          {:method "FOOBAR" :uri "/" :version "HTTP/1.1"})
        @core/LOG))))

