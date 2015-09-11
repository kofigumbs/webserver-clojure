(ns cob-app.get
  (:require [cob-app.core :as core]
            [webserver.app :as app]
            [webserver.response :as response]
            [clojure.java.io :as io]
            [clojure.data.codec.base64 :as b64]))

(def AUTHORIZATION
  (str "Basic " (String. (b64/encode (.getBytes "admin:hunter2")))))

(defmethod core/route ["GET" "/redirect"] [{host :Host} _]
  [(response/make 302 {:Location (format "http://%s/" host)})])

(defmethod core/route ["GET" "/parameters"] [{parameters :parameters} _]
  [(response/make 200)
   (if parameters
       (-> parameters
           (.replaceAll "=" " = ")
           (.replaceAll "&" "\r\n")
           (java.net.URLDecoder/decode "UTF-8")))])

(defmethod core/route ["GET" "/"] [_ _]
  (concat
    [(response/make 200 {:Content-Type "text/html"})
     "<!DOCTYPE html><html><body>"]
    (map #(format "<a href=\"/%s\">%s</a>" % %) (.list (io/file (app/get-dir))))
    ["</body></html>"]))

(defmethod core/route ["GET" "/logs"] [{token :Authorization} _]
  (if (= token AUTHORIZATION)
    (cons (response/make 200) (core/get-log))
    [(response/make 401) "Authentication required"]))
