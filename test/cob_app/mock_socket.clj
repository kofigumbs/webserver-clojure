(ns cob-app.mock-socket
  (:require [webserver.mock-socket :as mock-socket]
            [cob-app.core :as core]))

(defn connect
  ([request] (connect request ""))
  ([request body] (mock-socket/connect core/handle request body)))
