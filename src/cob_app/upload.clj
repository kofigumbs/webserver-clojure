(ns cob-app.upload
  (:require [cob-app.core :refer [route DIR]]))

(defn- disallow []
  ["HTTP/1.1 405 Method Not Allowed\r\n\r\n"])

(defn- write-file [input-stream output-file length]
  (let [contents (byte-array length)]
    (.read input-stream contents)
    (clojure.java.io/copy contents output-file)
    ["HTTP/1.1 200 OK\r\n\r\n"]))

(defn- get-length [{length :Content-Length} input-stream]
  (if length
    (Integer. length)
    (.available input-stream)))

(defn- upload [request input-stream]
  (let [file (java.io.File. (str @DIR (:uri request)))]
    (if (.exists file)
      (disallow)
      (write-file input-stream file (get-length request input-stream)))))

(defmethod route "PUT" [request input-stream]
  (upload request input-stream))

(defmethod route "POST" [request input-stream]
  (upload request input-stream))

