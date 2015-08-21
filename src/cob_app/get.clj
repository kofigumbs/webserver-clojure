(ns cob-app.get
  (:require [cob-app.core :refer [route DIR]]))

(def IMAGE_EXTENSION #"\.(jpeg|png|gif)$")
(def REDIRECT "redirect")

(defn- redirect [{host :Host}]
  ["HTTP/1.1 302 Found\r\n"
   "Location: http://" host "/\r\n\r\n"])

(defn- not-found []
  ["HTTP/1.1 404 Not Found\r\n"])

(defn- request-image [file]
  ["HTTP/1.1 200 OK\r\n"
   (format
     "Content-Type: image/%s\r\n\r\n"
     (second (re-find IMAGE_EXTENSION (.getName file))))
   file])

(defn- request-directory [folder]
  (concat
    ["HTTP/1.1 200 OK\r\n"
     "Content-Type: text/html\r\n\r\n"
     "<!DOCTYPE html><html><body>"]
    (map #(format "<a href=\"/%s\">%s</a>" % %) (.list folder))
    ["</body></html>"]))

(defn- request-octet-stream [file]
  ["HTTP/1.1 200 OK\r\n"
   "Content-Type: application/octet-stream\r\n\r\n"
   file])

(defn- respond [file request]
  (cond
    (= REDIRECT (.getName file)) (redirect request)
    (not (.exists file)) (not-found)
    (re-find IMAGE_EXTENSION (.getName file)) (request-image file)
    (.isDirectory file) (request-directory file)
    :default (request-octet-stream file)))

(defmethod route "GET" [request _]
  (respond
    (clojure.java.io/file (str @DIR (:uri request)))
    request))

