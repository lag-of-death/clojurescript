(ns client.tests
  (:require
    [client.todo.core :refer [map-to-lis]]
    [client.helpers :refer [greet-visitor]]
    [cljs.test :refer-macros [deftest is testing run-tests]]))


(deftest greeting-visitor
  (is (= "Hello world!" (greet-visitor))))
