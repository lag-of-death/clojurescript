(ns client.helpers)

(defn to-keywords [my-map]
  (into {}
        (for [[k v] my-map]
          [(keyword k) v])))
