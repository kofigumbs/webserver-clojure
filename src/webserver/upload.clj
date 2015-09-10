(ns webserver.upload
  (:require [webserver.app :as app]
            [webserver.response :as response]
            [clojure.java.io :as io]
            [pandect.algo.sha1 :refer [sha1]]))

(defn- write-file [input-stream output-file length]
  (let [contents (byte-array length)]
    (.read input-stream contents)
    (io/copy contents output-file)
    [(response/make 200)]))

(defn- get-length [{length :Content-Length} input-stream]
  (if length (Integer. ^String length) (.available input-stream)))

(defn- upload [request input-stream]
  (write-file
    input-stream
    (io/file (str (app/get-dir) (:uri request)))
    (get-length request input-stream)))

(defn- valid-hash? [{etag :If-Match uri :uri}]
  (let [file (io/file (str (app/get-dir) uri))]
    (and etag (.exists file) (= etag (sha1 file)))))

(defn- patch [request input-stream]
  (upload request input-stream) (response/make 204))

(defmethod app/route "PUT" [socket request]
  (upload request (.getInputStream socket)))

(defmethod app/route "POST" [socket request]
  (upload request (.getInputStream socket)))

(defmethod app/route "PATCH" [socket request]
  [(cond
     (valid-hash? request) (patch request (.getInputStream socket))
     (:If-Match request) (response/make 412)
     :default (response/make 409))])
