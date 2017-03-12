(ns shared.core)

(defn del-todo [todos id]
  (swap! todos
         (fn [old-todos]
           (filter (fn [todo] (not= (:id todo) id)) old-todos))) id)