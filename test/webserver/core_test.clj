(ns webserver.core-test
  (:require [clojure.test :refer :all]
            [webserver.core :refer :all]))

(deftest parse-args-test
  (testing "Port option"
    (is (= "80" (:port (parse-args ["-p" "80"]))))
    (is (= "8080" (:port (parse-args ["-p" "8080"]))))
    (is (= PORT (:port (parse-args []))))
    )

  (testing "Public dir option"
    (is (= "/some/random/path" (:dir (parse-args ["-d" "/some/random/path"]))))
    (is (= "/" (:dir (parse-args ["-d" "/"]))))
    (is (= PUBLIC_DIR (:dir (parse-args []))))
    )

  (testing "Both options"
    (is (= {:port "80" :dir "/tmp"} (parse-args ["-p" "80" "-d" "/tmp"])))
    (is (= {:port PORT :dir PUBLIC_DIR} (parse-args [])))
    )
  )
