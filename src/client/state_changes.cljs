(ns client.state_changes
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require
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
                body           (:body res)
                jo             (:body res)
                updated-todos  (shared/change-todo-status (:todos @state) body)]
            (js/console.log "jo" jo)
            (swap! state assoc-in [:todos] updated-todos))))

  (go
   (while true
          (let [res (<! channels/del-todo-channel)]
            (swap! state assoc-in [:todos] (shared/filter-out-todo (:todos @state) (:body res))))))

  (go
   (while true
          (let [res (<! channels/add-todo-channel)]
            (swap! state assoc-in [:todos] (cons (js->clj (:body res)) (:todos @state))))))

  (go
   (while true
          (let [res (<! channels/todo-input-channel)] (swap! state assoc-in [:input] res))))

  (go
   (while true
          (let [filter-by-status (<! channels/filter-todos-channel)]
            (swap! state assoc-in [:filter-todos-by] filter-by-status))))

  state)
