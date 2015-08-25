(ns webserver.core
  (:require [webserver.app :as app])
  (:require [cob-app.core])
  (:require [cob-app.get])
  (:require [cob-app.upload])
  (:require [cob-app.delete])
  (:require [cob-app.options])
  (:gen-class))

(def DEFAULT_PORT 5000)

(defn extract-port [args]
  (try
    (Integer. (second (drop-while #(not= "-p" %) args)))
    (catch Exception _ DEFAULT_PORT)))

(defn -main [& args]
  (let [server (java.net.ServerSocket. (extract-port args))
        pool (java.util.concurrent.Executors/newSingleThreadExecutor)
        _ (app/initialize args)
        _ (println "Serving HTTP...")]
    (while true (let [socket (.accept server)]
                  (.submit pool (cast Runnable #(app/relay socket)))))))

