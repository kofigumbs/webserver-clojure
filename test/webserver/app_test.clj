(ns webserver.app-test
  (:require [speclj.core :refer :all]
            [webserver.app :as app]
            [webserver.response :as response]
            [webserver.mock-socket :as socket]))

(describe "400 response"
  (with socket (socket/make ""))
  (it "writes straight to socket"
    (should=
      (response/make 400)
      (do
        (app/relay @socket)
        (str (.getOutputStream @socket))))))

