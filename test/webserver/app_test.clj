(ns webserver.app-test
  (:require [speclj.core :refer :all]
            [webserver.app :as app]
            [webserver.response :as response]
            [webserver.headers :as headers]
            [webserver.mock-socket :as socket]))

(describe "Relay (with no app hooked up)"
  (with socket (socket/make ""))
  (it "writes 400 response straight to socket"
    (should=
      (response/make 400)
      (do
        (app/relay @socket)
        (str (.getOutputStream @socket))))))

(describe "Initialize (with no app hooked up)"
  (it "doesn't fail"
    (should-not-throw (app/initialize nil))))

(describe "Handle (with no app hooked up)"
  (it "500s on valid request"
    (should=
      (response/make 500)
      (with-redefs [headers/valid? (fn[_] :default)]
        ;; this redef keeps require's from other namespaces from
        ;; interfering when running entire test suite
        (socket/connect {:method "GET" :uri "/" :version "HTTP/1.1"})))))

