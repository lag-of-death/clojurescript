(ns shared.core)

(defn del-todo [todos id]
  (swap! todos
         (fn [old-todos]
           (filter (fn [todo] (not= (:id todo) id)) old-todos))) id)

(defn add-todo [todos todo]
  (swap! todos (fn [old-todos] (cons todo old-todos))) todo)

(def map-todo (fn
                [todo-to-mark todo]
                (if
                  (= (:id todo) (:id todo-to-mark))
                  (update todo :is-done #(not (:is-done todo)))
                  todo)))


(defn mark-todo-as-done [todos todo-to-mark]
  (swap! todos
         (fn [old-todos]
           (map (partial map-todo todo-to-mark) old-todos))) todo-to-mark)