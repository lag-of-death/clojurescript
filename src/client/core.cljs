(ns client.core
  (:require
    [client.helpers :as helpers]
    [reagent.core :as reagent]))

(defn app [] [:div [:p (helpers/greet-visitor)]])

(reagent/render [app] (.getElementById js/document "app"))