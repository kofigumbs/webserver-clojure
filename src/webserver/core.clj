(ns webserver.core
  (:gen-class)
  (:require [webserver.app]
            [webserver.get]
            [webserver.delete]
            [webserver.upload]
            [webserver.servlet :as servlet]
            [cob-app.core :as app]
            [cob-app.get]
            [cob-app.upload]
            [cob-app.options]))

(defn -main [& args] (servlet/start args app/handle))
