(ns server.events
  (:require
    [server.comms :refer [chsk-send!]]
    [server.domain :refer [get-todos todos mark-as-done del-todo gen-next-todo]]))

(defmulti event-msg-handler :id)

(defmethod event-msg-handler :default
           [{:keys [event ?reply-fn]}]
           (when ?reply-fn
                 (?reply-fn {:umatched-event-as-echoed-from-from-server event})))


(defmethod event-msg-handler :todos/get-all
           [{:keys [ring-req ?reply-fn]}]
           (let [body    (:body ring-req)
                 session (aget body "session")
                 uid     (aget session "uid")

                 todos   (get-todos uid)]

             (when ?reply-fn (?reply-fn todos))))


(defmethod event-msg-handler :todos/mark-as-done
           [{:keys [?data ring-req]}]
           (let [req-body    (:body ring-req)
                 session     (aget req-body "session")
                 uid         (aget session "uid")

                 todo        (mark-as-done uid ?data)]

             (chsk-send! uid
                         [:todos/marked-as-done (:id todo)])))

(defmethod event-msg-handler :todos/mark-as-deleted
           [{:keys [?data ring-req]}]
           (let [req-body             (:body ring-req)
                 session              (aget req-body "session")
                 uid                  (aget session "uid")

                 deleted-todo-id      (del-todo uid todos (clj->js ?data))]
             (chsk-send! uid
                         [:todos/deleted deleted-todo-id])))


(defmethod event-msg-handler :todos/add
           [{:keys [?data ring-req]}]

           (let [req-body             (:body ring-req)
                 session              (aget req-body "session")
                 uid                  (aget session "uid")

                 next-todo            (gen-next-todo uid todos (clj->js ?data))]

             (chsk-send! uid
                         [:todos/added next-todo])))
