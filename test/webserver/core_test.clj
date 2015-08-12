(ns webserver.core-test
  (:require [speclj.core :refer :all]
            [webserver.core :refer :all]))

(describe "Valid argument passing"
  (it "contains correct port"
    (should= 80 (:port (parse-args ["-p" "80"])))
    (should= 8080 (:port (parse-args ["-p" "8080"])))
    (should= PORT (:port (parse-args [])))
    )

  (it "contains correct dir"
    (should= "/some/random/path" (:dir (parse-args ["-d" "/some/random/path"])))
    (should= "/" (:dir (parse-args ["-d" "/"])))
    (should= PUBLIC_DIR (:dir (parse-args [])))
    )

  (it "has both options"
    (should= {:port 80 :dir "/tmp"} (parse-args ["-p" "80" "-d" "/tmp"]))
    (should= {:port PORT :dir PUBLIC_DIR} (parse-args []))
    )
  )
