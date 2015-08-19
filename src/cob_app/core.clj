(ns cob-app.core)

(def DEFAULT_DIR
  "/Users/hkgumbs/Workspaces/8thLight/webserver-clojure/cob_spec/public/")
(def DIR (atom DEFAULT_DIR))
(def IMAGE_EXTENSION #"\.(jpeg|png|gif)$")

(defn- extract-dir [args]
  (#(if (nil? %) DEFAULT_DIR %) (second (drop-while #(not= "-d" %) args))))

(defn- add-trailing-slash [dir]
  (str dir (if-not (.endsWith dir "/") "/")))

(defn- get-request-404 [] ["HTTP/1.1 404 Not Found\r\n"])

(defn- get-request-directory [folder]
  (concat
    ["HTTP/1.1 200 OK\r\n"
     "Content-Type: text/html\r\n\r\n"
     "<!DOCTYPE html><html><body>"]
    (map #(format "<a href=\"/%s\">%s</a>" % %) (.list folder))
    ["</body></html>"]))

(defn- get-request-image [file]
  ["HTTP/1.1 200 OK\r\n"
   (format
     "Content-Type: image/%s\r\n\r\n"
     (second (re-find IMAGE_EXTENSION (.getName file))))
   file])

(defn- get-request-octet-stream [file]
  ["HTTP/1.1 200 OK\r\n"
   "Content-Type: application/octet-stream\r\n\r\n"
   (slurp file)])

(defn- get-request-response [file]
  (cond
    (not (.exists file)) (get-request-404)
    (.isDirectory file) (get-request-directory file)
    (re-find IMAGE_EXTENSION (.getName file)) (get-request-image file)
    :default (get-request-octet-stream file)
    )
  )

(defmulti route :method)

(defmethod route "GET" [request]
  (get-request-response (clojure.java.io/file (str @DIR (:uri request)))))

(defmethod route :default [request] ["HTTP/1.1 200 OK\r\n"])

(defn initialize [args]
  (reset! DIR (add-trailing-slash (extract-dir args))))

(defn handle [request socket]
  (let [response (route request)]
    (doall
      (map #(clojure.java.io/copy % (.getOutputStream socket)) response))))

