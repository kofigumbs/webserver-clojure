(ns cob-app.options
  (:require [cob-app.core :as core]
            [webserver.response :as response]))

(defmethod core/pre-route ["OPTIONS" "/method_options"] [_ _]
  [(response/make 200 {:Allow "GET,HEAD,POST,OPTIONS,PUT"})])

