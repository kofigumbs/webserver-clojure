(ns cob-app.core)

(def DEFAULT_DIR
  "/Users/hkgumbs/Workspaces/8thLight/webserver-clojure/cob_spec/public/")
(def DIR (atom DEFAULT_DIR))

(defn- extract-dir [args]
  (#(if (nil? %) DEFAULT_DIR %) (second (drop-while #(not= "-d" %) args))))

(defn- add-trailing-slash [dir]
  (str dir (if-not (.endsWith dir "/") "/")))

(defmulti route (comp :method first list))

(defn initialize [args]
  (reset! DIR (add-trailing-slash (extract-dir args))))

(defn handle [request socket]
  (let [response (route request (.getInputStream socket))
        stream (.getOutputStream socket)]
    (doall (for [r response] (clojure.java.io/copy r stream)))))

