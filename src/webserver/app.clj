(ns webserver.app
  (:require [webserver.response :as response])
  (:require [webserver.headers :as headers])
  (:require [clojure.java.io :as io])
  (:gen-class))

(defn- dispatch-handle [_ request]
  (headers/valid? request))

(defn- respond [code socket]
  (io/copy (response/make code) (.getOutputStream socket)))

(defmulti handle dispatch-handle)
(defmethod handle false [socket _] (respond 400 socket))
(defmethod handle :default [socket _] (respond 500 socket))

(defmulti initialize coll?)
(defmethod initialize :default [& _])

(defn relay [socket]
  (handle socket (headers/extract socket))
  (.close socket))

