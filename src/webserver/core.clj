(ns webserver.core
  (:require [cob-app.core :as app])
  (:require [cob-app.get])
  (:require [cob-app.add])
  (:gen-class))

(def DEFAULT_PORT 5000)
(def END_HEADER [\return \newline \return \newline])
(def REQUEST_LINE_REGEX #"(^[A-Z]+) (.+) (HTTP/\d\.\d)$")

(defn- read-until-end-header [stream recent content]
  (let [current (char (.read stream))
        content (conj content current)
        recent (conj recent current)]
    (cond
      (= recent END_HEADER) (apply str content)
      (= (subvec END_HEADER 0 (count recent))
         recent) (recur stream recent content)
      :default (recur stream [current] content))))

(defn- accumulate-header-fields [acc field]
  (let [[field-name field-value] (.split field ":" 2)]
    (assoc acc (keyword field-name) (.trim field-value))))

(defn- map-headers [headers]
  (let [[request-line header-fields] (.split headers "\r\n" 2)
        [_ method uri version] (re-find REQUEST_LINE_REGEX request-line)]
    (reduce
      accumulate-header-fields
      {:method method :uri uri :version version}
      (.split header-fields "\r\n"))))

(defn respond-400 [socket]
  (clojure.java.io/copy
    "HTTP/1.1 400 Bad Request\r\n"
    (.getOutputStream socket)))

(defn extract-headers [socket]
  (try
    (map-headers
      (read-until-end-header
        (.getInputStream socket) [] []))
    (catch Exception e {:error nil})))

(defn extract-port [args]
  (try
    (Integer. (second (drop-while #(not= "-p" %) args)))
    (catch Exception _ DEFAULT_PORT)))

(defn valid-headers [request-map]
  (not-any? nil? (vals request-map)))

(defn -main [& args]
  (let [server (java.net.ServerSocket. (extract-port args))
        _ (app/initialize args)
        _ (println "Serving HTTP...")]
    (while true (with-open [socket (.accept server)]
                  (#(if (valid-headers %)
                      (app/handle % socket)
                      (respond-400 socket)) (extract-headers socket))))))

