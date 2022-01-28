(ns server.helpers)

(defn get-random-id [] (js/Math.floor (* 100000 (.random js/Math))))

(defn to-keywords [my-map]
  (into {}
        (for [[k v] my-map]
          {(keyword k) v})))

(defn map-todos [my-map]
  (into {}
        (for [[k v] my-map]
          {(keyword k) (map to-keywords v)})))
