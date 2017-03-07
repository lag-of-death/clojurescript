(ns client.todo.core
  (:require
    [client.helpers :as helpers]
    [reagent.core :as reagent]))

(defonce state (reagent/atom [{:name "Learn ClojureScript" :is-done false}
                              {:name "Write a great app in ClojureScript" :is-done false}]))

(defn map-to-lis [state] (map (fn [value] ^{:key value} [:li (:name value)]) @state))

(defn app [] [:div [:p "TODO example"] [:ul (map-to-lis state)]])
