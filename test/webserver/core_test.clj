(ns webserver.core-test
  (:require [speclj.core :refer :all]
            [webserver.core :as core]))

(describe "Command line argument"
  (it "assigns correct port"
    (should= 80 (core/extract-port ["-p" "80"]))
    (should= 8080 (core/extract-port ["-p" "8080"]))
    (should= core/DEFAULT_PORT (core/extract-port ["-p" "asdf"]))
    (should= core/DEFAULT_PORT (core/extract-port []))))

