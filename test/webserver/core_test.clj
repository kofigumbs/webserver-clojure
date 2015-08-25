(ns webserver.core-test
  (:require [speclj.core :refer :all]
            [webserver.core :as core]
            [webserver.response :as response]
            [webserver.mock-socket :as socket]))

(describe "Command line argument"
  (it "assigns correct port"
    (should= 80 (core/extract-port ["-p" "80"]))
    (should= 8080 (core/extract-port ["-p" "8080"]))
    (should= core/DEFAULT_PORT (core/extract-port ["-p" "asdf"]))
    (should= core/DEFAULT_PORT (core/extract-port []))))

(describe "400 response"
  (with socket (socket/make ""))
  (it "writes straight to socket"
    (should=
      (response/make 400)
      (do
        (core/relay @socket)
        (str (.getOutputStream @socket))))))

