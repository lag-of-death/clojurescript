(ns client.tests
  (:require
    [cljs.test :refer-macros [deftest is]]))

(defn greet-visitor [] "Hello world!")


(deftest greeting-visitor
  (is (= "Hello world!" (greet-visitor))))
