(ns client.todo.button)


(defn handle-del-btn-clicked-with-channel [send-fn id]
  (send-fn
   [:todos/mark-as-deleted {:id id}]
   8000))


(defn del-button [on-click todo-id]
  [:button.button.todo_button {:on-click #(on-click todo-id)} "x"])

(defn button [send-fn todo-id]
  (del-button (fn [id] (handle-del-btn-clicked-with-channel send-fn id)) todo-id))
