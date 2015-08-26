(ns cob-app.mock-socket
  (:require [webserver.mock-socket :as mock-socket]
            [cob-app.core :as app]))

(def handler (:valid-request-handler app/responder))

(defn connect
  ([request] (connect request ""))
  ([request body]
   (let [socket (mock-socket/make body)]
     (handler socket request)
     (str (.getOutputStream socket)))))

