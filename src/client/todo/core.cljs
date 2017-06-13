(ns client.todo.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require
    [cljs-http.client :as http]
    [client.todo.todos :refer [generate-todos]]
    [cljs.core.async :refer [<! put!]]
    [client.channels :refer [add-todo-channel todo-input-channel filter-todos-channel all-todos-channel]]
    [clojure.string :refer [blank?]]))

(defn change-filter-with-channel [channel filter-by-status]
  (put! channel filter-by-status))

(def change-filter (partial change-filter-with-channel filter-todos-channel))

(defn on-add-btn-clicked-with-channel [channel todo-name]
  (go
    (let [res (<!
                (http/post "http://localhost:4000/todos" {:json-params {:todo-name todo-name}}))]
      (put! channel res))))

(def on-add-btn-clicked (partial on-add-btn-clicked-with-channel add-todo-channel))

(defn on-input-change-with-channel [channel evt]
  (put! channel (-> evt .-target .-value)))

(def on-input-change (partial on-input-change-with-channel todo-input-channel))

(defn app [state]
  (let []
    (http/get "http://localhost:4000/todos" {:channel all-todos-channel})
    (fn [state]
      (let [app-state @state]
        [:main
         {:class "main"}
         [:div
          {:class "menu"}
          [:p {:class "menu__current-tab"} (:filter-todos-by app-state) " " "todos"]
          [:div
           {:class "menu__buttons"}
           [:button {:class "button" :on-click #(change-filter "ALL")} [:span "all"]]
           [:button {:class "button" :on-click #(change-filter "DONE")} [:span "done"]]
           [:button {:class "button" :on-click #(change-filter "TO-DO")} [:span "to-do"]]]]
         [:div
          {:class "todos-container"}
          [:div
           {:class "todos"}
           [:ul
            {:class "todos__list"}
            (case (:filter-todos-by app-state)
              "DONE"   (generate-todos (filter :is-done (:todos app-state)))
              "ALL"    (generate-todos (:todos app-state))
              "TO-DO"  (generate-todos (remove :is-done (:todos app-state)))
              (generate-todos (:todos app-state)))]]
          [:div
           {:class "add-area"}
           [:input {:class "input" :on-change #(on-input-change %)}]
           [:button
            {:disabled (blank? (:input app-state))
             :class    "button button--adder"
             :on-click #(on-add-btn-clicked (:input app-state))}
            [:span "add"]]]]]))))
