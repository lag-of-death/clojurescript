(ns client.todo.todo
  (:require [client.todo.button :refer [button]]))

(def todo
  (fn [on-delete value]
    (let [button (button (partial on-delete (:id value)))]
      ^{:key (:id value)} [:li (:name value) button])))
