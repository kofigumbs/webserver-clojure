(ns cob-app.core
  (:require [webserver.response :as response]))

(def DEFAULT_DIR "./tmp/")
(def DIR (atom DEFAULT_DIR))

(defn- extract-dir [args]
  (#(if % % DEFAULT_DIR) (second (drop-while (partial not= "-d") args))))

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

(defn initialize [args]
  (reset! DIR (add-trailing-slash (extract-dir args))))

(defn handle [request socket]
  (let [response (pre-route request (.getInputStream socket))
        output-stream (.getOutputStream socket)]
    (doall (for [r response] (clojure.java.io/copy r output-stream)))))

