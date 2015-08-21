(ns cob-app.delete
  (:require [cob-app.core :refer [route DIR]]))

(defmethod route "DELETE" [request _]
  (if
    (= :failed
       (clojure.java.io/delete-file (str @DIR (:uri request)) :failed))
    ["HTTP/1.1 204 No Content\r\n"]
    ["HTTP/1.1 200 OK\r\n"]))

