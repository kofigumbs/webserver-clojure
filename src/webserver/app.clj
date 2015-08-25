(ns webserver.app
  (:require [webserver.response :as response])
  (:require [webserver.headers :as headers])
  (:require [clojure.java.io :as io])
  (:gen-class))

(defn- dispatch-handle [_ request]
  (headers/valid? request))

(defmulti initialize coll?)

(defmulti handle dispatch-handle)

(defmethod initialize :default [& _])

(defmethod handle :default [socket _]
  (io/copy (response/make 400) (.getOutputStream socket)))

(defn relay [socket]
  (handle socket (headers/extract socket))
  (.close socket))

