(ns webserver.dispatcher
  (:require [webserver.socket]
            [webserver.validator]))

(def DIR (atom ""))

(defn- get-request-directory [folder]
  (apply
    str
    (concat
      ["HTTP/1.1 200 OK\r\n"
       "Content-Type: text/html\r\n\r\n"
       "<!DOCTYPE html><html>"
       "<title>Directory listing</title>"
       "<body>"
       "<h2>Directory listing</h2>"
       "<hr>" "<ul>"]
      (map #(format "<li><a href=\"%s\">%s</a>" % %) (.list folder))
      ["</ul>" "<hr>" "</body>" "</html>"])))

(defn- get-request-file [file]
  (str "HTTP/1.1 200 OK\r\n"
       "Content-Type: application/octet-stream\r\n\r\n"
       (slurp file)))

(defn- get-request-response [file]
  (cond
    (.isDirectory file) (get-request-directory file)
    (.isFile file) (get-request-file file)
    :default "HTTP/1.1 404 Not Found\r\n"
    )
  )

(defmulti route (comp :method :request-line))

(defmethod route "GET" [request]
  (get-request-response
    (clojure.java.io/file
      (str @DIR (-> request :request-line :uri)))))

(defmethod route :default [request]
  "HTTP/1.1 400 Bad Request\r\n")

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
    (webserver.socket/respond socket response)))

