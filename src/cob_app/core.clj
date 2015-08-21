(ns cob-app.core)

(def DEFAULT_DIR
  "/Users/hkgumbs/Workspaces/8thLight/webserver/cob_spec/public/")
(def DIR (atom DEFAULT_DIR))

(defn- extract-dir [args]
  (#(if % % DEFAULT_DIR) (second (drop-while (partial not= "-d") args))))

(defn- add-trailing-slash [dir]
  (str dir (if-not (.endsWith dir "/") "/")))

(defn- dispatch [request _]
  (:method request))

(defmulti route dispatch)

(defmethod route :default [request input-stream]
  ["HTTP/1.1 501 Not Implemented\r\n\r\n"])

(defn initialize [args]
  (reset! DIR (add-trailing-slash (extract-dir args))))

(defn handle [request socket]
  (let [response (route request (.getInputStream socket))
        input-stream (.getOutputStream socket)]
    (doall (for [r response] (clojure.java.io/copy r input-stream)))))

