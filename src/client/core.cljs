(ns client.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require
    [client.state.changes :refer [create-store]]
    [client.todo.core :as todo]
    [client.state :refer [state]]
    [reagent.core :as reagent]))


(reagent/render [todo/app (create-store state)] (.getElementById js/document "app"))

