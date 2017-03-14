(ns shared.core)

(defn del-todo [todos id]
  (swap! todos
         (fn [old-todos]
           (filter (fn [todo] (not= (:id todo) id)) old-todos))) id)

(defn add-todo [todos todo]
  (swap! todos (fn [old-todos] (conj old-todos todo))) todo)