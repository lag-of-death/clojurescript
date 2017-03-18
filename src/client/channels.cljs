(ns client.channels
  (:require
    [cljs.core.async :refer [chan]]))

(def all-todos-channel (chan))
(def done-todo-channel (chan))

(def add-todo-channel (chan))
(def del-todo-channel (chan))

(def todo-input-channel (chan))

(def filter-todos-channel (chan))
