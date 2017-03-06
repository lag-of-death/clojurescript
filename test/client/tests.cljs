(ns client.tests
  (:require
    [client.helpers :refer [greet-visitor]]
    [cljs.test :refer-macros [deftest is testing run-tests]]))

(deftest greeting-visitor
  (is (= "Hello world!" (greet-visitor))))
