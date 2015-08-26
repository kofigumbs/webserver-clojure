(ns webserver.core
  (:gen-class)
  (:require [webserver.app :as app]
            [cob-app.core :as backing-app]
            [cob-app.get]
            [cob-app.upload]
            [cob-app.delete]
            [cob-app.options]))

(def DEFAULT_PORT 5000)

(defn extract-port [args]
  (try
    (Integer. ^String (second (drop-while #(not= "-p" %) args)))
    (catch Exception _ DEFAULT_PORT)))

(defn process-request [^java.net.ServerSocket server
                       ^java.util.concurrent.ExecutorService pool
                       valid-request-handler]
  (let [socket (.accept server)]
    (.submit pool ^Runnable #(app/relay valid-request-handler socket))))

(defn -main [& args]
  (let [server (java.net.ServerSocket. (extract-port args))
        pool (java.util.concurrent.Executors/newSingleThreadExecutor)
        {:keys [initializer valid-request-handler]} backing-app/responder]
    (initializer args)
    (println "Serving HTTP...")
    (while (not (.isClosed server))
      (process-request server pool valid-request-handler))))

