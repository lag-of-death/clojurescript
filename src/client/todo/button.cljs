(ns client.todo.button)

(defn button
  ([on-delete] [:button {:on-click on-delete} "x"])
  ([on-click child-node] [:button {:on-click on-click} child-node]))
