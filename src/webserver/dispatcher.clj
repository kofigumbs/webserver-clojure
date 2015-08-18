(ns webserver.dispatcher
  (:require [webserver.socket]
            [webserver.validator]))

(def DIR (atom ""))

(def image-extension #"\.(jpeg|png|gif)$")

(defn- get-request-404 [] ["HTTP/1.1 404 Not Found\r\n"])

(defn- get-request-directory [folder]
  (concat
    ["HTTP/1.1 200 OK\r\n"
     "Content-Type: text/html\r\n\r\n"
     "<!DOCTYPE html><html>"
     "<title>Directory listing</title>"
     "<body>"
     "<h2>Directory listing</h2>"
     "<hr>" "<ul>"]
    (map #(format "<li><a href=\"%s\">%s</a>" % %) (.list folder))
    ["</ul>" "<hr>" "</body>" "</html>"]))

(defn- get-request-image [file]
  ["HTTP/1.1 200 OK\r\n"
   (format
     "Content-Type: image/%s\r\n\r\n"
     (second (re-find image-extension (.getName file))))
   file])

(defn- get-request-octet-stream [file]
  ["HTTP/1.1 200 OK\r\n"
   "Content-Type: application/octet-stream\r\n\r\n"
   (slurp file)])

(defn- get-request-response [file]
  (cond
    (not (.exists file)) (get-request-404)
    (.isDirectory file) (get-request-directory file)
    (re-find image-extension (.getName file)) (get-request-image file)
    :default (get-request-octet-stream file)
    )
  )

(defmulti route (comp :method :request-line))

(defmethod route "GET" [request]
  (get-request-response
    (clojure.java.io/file
      (str @DIR (-> request :request-line :uri)))))

(defmethod route :default [request]
  ["HTTP/1.1 400 Bad Request\r\n"])

(defn set-dir [value]
  (if
    (.endsWith value "/")
    (reset! DIR value)
    (reset! DIR (str value "/"))))

(defn dispatch [socket]
  (let [response (-> socket
                     webserver.socket/get-request
                     webserver.validator/parse-request
                     route)]
    (doall
      (map #(clojure.java.io/copy % (.getOutputStream socket)) response))))

