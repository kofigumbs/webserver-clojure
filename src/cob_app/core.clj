(ns cob-app.core)

(def DEFAULT_DIR
  "/Users/hkgumbs/Workspaces/8thLight/webserver-clojure/cob_spec/public/")
(def DIR (atom DEFAULT_DIR))

(defn- extract-dir [args]
  (#(if (nil? %) DEFAULT_DIR %) (second (drop-while #(not= "-d" %) args))))

(defn- add-trailing-slash [dir]
  (str dir (if-not (.endsWith dir "/") "/")))

(defmulti route :method)

(defmethod route :default [request] ["HTTP/1.1 200 OK\r\n"])

(defn initialize [args]
  (reset! DIR (add-trailing-slash (extract-dir args))))

(defn handle [request socket]
  (let [response (route request)]
    (doall
      (map #(clojure.java.io/copy % (.getOutputStream socket)) response))))

