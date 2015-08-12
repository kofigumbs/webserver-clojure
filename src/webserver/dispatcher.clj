(ns webserver.dispatcher
  (:require [webserver.socket]
            [webserver.validator]))

(def DIR (atom ""))
(def file1 (str
              "HTTP/1.1 200 OK\r\n"
              "Content-Type: text/plain; charset=utf-8\r\n"
              "Content-Length: 14\r\n\r\n"
              "file1 contents\r\n"))

(defmulti route (comp :method :request-line))

(defmethod route "GET" [request] file1)

(defn set-dir [value] (reset! DIR value))

(defn dispatch [socket]
  (let [resposne (-> socket
                     webserver.socket/get-request
                     webserver.validator/parse-request
                     route)]
    (webserver.socket/respond socket resposne)))

