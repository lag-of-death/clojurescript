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
  (put! todo-input-channel "")
  (send-fn
   [:todos/add todo-name]
   8000))


(defn on-input-change-with-channel [channel evt]
  (put! channel (-> evt .-target .-value)))

(def on-key-up
  (fn [send-fn evt]
    (when (= 13 (.-keyCode evt))
      (on-add-btn-clicked-with-channel (-> evt .-target .-value) send-fn))))

(def on-input-change
  (fn [evt]
    (on-input-change-with-channel todo-input-channel evt)))

(defn app [state send-fn]
  (let [app-state @state]
    [:main.main
     [:div.menu
      [:p.menu__current-tab (:filter-todos-by app-state) " " "todos"]
      [:div.menu__buttons
       [:button.button.menu__button {:on-click #(change-filter "ALL")} [:span "all"]]
       [:button.button.menu__button {:on-click #(change-filter "DONE")} [:span "done"]]
       [:button.button.menu__button
        {:on-click #(change-filter "TO-DO")}
        [:span "to-do"]]]]
     [:div.todos-container
      [:div.todos
       [:ul.todos__list
        (case (:filter-todos-by app-state)
          "DONE"   (generate-todos send-fn (filter :is-done (:todos app-state)))
          "ALL"    (generate-todos send-fn (:todos app-state))
          "TO-DO"  (generate-todos send-fn (remove :is-done (:todos app-state)))
          (generate-todos send-fn (:todos app-state)))]]
      [:div.add-area
       [:input.input
        {:value     (:input app-state)
         :on-key-up #(on-key-up send-fn %)
         :on-change on-input-change}]
       [:button.button.button--adder
        {:disabled (blank? (:input app-state))
         :on-click #(on-add-btn-clicked-with-channel (:input app-state) send-fn)}
        [:span "+"]]]]]))
