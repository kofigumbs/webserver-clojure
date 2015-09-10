(ns webserver.app
  (:require [webserver.response :as response]
            [webserver.headers :as headers]
            [webserver.util :as util]
            [clojure.java.io :as io]))

(def default-dir "./tmp/")
(def dir (atom default-dir))
(defn- ensure-trailing-slash [dir] (str dir (if-not (.endsWith dir "/") "/")))
(defn get-dir [] @dir)
(defn initialize [args]
  (reset! dir (ensure-trailing-slash (util/extract-str args "-d" default-dir))))

(defn- write [code socket]
  (io/copy (response/make code) (.getOutputStream socket)))

(defn- dispatcher [_ request] (:method request))
(defmulti route dispatcher)
(defmethod route :default [socket request] (write 501 socket))

(defn- handle-internally [socket request]
  (doseq [output (route socket request)]
    (io/copy output (.getOutputStream socket))))
(defn respond [handler socket request]
  (if (headers/valid? request)
    (or (handler socket request) (handle-internally socket request))
    (write 400 socket)))

(defn relay [handler socket]
  (respond handler socket (headers/extract socket))
  (.close socket))
