(ns webserver.util)


(defn extract-str [args k default]
  (#(if % % default) (second (drop-while #(not= k %) args))))
(defn extract-int [args k default]
  (try (Integer. ^String (extract-str args k nil))
       (catch Exception _ default)))
