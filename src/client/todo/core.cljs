(ns client.todo.core
  (:require
    [client.helpers :as helpers]
    [reagent.core :as reagent]))

(defn map-to-lis [state on-delete]
  (map (fn [value] ^{:key value} [:li (:name value) [:button {:on-click #(on-delete (:id value))} "x"]]) @state))

(defn app [given-state on-delete]
  (let [todos given-state]
    (fn [todos on-delete] [:div [:p "TODO example"] [:ul (map-to-lis todos on-delete)]])))
