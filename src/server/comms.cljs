(ns server.comms
  (:require [taoensso.sente.server-adapters.express :as sente-express]))

(let [packer :edn
      {:keys [ch-recv
              send-fn
              ajax-post-fn
              ajax-get-or-ws-handshake-fn
              connected-uids]}
      (sente-express/make-express-channel-socket-server!
       {:packer        packer
        :csrf-token-fn nil
        :user-id-fn    (fn [ring-req] (aget (:body ring-req) "session" "uid"))})]

  (def ajax-post ajax-post-fn)
  (def ajax-get-or-ws-handshake ajax-get-or-ws-handshake-fn)
  (def ch-chsk ch-recv)
  (def chsk-send! send-fn)
  (def connected-uids connected-uids))
