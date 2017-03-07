(ns client.tests
  (:require
    [client.todo.core :refer [map-to-lis state]]
    [client.helpers :refer [greet-visitor]]
    [cljs.test :refer-macros [deftest is testing run-tests]]))

(deftest greeting-visitor
  (is (= "Hello world!" (greet-visitor))))


(deftest mapping-state-to-lis
  (is (= '(
            [:li "Learn ClojureScript"]
            [:li "Write a great app in ClojureScript"]) (map-to-lis state))))
