(ns webserver.socket)

(defn get-request [socket]
  (let [scan (.useDelimiter
               (java.util.Scanner. (.getInputStream socket)) "\\A")]
    (if (.hasNext scan) (.next scan) "")))

(defn respond [socket response]
  (.write (.getOutputStream socket) (.getBytes response)))

