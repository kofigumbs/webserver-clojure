(ns webserver.core
  (:require [webserver.response :as response])
  (:require [webserver.headers :as headers])
  (:require [cob-app.core :as app])
  (:require [cob-app.get])
  (:require [cob-app.upload])
  (:require [cob-app.delete])
  (:require [cob-app.options])
  (:gen-class))

(def DEFAULT_PORT 5000)

(defn- respond-400 [socket]
  (clojure.java.io/copy
    (response/make 400)
    (.getOutputStream socket)))

(defn extract-port [args]
  (try
    (Integer. (second (drop-while #(not= "-p" %) args)))
    (catch Exception _ DEFAULT_PORT)))

(defn relay [socket]
  (let [request (headers/extract socket)]
    (if (headers/valid? request)
      (app/handle request socket)
      (respond-400 socket))
    (.close socket)))

(defn -main [& args]
  (let [server (java.net.ServerSocket. (extract-port args))
        pool (java.util.concurrent.Executors/newSingleThreadExecutor)
        _ (app/initialize args)
        _ (println "Serving HTTP...")]
    (while true (let [socket (.accept server)]
                  (.submit pool (cast Runnable #(relay socket)))))))

