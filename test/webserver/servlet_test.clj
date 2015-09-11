(ns webserver.servlet-test
  (:require [speclj.core :refer :all]
            [webserver.servlet :as servlet]
            [webserver.mock-socket :as socket]
            [clojure.java.io :as io]))

(def stub-handler-response "Hello world!\r\n")
(defn stub-handler [socket _]
  (io/copy stub-handler-response (.getOutputStream socket)) true)

(describe "Command line argument"
  (it "assigns correct port"
    (should= 80 (servlet/get-port ["-p" "80"]))
    (should= 8080 (servlet/get-port ["-p" "8080"]))
    (should= servlet/default-port (servlet/get-port ["-p" "asdf"]))
    (should= servlet/default-port (servlet/get-port []))))

(describe "Request processing"
  (with socket (socket/make "HEAD / HTTP/1.1\r\n\r\n"))
  (with stub-server (proxy [java.net.ServerSocket] []
               (accept [] @socket)))
  (with stub-pool (proxy [java.util.concurrent.ExecutorService] []
               (submit [^Runnable runnable] (.run runnable))))

  (it "accepts from ServerSocket and submits to ExecutorService"
    (should= stub-handler-response
             (do (servlet/process-request
                   @stub-server @stub-pool stub-handler)
                 (str (.getOutputStream @socket))))))
