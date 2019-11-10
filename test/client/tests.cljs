(ns client.tests
  (:require
    [cljs.test :refer-macros [deftest is testing run-tests]]))

(defn greet-visitor [] "Hello world!")


(deftest greeting-visitor
  (is (= "Hello world!" (greet-visitor))))
