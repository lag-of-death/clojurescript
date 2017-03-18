(ns client.todo.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require
    [cljs-http.client :as http]
    [client.todo.todos :refer [generate-todos]]
    [cljs.core.async :refer [<! put!]]
    [client.channels :refer [add-todo-channel todo-input-channel filter-todos-channel all-todos-channel]]
    [client.state :refer [filter-todos-by input-atom todos]]
    [clojure.string :refer [blank?]]))

(defn change-filter [filter-by-status]
  (put! filter-todos-channel filter-by-status))

(defn on-add-btn-clicked [todo-name]
  (go
    (let
      [res (<! (http/post "http://localhost:4000/todos" {:json-params {:todo-name todo-name}}))]
      (put! add-todo-channel res))))

(defn on-input-change [input-atom evt]
  (put! todo-input-channel (-> evt .-target .-value)))

(defn app []
  (fn []
    (http/get "http://localhost:4000/todos" {:channel all-todos-channel})
    (fn []
      [:div [:p @filter-todos-by " " "todos"]
       [:div
        [:button {:on-click #(change-filter "all")} [:span "all"]]
        [:button {:on-click #(change-filter "done")} [:span "done"]]
        [:button {:on-click #(change-filter "to-do")} [:span "to-do"]]]
       [:ul (case @filter-todos-by
              "done" (generate-todos (filter (fn [todo] (:is-done todo)) @todos))
              "all" (generate-todos @todos)
              "to-do" (generate-todos (filter (fn [todo] (not (:is-done todo))) @todos))
              (generate-todos @todos))
        ]
       [:div
        [:span "add todo:"]
        [:input {:on-change #(on-input-change input-atom %)}]
        [:button {:disabled (blank? @input-atom) :on-click #(on-add-btn-clicked @input-atom)}
         [:span "add"]]]])))
