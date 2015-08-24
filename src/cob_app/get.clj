(ns cob-app.get
  (:require [cob-app.core :as core]
            [webserver.response :as response]
            [clojure.data.codec.base64 :as b64]))

(def IMAGE_EXTENSION #"\.(jpeg|png|gif)$")
(def AUTHORIZATION
  (str "Basic " (String. (b64/encode (.getBytes "admin:hunter2")))))

(defn- not-found []
  [(response/make 404)])

(defn- get-image-extension [file]
  (second (re-find IMAGE_EXTENSION (.getName file))))

(defn- request-image [file]
  [(response/make
     200
     {:Content-Type
      (str "image/" (get-image-extension file))})
   file])

(defn- request-directory [folder]
  (concat
    [(response/make 200 {:Content-Type "text/html"})
     "<!DOCTYPE html><html><body>"]
    (map #(format "<a href=\"/%s\">%s</a>" % %) (.list folder))
    ["</body></html>"]))

(defn- request-octet-stream [file]
  [(response/make 200 {:Content-Type "application/octet-stream"})
   file])

(defn- respond [file request]
  (cond
    (not (.exists file)) (not-found)
    (re-find IMAGE_EXTENSION (.getName file)) (request-image file)
    (.isDirectory file) (request-directory file)
    :default (request-octet-stream file)))

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
    request))

