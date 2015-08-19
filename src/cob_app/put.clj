(ns cob-app.put
  (:require [cob-app.core :refer [route DIR]]))

(defn- write-file [input-stream output-file length]
  (let [contents (byte-array length)]
    (do
      (.read input-stream contents)
      (clojure.java.io/copy contents output-file))))

(defmethod route "PUT" [request input-stream]
  (do
    (write-file
      input-stream
      (java.io.File. (str @DIR (:uri request)))
      (Integer. (:Content-Length request)))
    ["HTTP/1.1 200 OK\r\n\r\n"]))

