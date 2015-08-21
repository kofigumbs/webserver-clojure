(ns cob-app.options
  (:require [cob-app.core :refer [route DIR]]))

(defmethod route "OPTIONS" [_ _]
  ["HTTP/1.1 200 OK\r\n"
   "Allow: GET,HEAD,POST,OPTIONS,PUT\r\n\r\n"])

