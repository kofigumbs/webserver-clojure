(ns webserver.response)

(def VERSION "HTTP/1.1")
(def NEW_LINE "\r\n")
(def NAMES
  {200 "OK"
   204 "No Content"
   302 "Found"
   400 "Bad Request"
   401 "Authorization Required"
   404 "Not Found"
   405 "Method Not Allowed"
   501 "Not Implemented"})

(defn- make-response-line [code]
  (if (contains? NAMES code)
    (str VERSION " " code " " (NAMES code) NEW_LINE)
    (throw (Exception. "Invalid code"))))

(defn- make-header [[field-name field-value]]
  (str (name field-name) ": " field-value NEW_LINE))

(defn- make-headers [fields]
  (apply str (map make-header fields)))

(defn make
  ([code] (make code {}))
  ([code fields]
   (str (make-response-line code) (make-headers fields) NEW_LINE)))

