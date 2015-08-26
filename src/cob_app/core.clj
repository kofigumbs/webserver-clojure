(ns cob-app.core
  (:require [webserver.response :as response]
            [webserver.app :as app]
            [clojure.java.io :as io]))

(def DEFAULT_DIR "./tmp/")
(def DIR (atom DEFAULT_DIR))
(def LOG (atom []))

(defn- extract-dir [args]
  (#(if % % DEFAULT_DIR) (second (drop-while (partial not= "-d") args))))

(defn- update-log [{method :method uri :uri version :version}]
  (swap! LOG conj (format "%s %s %s\r\n" method uri version)))

(defn- add-trailing-slash [dir]
  (str dir (if-not (.endsWith dir "/") "/")))

(defmethod app/initialize true [args]
  (reset! DIR (add-trailing-slash (extract-dir args))))

(defn- add-trailing-slash [dir]
  (str dir (if-not (.endsWith dir "/") "/")))


(defn- dispatch-route [request _]
  (:method request))

(defmulti route dispatch-route)

(defmethod route :default [request input-stream]
  [(response/make 501)])


(defn- dispatch-pre-route [request _]
  [(:method request) (:uri request)])

(defmulti pre-route dispatch-pre-route)

(defmethod pre-route :default [request input-stream]
  (route request input-stream))

(defmethod app/handle true [socket request]
  (let [response (pre-route request (.getInputStream socket))]
    (update-log request)
    (doall (for [r response] (io/copy r (.getOutputStream socket))))))

