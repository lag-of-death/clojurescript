(ns shared.domain)

(def filter-out-todo
  (fn [old-todos id]
    (filter (fn [todo] (not= (:id todo) id)) old-todos)))

(def map-todo
  (fn
    [todo-to-mark todo]
    (let []
      (if
        (= (:id todo) (or (:id todo-to-mark) (aget todo-to-mark "id")))
        (update todo :is-done #(not (:is-done todo)))
        todo))))

(def change-todo-status
  (fn [old-todos todo-to-mark]
    (map (partial map-todo todo-to-mark) old-todos)))
