(ns server.comms
  (:require
    [server.domain :refer [todos rooms passwords]]
    [taoensso.sente.server-adapters.express :as sente-express]))

(let [packer :edn
      {:keys [ch-recv
              send-fn
              ajax-post-fn
              ajax-get-or-ws-handshake-fn
              connected-uids]}
      (sente-express/make-express-channel-socket-server!
       {:packer        packer
        :csrf-token-fn nil
        :user-id-fn    (fn [ring-req] (or (aget (:body ring-req) "session" "uid") "x:x"))})]

  (def ajax-post ajax-post-fn)
  (def ajax-get-or-ws-handshake ajax-get-or-ws-handshake-fn)
  (def ch-chsk ch-recv)
  (def chsk-send! send-fn)
  (def connected-uids connected-uids))

(add-watch connected-uids :connected-uids
           (fn [_ _ old new]
             (when (not= old new)
                   (do
                     (doseq [uid  (clojure.set/difference (:any old) (:any new))
                             :let [room-and-pass
                                   (.split (or uid "") ":")
                                   room
                                   (aget room-and-pass 0)
                                   pass
                                   (aget room-and-pass 1)]]

                       (swap! rooms disj (keyword room))
                       (swap! passwords dissoc (keyword pass))
                       (swap! todos dissoc (keyword uid)))

                     (js/console.log "Connected uids change: %s" new)))))
