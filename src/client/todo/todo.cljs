(ns client.todo.todo
  (:require
    [client.todo.button :refer [button]]))

(defn handle-click [send-fn todo-id]
  (send-fn
   [:todos/mark-as-done {:id todo-id}]
   8000))


(def generate-todo-with-on-click
  (fn [on-click-handler send-fn todo-data]
    ^{:key (:id todo-data)} [:li.todo
                             {}
                             [:span
                              {:on-click #(on-click-handler send-fn (:id todo-data))
                               :class
                               (if (:is-done todo-data)
                                 "todo__done-item"
                                 "todo__new-item")}
                              (:name todo-data)]
                             [button send-fn (:id todo-data)]]))

(defn generate-todo [send-fn todo-data]
  (generate-todo-with-on-click handle-click send-fn todo-data))
