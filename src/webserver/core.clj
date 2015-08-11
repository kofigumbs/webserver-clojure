(ns webserver.core
  (:gen-class))

(def PORT 5000)
(def PUBLIC_DIR
  "/Users/hkgumbs/Workspaces/8thLight/webserver-clojure/cob_spec/public")

(defn parse-args [args]
  (let [m (apply hash-map args)]
    {:port (Integer. (m "-p" PORT)) :dir (m "-d" PUBLIC_DIR)}))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
