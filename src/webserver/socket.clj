(ns webserver.socket)

(defn get-request [socket]
  (let [byte-input (byte-array (.available (.getInputStream socket)))]
    (do
      (.read (.getInputStream socket) byte-input)
      (slurp byte-input))))

(defn respond [socket response]
  (.write (.getOutputStream socket) (.getBytes response)))

