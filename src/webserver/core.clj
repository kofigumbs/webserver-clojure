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
    (Integer. (second (drop-while #(not= "-p" %) args)))
    (catch Exception _ DEFAULT_PORT)))

(defn -main [& args]
  (let [server (java.net.ServerSocket. (extract-port args))
        pool (java.util.concurrent.Executors/newSingleThreadExecutor)
        _ (app/initialize backing-app/protocol args)
        _ (println "Serving HTTP...")]
    (while true (let [socket (.accept server)]
                  (.submit pool (cast Runnable #(app/relay socket)))))))

