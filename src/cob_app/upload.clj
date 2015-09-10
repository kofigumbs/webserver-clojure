(ns cob-app.upload
  (:require [cob-app.core :as core]
            [webserver.response :as response]))

(def METHOD_NOT_ALLOWED [(response/make 405)])
(defmethod core/route ["PUT" "/file1"] [_ _] METHOD_NOT_ALLOWED)
(defmethod core/route ["POST" "/text-file.txt"] [_ _] METHOD_NOT_ALLOWED)
