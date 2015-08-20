(ns cob-app.add
  (:require [cob-app.core :refer [route DIR]]))

(defn- write-file [input-stream output-file length]
  (let [contents (byte-array length)]
    (do
      (.read input-stream contents)
      (clojure.java.io/copy contents output-file))))

(defn- add [request input-stream]
  (do
    (write-file
      input-stream
      (java.io.File. (str @DIR (:uri request)))
      (Integer. (:Content-Length request)))
    ["HTTP/1.1 200 OK\r\n\r\n"]))

(defmethod route "PUT" [request input-stream] (add request input-stream))
(defmethod route "POST" [request input-stream] (add request input-stream))
