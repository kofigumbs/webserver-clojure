(ns webserver.get
  (:require [webserver.app :as app]
            [webserver.response :as response]
            [clojure.java.io :as io]))

(def image-extension #"\.(jpeg|png|gif)$")
(defn- get-image-type [file]
  (#(if % (str "image/" %))
        (second (re-find image-extension (.getName file)))))

(defn- get-content-type [file]
  (#(if %1 %1 %2) (get-image-type file) "application/octet-stream"))

(defn- extract-bytes [field]
  (let [[_ start end] (re-find #"^bytes=(\d+)?-(\d+)?$" field)]
    (map #(try (Integer. ^String %) (catch Exception _ nil)) [start end])))

(defn- parse-range [file file-range]
  (let [[start end] (extract-bytes (str file-range))]
    (cond
      (and start (not end)) [start (- (.length file) start)]
      (and (not start) end) [(- (.length file) end) end]
      (and start end) [start (inc (- end start))]
      :default [0 (.length file)])))

(defn- read-file [file file-range]
  (let [[start length] (parse-range file file-range)
        output (byte-array length)
        file-input-stream (java.io.FileInputStream. file)]
    (.skip file-input-stream start)
    (.read file-input-stream output)
    output))

(defn- request-file [file file-range]
  [(response/make
     (if file-range 206 200)
     {:Content-Type (get-content-type file)})
   (read-file file file-range)])

(defn- not-found [] [(response/make 404)])
(defn- respond [file file-range]
  (if (not (.exists file)) (not-found) (request-file file file-range)))

(defmethod app/route "GET" [socket {:keys [uri Range]}]
  (respond (io/file (str (app/get-dir) uri)) Range))
