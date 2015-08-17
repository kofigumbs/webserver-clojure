(ns webserver.socket)

(def END_HEADER [\return \newline \return \newline])

(defn- get-input-stream-content [is recently-seen content]
  (if
    (= recently-seen END_HEADER)
    content
    (let [current-char (char (.read is))
          current-content (conj content current-char)]
      (if
        (= (subvec END_HEADER 0 (count recently-seen)) recently-seen)
        (recur is (conj recently-seen current-char) current-content)
        (recur is [current-char] current-content))
      )
    ))

(defn get-request [socket]
  (try
    (apply str (get-input-stream-content (.getInputStream socket) [] []))
    (catch Exception e "")))

(defn respond [socket response]
  (.write (.getOutputStream socket) (.getBytes response)))

