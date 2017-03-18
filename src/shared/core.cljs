(ns shared.core)

(def filter-out-todo (fn [old-todos id]
                       (filter (fn [todo] (not= (:id todo) id)) old-todos)))


(def cons-todo (fn [old-todos todo] (cons todo old-todos)))

(def map-todo (fn
                [todo-to-mark todo]
                (if
                  (= (:id todo) (:id todo-to-mark))
                  (update todo :is-done #(not (:is-done todo)))
                  todo)))

(def change-todo-status (fn [old-todos todo-to-mark]
                          (map (partial map-todo todo-to-mark) old-todos)))
