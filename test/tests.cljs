(ns tests
  (:require [doo.runner :refer-macros [doo-tests]]
            [client.tests]))


(doo-tests 'client.tests)