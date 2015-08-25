(ns cob-app.upload
  (:require [cob-app.core :as core]
            [webserver.response :as response]
            [clojure.java.io :as io]
            [pandect.algo.sha1 :refer [sha1]]))

(def METHOD_NOT_ALLOWED [(response/make 405)])

(defn- write-file [input-stream output-file length]
  (let [contents (byte-array length)]
    (.read input-stream contents)
    (io/copy contents output-file)
    [(response/make 200)]))

(defn- get-length [{length :Content-Length} input-stream]
  (if length
    (Integer. length)
    (.available input-stream)))

(defn- upload [request input-stream]
  (write-file
    input-stream
    (io/file (str @core/DIR (:uri request)))
    (get-length request input-stream)))

(defn- valid-hash? [{etag :If-Match uri :uri}]
  (let [file (io/file (str @core/DIR uri))]
    (and etag (.exists file) (= etag (sha1 file)))))

(defn- patch [request input-stream]
  (upload request input-stream)
  (response/make 204))

(defmethod core/pre-route ["PUT" "/file1"] [_ _]
  METHOD_NOT_ALLOWED)

(defmethod core/pre-route ["POST" "/text-file.txt"] [_ _]
  METHOD_NOT_ALLOWED)

(defmethod core/route "PUT" [request input-stream]
  (upload request input-stream))

(defmethod core/route "POST" [request input-stream]
  (upload request input-stream))

(defmethod core/route "PATCH" [{etag :If-Match :as request} input-stream]
  [(cond
     (valid-hash? request) (patch request input-stream)
     etag (response/make 412)
     :default (response/make 409))])

