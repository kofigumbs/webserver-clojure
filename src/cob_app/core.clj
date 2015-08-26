(ns cob-app.core
  (:require [webserver.response :as response]
            [clojure.java.io :as io]))

(def LOG (atom []))

(def DEFAULT_DIR "./tmp/")
(def DIR (atom DEFAULT_DIR))

(defn- extract-dir [args]
  (#(if % % DEFAULT_DIR) (second (drop-while (partial not= "-d") args))))

(defn- ensure-trailing-slash [dir]
  (str dir (if-not (.endsWith dir "/") "/")))

(defn- initialize [args]
  (reset! DIR (ensure-trailing-slash (extract-dir args))))


(defn- dispatch-route [request _] (:method request))
(defmulti route dispatch-route)
(defmethod route :default [request input-stream] [(response/make 501)])

(defn- dispatch-pre-route [request _] [(:method request) (:uri request)])
(defmulti pre-route dispatch-pre-route)
(defmethod pre-route :default [request input-stream]
  (route request input-stream))

(defn- update-log [{method :method uri :uri version :version}]
  (swap! LOG conj (format "%s %s %s\r\n" method uri version)))

(defn- handle [socket request]
  (let [response (pre-route request (.getInputStream socket))]
    (update-log request)
    (doall (for [r response] (io/copy r (.getOutputStream socket))))))

(def responder {:valid-request-handler handle :initializer initialize})

