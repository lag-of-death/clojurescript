(ns client.comms
  (:require [taoensso.sente :as sente]))

(def chsk nil)
(def ch-chsk nil)
(def chsk-send! nil)
(def chsk-state nil)

(defn create-channel []
  (let [{:keys [chsk ch-recv send-fn state]}
        (sente/make-channel-socket! "/chsk"
                                    {:type :auto})]
    (set! client.comms/chsk chsk)
    (set! client.comms/ch-chsk ch-recv)
    (set! client.comms/chsk-send! send-fn)
    (set! client.comms/chsk-state state)))
