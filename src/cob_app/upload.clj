(ns cob-app.upload
  (:require [cob-app.core :as core]
            [webserver.response :as response]))

(def METHOD_NOT_ALLOWED [(response/make 405)])

(defn- write-file [input-stream output-file length]
  (let [contents (byte-array length)]
    (.read input-stream contents)
    (clojure.java.io/copy contents output-file)
    [(response/make 200)]))

(defn- get-length [{length :Content-Length} input-stream]
  (if length
    (Integer. length)
    (.available input-stream)))

(defn- upload [request input-stream]
  (write-file
    input-stream
    (java.io.File. (str @core/DIR (:uri request)))
    (get-length request input-stream)))

(defmethod core/pre-route ["PUT" "/file1"] [_ _]
  METHOD_NOT_ALLOWED)

(defmethod core/pre-route ["POST" "/text-file.txt"] [_ _]
  METHOD_NOT_ALLOWED)

(defmethod core/route "PUT" [request input-stream]
  (upload request input-stream))

(defmethod core/route "POST" [request input-stream]
  (upload request input-stream))

