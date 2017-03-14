(ns client.todo.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require
    [client.helpers :as helpers]
    [shared.core :refer [del-todo add-todo]]
    [cljs-http.client :as http]
    [client.todo.todos :refer [todos]]
    [client.todo.button :refer [button]]
    [cljs.core.async :refer [<!]]
    [reagent.core :as reagent]))

(defonce input-atom (reagent/atom ""))

(defn on-del-btn-clicked [state todo-id]
  (go
    (let
      [res (<! (http/delete (str "http://localhost:4000/todos/" todo-id)))]
      (del-todo state (:body res)))))

(defn on-add-btn-clicked [state todo-name]
  (go
    (let
      [res (<! (http/post "http://localhost:4000/todos" {:json-params {:todo-name todo-name}}))]
      (add-todo state (js->clj (:body res))))))

(defn on-input-change [input-atom evt] (swap! input-atom (fn [old-val] (-> evt .-target .-value))))

(defn app [given-state]
  (fn [given-state]
    (let [todos (todos given-state on-del-btn-clicked)]
      [:div [:p "TODO example"]
       [:ul todos]
       [:div
        [:span "add todo:"]
        [:input {:on-change #(on-input-change input-atom %)}]
        [button #(on-add-btn-clicked given-state @input-atom)
         [:span "add"]]]])))
