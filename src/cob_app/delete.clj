(ns cob-app.delete
  (:require [cob-app.core :as core]
            [clojure.java.io :as io]))

(defmethod core/route "DELETE" [request _]
  (if
    (= :failed
       (io/delete-file (str @core/DIR (:uri request)) :failed))
    ["HTTP/1.1 204 No Content\r\n"]
    ["HTTP/1.1 200 OK\r\n"]))

