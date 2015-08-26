(ns cob-app.mock-socket
  (:require [webserver.mock-socket :as socket]
            [cob-app.core :as app]))

(def handler (:valid-request-handler app/responder))

(defn connect
  ([request] (socket/connect handler request))
  ([request body] (socket/connect handler request body)))

