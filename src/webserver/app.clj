(ns webserver.app
  (:require [webserver.response :as response]
            [webserver.headers :as headers]
            [clojure.java.io :as io]))

(def protocol (atom {}))

(defn initialize [backing-protocol args]
  (reset! protocol backing-protocol)
  ((:initializer @protocol) args))


(defn- write-400 [socket]
  (io/copy (response/make 400) (.getOutputStream socket)))

(defn- respond [handler request socket]
  (if (headers/valid? request)
    (handler socket request)
    (write-400 socket)))

(defn relay [socket]
  (respond (:valid-request-handler @protocol) (headers/extract socket) socket)
  (.close socket))

