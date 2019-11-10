(ns server.state)

(def todos
  (atom
   [{:name "Learn ClojureScript" :is-done false :id 0}
    {:name "Write a great app in ClojureScript" :is-done false :id 1}]))

(defonce router_ (atom nil))
