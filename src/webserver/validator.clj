(ns webserver.validator)

(defn- no-value-nil? [m]
  (every? (comp not nil?) (vals m)))

(defn parse-request-line [request-line]
  (let [regex #"(^[A-Z]+) (.+) (HTTP/\d\.\d)$"
        [_ method uri version] (first (re-seq regex request-line))]
    {:method method
     :uri uri
     :version version}))

(defn parse-header
  ([fields] (if (nil? fields) {} (parse-header {} (.split fields "\r\n"))))
  ([acc [field & more]]
   (if (nil? field)
     acc
     (let [regex #"^([\w\-]+): *([^\r\n]+) *$"
           [_ field-name field-value] (first (re-seq regex field))
           acc (assoc acc (keyword field-name) field-value)]
       (recur acc more)))))

(defn parse-request [request]
  (let [[head body] (.split request "\r\n\r\n" 2)
        [request-line header] (.split head "\r\n" 2)]
    {:request-line (parse-request-line request-line)
     :header (parse-header header)
     :body (if (nil? body) "" body)}))

(def valid-request-line? no-value-nil?)
(def valid-header? no-value-nil?)

(defn valid-request? [parsed-request]
  (and
    (= (count parsed-request) 3)
    (valid-request-line? (:request-line parsed-request))
    (valid-header? (:header parsed-request))
    (contains? parsed-request :body)
    )
  )

