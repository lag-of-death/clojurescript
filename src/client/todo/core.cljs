(ns client.todo.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require
    [client.helpers :as helpers]
    [shared.core :refer [del-todo]]
    [cljs-http.client :as http]
    [cljs.core.async :refer [<!]]
    [reagent.core :as reagent]))

(defn on-del-btn-clicked [state todo-id]
  (go
    (let
      [res (<! (http/delete (str "http://localhost:4000/todos/" todo-id)))]
      (del-todo state (:body res)))))

(def todo (fn [on-delete value]
            ^{:key value} [:li (:name value)
                           [:button {:on-click #(on-delete (:id value))} "x"]]))

(defn map-to-lis [state on-delete]
  (let [on-del (partial on-delete state)]
    (-> (partial todo on-del)
        (map @state))))

(defn app [given-state]
  (fn [given-state]
    [:div [:p "TODO example"]
     [:ul (map-to-lis given-state on-del-btn-clicked)]]))
