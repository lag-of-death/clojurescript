(ns client.todo.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require
    [client.helpers :as helpers]
    [shared.core :refer [del-todo add-todo]]
    [cljs-http.client :as http]
    [client.todo.todos :refer [todos]]
    [client.todo.button :refer [button]]
    [cljs.core.async :refer [<!]]
    [clojure.string :refer [blank?]]
    [reagent.core :as reagent]))

(defonce input-atom (reagent/atom ""))
(defonce filter-todos-by (reagent/atom ""))

(defn on-del-btn-clicked [state todo-id]
  (go
    (let
      [res (<! (http/delete (str "http://localhost:4000/todos/" todo-id)))]
      (del-todo state (:body res)))))

(defn show-done-todos [] (swap! filter-todos-by (fn [] "done")))
(defn show-to-do-todos [] (swap! filter-todos-by (fn [] "to-do")))
(defn show-all-todos [] (swap! filter-todos-by (fn [] "all")))

(defn on-add-btn-clicked [state todo-name]
  (go
    (let
      [res (<! (http/post "http://localhost:4000/todos" {:json-params {:todo-name todo-name}}))]
      (add-todo state (js->clj (:body res))))))

(defn on-input-change [input-atom evt] (swap! input-atom (fn [old-val] (-> evt .-target .-value))))

(defn app [given-state]
  (fn [given-state]
    (let [partial-todos (partial todos on-del-btn-clicked)]
      [:div [:p "TODO example"]
       [:div [:button {:on-click show-done-todos} [:span "done"]]]
       [:div [:button {:on-click show-all-todos} [:span "all"]]]
       [:div [:button {:on-click show-to-do-todos} [:span "to-do"]]]
       [:ul (case @filter-todos-by
              "done" (partial-todos given-state (filter (fn [todo] (:is-done todo)) @given-state))
              "all" (partial-todos given-state @given-state)
              "to-do" (partial-todos given-state (filter (fn [todo] (not (:is-done todo))) @given-state))
              (partial-todos given-state @given-state))
        ]
       [:div
        [:span "add todo:"]
        [:input {:on-change #(on-input-change input-atom %)}]
        [:button {:disabled (blank? @input-atom) :on-click #(on-add-btn-clicked given-state @input-atom)}
         [:span "add"]]]])))
