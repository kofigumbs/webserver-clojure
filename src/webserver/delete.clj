(ns webserver.delete
  (:require [webserver.app :as app]
            [webserver.response :as response]
            [clojure.java.io :as io]))

(defn- delete-file [{uri :uri}]
  (= :failed (io/delete-file (str (app/get-dir) uri) :failed)))

(defmethod app/route "DELETE" [_ request]
  [(response/make (if (delete-file request) 204 200))])
