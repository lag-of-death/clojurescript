(ns client.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require
    [client.state.changes :refer [react-on-changes]]
    [client.todo.core :as todo]
    [reagent.core :as reagent]))

(react-on-changes)

(reagent/render [todo/app] (.getElementById js/document "app"))

