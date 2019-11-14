(ns client.todo.core
  (:require
    [client.todo.todos :refer [generate-todos]]
    [cljs.core.async :refer [put!]]
    [client.domain
     :refer
     [todo-input-channel filter-todos-channel]]
    [clojure.string :refer [blank?]]))

(defn change-filter-with-channel [channel filter-by-status]
  (put! channel filter-by-status))

(def change-filter (partial change-filter-with-channel filter-todos-channel))

(defn on-add-btn-clicked-with-channel [todo-name send-fn]
  (send-fn
   [:todos/add {:todo-name todo-name}]
   8000))


(defn on-input-change-with-channel [channel evt]
  (put! channel (-> evt .-target .-value)))

(def on-input-change (partial on-input-change-with-channel todo-input-channel))

(defn app [state send-fn]
  (let [app-state @state]
    [:main.main
     [:div.menu
      [:p.menu__current-tab (:filter-todos-by app-state) " " "todos"]
      [:div.menu__buttons
       [:button.button {:on-click #(change-filter "ALL")} [:span "all"]]
       [:button.button {:on-click #(change-filter "DONE")} [:span "done"]]
       [:button.button {:on-click #(change-filter "TO-DO")} [:span "to-do"]]]]
     [:div.todos-container
      [:div.todos
       [:ul.todos__list
        (case (:filter-todos-by app-state)
              "DONE"   (generate-todos send-fn (filter :is-done (:todos app-state)))
              "ALL"    (generate-todos send-fn (:todos app-state))
              "TO-DO"  (generate-todos send-fn (remove :is-done (:todos app-state)))
              (generate-todos send-fn (:todos app-state)))]]
      [:div.add-area
       [:input.input {:on-change #(on-input-change %)}]
       [:button.button.button--adder
        {:disabled (blank? (:input app-state))
         :on-click #(on-add-btn-clicked-with-channel (:input app-state) send-fn)}
        [:span "add"]]]]]))
