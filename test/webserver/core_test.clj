(ns webserver.core-test
  (:require [speclj.core :refer :all]
            [webserver.core :as core]
            [webserver.mock-socket :as socket]
            [clojure.java.io :as io]))

(def stub-handler-response "Hello world!\r\n")
(defn stub-handler [socket _]
  (io/copy stub-handler-response (.getOutputStream socket)))

(describe "Command line argument"
  (it "assigns correct port"
    (should= 80 (core/extract-port ["-p" "80"]))
    (should= 8080 (core/extract-port ["-p" "8080"]))
    (should= core/DEFAULT_PORT (core/extract-port ["-p" "asdf"]))
    (should= core/DEFAULT_PORT (core/extract-port []))))

(describe "Request processing"
  (with socket (socket/make "HEAD / HTTP/1.1\r\n\r\n"))
  (with stub-server (proxy [java.net.ServerSocket] []
               (accept [] @socket)))
  (with stub-pool (proxy [java.util.concurrent.ExecutorService] []
               (submit [^Runnable runnable] (.run runnable))))

  (it "accepts from ServerSocket and submits to ExecutorService"
    (should= stub-handler-response
             (do (core/process-request
                   @stub-server @stub-pool stub-handler)
                 (str (.getOutputStream @socket))))))

