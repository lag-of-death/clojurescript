(ns client.state_changes
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require
    [client.helpers :refer [to-keywords]]
    [cljs.core.async :refer [<!]]
    [client.domain :as channels]
    [shared.domain :as shared]))

(defn create-store [state]
  (go
   (let [res (<! channels/all-todos-channel)]
     (swap! state assoc-in [:todos] res)))

  (go
   (while true
          (let [res            (<! channels/done-todo-channel)
                updated-todos  (shared/change-todo-status (:todos @state) res)]
            (swap! state assoc-in [:todos] updated-todos))))

  (go
   (while true
          (let [res (<! channels/del-todo-channel)]
            (swap! state assoc-in [:todos] (shared/filter-out-todo (:todos @state) res)))))

  (go
   (while true
          (let [res (<! channels/add-todo-channel)]
            (swap! state assoc-in [:todos] (cons (to-keywords (js->clj res)) (:todos @state))))))

  (go
   (while true
          (let [res (<! channels/todo-input-channel)] (swap! state assoc-in [:input] res))))

  (go
   (while true
          (let [filter-by-status (<! channels/filter-todos-channel)]
            (swap! state assoc-in [:filter-todos-by] filter-by-status))))

  state)
