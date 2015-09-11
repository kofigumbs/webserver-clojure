(ns webserver.servlet
  (:require [webserver.app :as app]
            [webserver.util :as util])
  (:import [java.net ServerSocket]
           [java.util.concurrent ExecutorService Executors]))

(def default-port 5000)
(defn get-port [args] (util/extract-int args "-p" default-port))

(defn process-request [^ServerSocket server ^ExecutorService pool handler]
  (let [socket (.accept server)]
    (.submit pool ^Runnable #(app/relay handler socket))))

(defn start [args handler]
  (let [server (ServerSocket. (get-port args))
        pool (Executors/newSingleThreadExecutor)]
    (app/initialize args)
    (println "Serving HTTP...")
    (while (not (.isClosed server))
      (process-request server pool handler))))
