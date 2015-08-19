(ns webserver.mock-socket)

(defn make [input]
  (let [ouput-stream (java.io.ByteArrayOutputStream.)]
    (proxy [java.net.Socket]
      []
      (getInputStream [] (java.io.ByteArrayInputStream. (.getBytes input)))
      (getOutputStream [] ouput-stream))))

