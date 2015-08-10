(ns webserver.socket)

(defn get-request [socket]
  (let [scan (.useDelimiter
               (new java.util.Scanner (.getInputStream socket)) "\\A")]
    (if (.hasNext scan) (.next scan) "")
    ))
