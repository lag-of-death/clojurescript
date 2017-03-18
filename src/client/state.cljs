(ns client.state
  (:require
    [reagent.core :as reagent]))

(defonce state (reagent/atom {:todos           []
                              :input           ""
                              :filter-todos-by "all"}))