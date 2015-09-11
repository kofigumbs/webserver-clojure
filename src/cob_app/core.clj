(ns cob-app.core
  (:require [webserver.response :as response]
            [webserver.util :as util]
            [clojure.java.io :as io]))

(def log (atom []))

(def DEFAULT_DIR "./tmp/")
(def DIR (atom DEFAULT_DIR))

(defn- dispatch-route [request _] [(:method request) (:uri request)])
(defmulti route dispatch-route)
(defmethod route :default [_ _])

(defn get-log [] @log)
(defn- update-log [{method :method uri :uri version :version}]
  (swap! log conj (format "%s %s %s\r\n" method uri version)))
(defn- write [output socket]
  (doseq [o output] (io/copy o (.getOutputStream socket))))

(defn handle [socket request]
  (update-log request)
  (if-let [result (route request (.getInputStream socket))]
    (do (write result socket) true)))
