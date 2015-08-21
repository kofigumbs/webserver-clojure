(ns cob-app.core-test
  (:require [speclj.core :refer :all]
            [cob-app.core :refer :all]
            [webserver.mock-socket]))

(describe "Set directory dir"
  (it "properly sets public string"
    (initialize [])
    (should= @DIR DEFAULT_DIR)
    (initialize ["-d" "tmp"])
    (should= @DIR "tmp/")
    (initialize ["-d" "dir/"])
    (should= @DIR "dir/")))


(describe "Default response"
  (it "501s on nonsense request"
    (should=
      "HTTP/1.1 501 Not Implemented\r\n\r\n"
      (webserver.mock-socket/connect
        handle
        {:method "FOOBAR" :uri "/" :version "HTTP/1.1"}))))
