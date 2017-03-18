(ns client.state
  (:require
    [reagent.core :as reagent]))

(defonce todos (reagent/atom []))
(defonce input-atom (reagent/atom ""))
(defonce filter-todos-by (reagent/atom "all"))