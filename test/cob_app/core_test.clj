(ns cob-app.core-test
  (:require [speclj.core :refer :all]
            [cob-app.core :refer :all]
            [webserver.mock-socket]
            [clojure.data.codec.base64]))

(describe "Set directory dir"
  (it "properly sets public string"
    (initialize [])
    (should= @DIR DEFAULT_DIR)
    (initialize ["-d" "tmp"])
    (should= @DIR "tmp/")
    (initialize ["-d" "dir/"])
    (should= @DIR "dir/")))

