(ns cob-app.get
  (:require [cob-app.core :as core]
            [webserver.response :as response]
            [clojure.data.codec.base64 :as b64]))

(def IMAGE_EXTENSION #"\.(jpeg|png|gif)$")
(def AUTHORIZATION
  (str "Basic " (String. (b64/encode (.getBytes "admin:hunter2")))))

(defn- not-found []
  [(response/make 404)])

(defn- get-image-type [file]
  (#(if % (str "image/" %))
        (second (re-find IMAGE_EXTENSION (.getName file)))))

(defn- get-content-type [file]
  (first
    (keep identity [(get-image-type file) "application/octet-stream"])))

(defn- extract-bytes [field]
  (let [[_ start end] (re-find #"^bytes=(\d+)?-(\d+)?$" field)]
    (map #(try (Integer. ^String %) (catch Exception _ 0)) [start end])))

(defn- parse-range [file file-range]
  (let [[start end] (extract-bytes (str file-range))]
    [start  (- (if (> end start) (inc end) (.length file)) start)]))

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

(defn- request-directory [folder]
  (concat
    [(response/make 200 {:Content-Type "text/html"})
     "<!DOCTYPE html><html><body>"]
    (map #(format "<a href=\"/%s\">%s</a>" % %) (.list folder))
    ["</body></html>"]))

(defn- respond [file file-range]
  (cond
    (not (.exists file)) (not-found)
    (.isDirectory file) (request-directory file)
    :default (request-file file file-range)))

(defmethod core/pre-route ["GET" "/redirect"] [{host :Host} _]
  [(response/make 302 {:Location (format "http://%s/" host)})])

(defmethod core/pre-route ["GET" "/parameters"] [{parameters :parameters} _]
  [(response/make 200)
   (if parameters
       (-> parameters
           (.replaceAll "=" " = ")
           (.replaceAll "&" "\r\n")
           (java.net.URLDecoder/decode "UTF-8")))])

(defmethod core/pre-route ["GET" "/logs"] [{token :Authorization} _]
  (if (= token AUTHORIZATION)
    (cons (response/make 200) @core/LOG)
    [(response/make 401) "Authentication required"]))

(defmethod core/route "GET" [request _]
  (respond
    (clojure.java.io/file (str @core/DIR (:uri request)))
    (:Range request)))

