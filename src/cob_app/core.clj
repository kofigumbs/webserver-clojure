(ns cob-app.core
  (:require [webserver.response :as response]
            [webserver.app :as app]))

(def DEFAULT_DIR "./tmp/")
(def DIR (atom DEFAULT_DIR))
(def LOG (atom []))

(defn- extract-dir [args]
  (#(if % % DEFAULT_DIR) (second (drop-while (partial not= "-d") args))))

(defn- update-log [{method :method uri :uri version :version}]
  (swap! LOG conj (format "%s %s %s\r\n" method uri version)))

(defn- add-trailing-slash [dir]
  (str dir (if-not (.endsWith dir "/") "/")))

(defn- dispatch-route [request _]
  (:method request))

(defn- dispatch-pre-route [request _]
  [(:method request) (:uri request)])

(defmulti route dispatch-route)

(defmulti pre-route dispatch-pre-route)

(defmethod route :default [request input-stream]
  [(response/make 501)])

(defmethod pre-route :default [request input-stream]
  (route request input-stream))

(defmethod app/initialize true [args]
  (reset! DIR (add-trailing-slash (extract-dir args))))

(defmethod app/handle true [socket request]
  (let [_ (update-log request)
        response (pre-route request (.getInputStream socket))
        output-stream (.getOutputStream socket)]
    (doall (for [r response] (clojure.java.io/copy r output-stream)))))

