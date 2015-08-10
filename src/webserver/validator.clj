(ns webserver.validator)

(defn parse [request]
  (let [regex #"(^[A-Z]+) (.+) (HTTP/\d\.\d)$"
        [_ method uri version] (first (re-seq regex request))]
    {:method method
     :uri uri
     :version version
     }))

(defn valid? [parsed-request]
  (every? (comp not nil?) (vals parsed-request))
  )
