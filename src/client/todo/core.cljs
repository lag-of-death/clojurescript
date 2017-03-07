(ns client.todo.core
  (:require
    [client.helpers :as helpers]
    [reagent.core :as reagent]))

(defn map-to-lis [state] (map (fn [value] ^{:key value} [:li (:name value)]) @state))

(defn app [given-state]
  (let [todos given-state]
    (fn [todos] [:div [:p "TODO example"] [:ul (map-to-lis todos)]])))
