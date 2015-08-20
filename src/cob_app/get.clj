(ns cob-app.get
  (:require [cob-app.core :refer [route DIR]]))

(def IMAGE_EXTENSION #"\.(jpeg|png|gif)$")

(defn- not-found [] ["HTTP/1.1 404 Not Found\r\n"])

(defn- request-directory [folder]
  (concat
    ["HTTP/1.1 200 OK\r\n"
     "Content-Type: text/html\r\n\r\n"
     "<!DOCTYPE html><html><body>"]
    (map #(format "<a href=\"/%s\">%s</a>" % %) (.list folder))
    ["</body></html>"]))

(defn- request-image [file]
  ["HTTP/1.1 200 OK\r\n"
   (format
     "Content-Type: image/%s\r\n\r\n"
     (second (re-find IMAGE_EXTENSION (.getName file))))
   file])

(defn- request-octet-stream [file]
  ["HTTP/1.1 200 OK\r\n"
   "Content-Type: application/octet-stream\r\n\r\n"
   (slurp file)])

(defn- respond [file]
  (cond
    (not (.exists file)) (not-found)
    (.isDirectory file) (request-directory file)
    (re-find IMAGE_EXTENSION (.getName file)) (request-image file)
    :default (request-octet-stream file)))

(defmethod route "GET" [request _]
  (respond (clojure.java.io/file (str @DIR (:uri request)))))

