(ns webserver.headers)

(def END [\return \newline \return \newline])
(def REQUEST_LINE_REGEX #"(^[A-Z]+) (.+) (HTTP/\d\.\d)$")

(defn- read-until-end [stream recent content]
  (let [current (char (.read stream))
        content (conj content current)
        recent (conj recent current)]
    (cond
      (= recent END) (apply str content)
      (= (subvec END 0 (count recent))
         recent) (recur stream recent content)
      :default (recur stream [current] content))))

(defn- accumulate-fields [acc field]
  (let [[field-name field-value] (.split field ":" 2)]
    (assoc acc (keyword field-name) (.trim field-value))))

(defn- add-parameters [{uri :uri :as request}]
  (if
    (and uri (.contains uri "?"))
    (let [[domain parameters] (.split uri "\\?" 2)]
      (-> request (assoc :uri domain) (assoc :parameters parameters)))
    request))

(defn- map-fields [headers]
  (let [[request-line header-fields] (.split headers "\r\n" 2)
        [_ method uri version] (re-find REQUEST_LINE_REGEX request-line)]
    (add-parameters
      (reduce
        accumulate-fields
        {:method method :uri uri :version version}
        (.split header-fields "\r\n")))))

(defn extract [socket]
  (try
    (map-fields
      (read-until-end
        (.getInputStream socket) [] []))
    (catch Exception e {:error nil})))

(defn valid? [request]
  (not-any? nil? (vals request)))

