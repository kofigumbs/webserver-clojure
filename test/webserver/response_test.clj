(ns webserver.response-test
  (:require [speclj.core :refer :all]
            [webserver.response :as response]))

(describe "Standard response"
  (it "looks right without fields"
    (should= "HTTP/1.1 400 Bad Request\r\n\r\n"
             (response/make 400)))

  (it "looks right with fields"
    (should=
      (str
        "HTTP/1.1 200 OK\r\n"
        "Allow: GET,HEAD,POST,OPTIONS,PUT\r\n\r\n")
      (response/make
        200
        {:Allow "GET,HEAD,POST,OPTIONS,PUT"})))

  (it "throws exception when code is not valid"
    (should-throw
      Exception
      "Invalid code"
      (response/make 0))))

