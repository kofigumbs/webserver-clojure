(ns cob-app.core-test
  (:require [speclj.core :refer :all]
            [cob-app.core :as core]
            [webserver.core :as app]
            [webserver.response :as response]
            [cob-app.mock-socket :as socket]))

(describe "Set directory dir"
  (with initializer (:initializer core/responder))
  (it "properly sets public string"
    (@initializer [])
    (should= @core/DIR core/DEFAULT_DIR)
    (@initializer ["-d" "tmp"])
    (should= @core/DIR "tmp/")
    (@initializer ["-d" "dir/"])
    (should= @core/DIR "dir/")
    (@initializer [])))

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
          {:method "FOOBAR" :uri "/" :version "HTTP/1.1"})
        @core/LOG))))

