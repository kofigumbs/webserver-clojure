(ns cob-app.delete
  (:require [cob-app.core :as core]
            [clojure.java.io :as io]
            [webserver.response :as response]))

(defn- delete-file [{uri :uri}]
  (= :failed (io/delete-file (str @core/DIR uri) :failed)))

(defmethod core/route "DELETE" [request _]
  [(response/make (if (delete-file request) 204 200))])

