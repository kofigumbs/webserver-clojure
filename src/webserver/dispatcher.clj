(ns webserver.dispatcher
  (:require [webserver.socket]
            [webserver.validator]))

(def DIR (atom ""))

(defmulti route (comp :method :request-line))

(defmethod route "GET" [request]
  (try (str "HTTP/1.1 200 OK\r\n"
            "Content-Type: text/plain; charset=utf-8\r\n"
            "Content-Length: 14\r\n\r\n"
            (->> request :request-line :uri (str @DIR) slurp))
       (catch java.io.FileNotFoundException _ "HTTP/1.1 404 Not Found\r\n")))

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

