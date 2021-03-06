(ns client.todo.todo
  (:require
    [client.todo.button :refer [button]]))

(defn handle-click [send-fn todo-id]
  (send-fn
   [:todos/mark-as-done todo-id]
   8000))


(def generate-todo-with-on-click
  (fn [on-click-handler send-fn todo-data]
    ^{:key (:id todo-data)} [:li.todo
                             {}
                             [:span
                              {:on-click #(on-click-handler send-fn (:id todo-data))
                               :class
                               (if (:is-done todo-data)
                                 "todo__item todo__item--done"
                                 "todo__item")}
                              (:name todo-data)]
                             [button send-fn (:id todo-data)]]))

(defn generate-todo [send-fn todo-data]
  (generate-todo-with-on-click handle-click send-fn todo-data))
