(ns webserver.validator)

(defn parse-request-line [request-line]
  (let [regex #"(^[A-Z]+) (.+) (HTTP/\d\.\d)$"
        [_ method uri version] (first (re-seq regex request-line))]
    {:method method
     :uri uri
     :version version}))

(defn parse-header-fields
  ([headers] (parse-header-fields {} (.split headers "\r\n")))
  ([acc [header & more]]
   (if (nil? header)
     acc
     (let [regex #"^([\w\-]+): ([^\r\n]+)$"
           [_ field-name field-value] (first (re-seq regex header))
           acc (assoc acc (keyword field-name) field-value) ]
       (recur acc more)))))

(defn valid? [parsed-request]
  (every? (comp not nil?) (vals parsed-request)))


