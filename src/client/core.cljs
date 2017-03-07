(ns client.core
  (:require
    [client.todo.core :as todo]
    [reagent.core :as reagent]))

(reagent/render [todo/app] (.getElementById js/document "app"))