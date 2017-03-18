(ns client.state.changes
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require
    [cljs.core.async :refer [<!]]
    [client.channels :as channels]
    [shared.core :as shared]
    [client.state :as state]))

(defn react-on-changes []
  (go (let [res (<! channels/all-todos-channel)] (reset! state/todos (:body res))))

  (go
    (while true
      (let [res (<! channels/done-todo-channel)] (shared/mark-todo-as-done state/todos (:body res)))))

  (go
    (while true
      (let [res (<! channels/del-todo-channel)] (shared/del-todo state/todos (:body res)))))

  (go
    (while true
      (let [res (<! channels/add-todo-channel)] (shared/add-todo state/todos (js->clj (:body res))))))

  (go
    (while true
      (let [res (<! channels/todo-input-channel)] (reset! state/input-atom res))))

  (go
    (while true
      (let [filter-by-status (<! channels/filter-todos-channel)] (reset! state/filter-todos-by filter-by-status)))))