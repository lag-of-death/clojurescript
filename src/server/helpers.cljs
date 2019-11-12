(ns server.helpers)

(defn get-random-id [] (js/Math.floor (* 100000 (.random js/Math))))
