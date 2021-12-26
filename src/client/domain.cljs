(ns client.domain
  (:require
    [reagent.core :as reagent]
    [cljs.core.async :refer [chan]]))

(defonce state
  (reagent/atom
   {:todos           []
    :input           ""
    :filter-todos-by "all"}))


(def all-todos-channel (chan))
(def done-todo-channel (chan))

(def add-todo-channel (chan))
(def del-todo-channel (chan))

(def todo-input-channel (chan))

(def filter-todos-channel (chan))
