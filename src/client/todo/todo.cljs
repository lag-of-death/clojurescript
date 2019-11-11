(ns client.todo.todo
  (:require
    [client.comms :refer [chsk-send!]]
    [client.todo.button :refer [button]]))

(defn handle-click [todo]
  (chsk-send!
   [:todos/mark-as-done {:id (:id todo)}]
   8000))


(def generate-todo-with-on-click
  (fn [on-click-handler todo-data]
    ^{:key (:id todo-data)} [:li.todo
                             {}
                             [:span
                              {:on-click #(on-click-handler todo-data)
                               :class
                               (if (:is-done todo-data)
                                 "todo__done-item"
                                 "todo__new-item")}
                              (:name todo-data)]
                             [button (:id todo-data)]]))

(def generate-todo (partial generate-todo-with-on-click handle-click))
