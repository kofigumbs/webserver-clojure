(ns webserver.core
  (:require [webserver.dispatcher])
  (:gen-class))

(def PORT 5000)
(def PUBLIC_DIR
  "/Users/hkgumbs/Workspaces/8thLight/webserver-clojure/cob_spec/public")

(defn parse-args [args]
  (let [m (apply hash-map args)]
    {:port (Integer. (m "-p" PORT)) :dir (m "-d" PUBLIC_DIR)}))

(defn -main [& args]
  (let [{:keys [port dir]} (parse-args args)
        server (java.net.ServerSocket. port)
        _ (webserver.dispatcher/set-dir dir)]
    (while
      true
      (webserver.dispatcher/dispatch (.accept server)))))

